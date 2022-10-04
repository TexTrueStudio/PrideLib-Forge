package org.rewrite.game.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class NamespaceResourceManager implements ResourceManager {
    static final Logger LOGGER = LogUtils.getLogger();
    public final List<NamespaceResourceManager.FilterablePack> packList = Lists.newArrayList();
    final ResourceType type;
    private final String namespace;

    public NamespaceResourceManager(ResourceType type, String namespace) {
        this.type = type;
        this.namespace = namespace;
    }

    public void addPack(ResourcePack pack) {
        this.addPack(pack.getName(), pack, (Predicate)null);
    }

    public void addPack(ResourcePack pack, Predicate<Identifier> filter) {
        this.addPack(pack.getName(), pack, filter);
    }

    public void addPack(String name, Predicate<Identifier> filter) {
        this.addPack(name, (ResourcePack)null, filter);
    }

    private void addPack(String name, @Nullable ResourcePack underlyingPack, @Nullable Predicate<Identifier> filter) {
        this.packList.add(new FilterablePack(name, underlyingPack, filter));
    }

    public Set<String> getAllNamespaces() {
        return ImmutableSet.of(this.namespace);
    }

    public Optional<Resource> getResource(Identifier arg) {
        if (!this.isPathAbsolute(arg)) {
            return Optional.empty();
        } else {
            for(int i = this.packList.size() - 1; i >= 0; --i) {
                FilterablePack filterablePack = (FilterablePack)this.packList.get(i);
                ResourcePack resourcePack = filterablePack.underlying;
                if (resourcePack != null && resourcePack.contains(this.type, arg)) {
                    return Optional.of(new Resource(resourcePack.getName(), this.createOpener(arg, resourcePack), this.createMetadataSupplier(arg, i)));
                }

                if (filterablePack.isFiltered(arg)) {
                    LOGGER.warn("Resource {} not found, but was filtered by pack {}", arg, filterablePack.name);
                    return Optional.empty();
                }
            }

            return Optional.empty();
        }
    }

    Resource.InputSupplier<InputStream> createOpener(Identifier id, ResourcePack pack) {
        return LOGGER.isDebugEnabled() ? () -> {
            InputStream inputStream = pack.open(this.type, id);
            return new DebugInputStream(inputStream, id, pack.getName());
        } : () -> {
            return pack.open(this.type, id);
        };
    }

    private boolean isPathAbsolute(Identifier id) {
        return !id.getPath().contains("..");
    }

    public List<Resource> getAllResources(Identifier id) {
        if (!this.isPathAbsolute(id)) {
            return List.of();
        } else {
            List<Entry> list = Lists.newArrayList();
            Identifier identifier = getMetadataPath(id);
            String string = null;
            Iterator var5 = this.packList.iterator();

            while(var5.hasNext()) {
                FilterablePack filterablePack = (FilterablePack)var5.next();
                if (filterablePack.isFiltered(id)) {
                    if (!list.isEmpty()) {
                        string = filterablePack.name;
                    }

                    list.clear();
                } else if (filterablePack.isFiltered(identifier)) {
                    list.forEach(Entry::ignoreMetadata);
                }

                ResourcePack resourcePack = filterablePack.underlying;
                if (resourcePack != null && resourcePack.contains(this.type, id)) {
                    list.add(new Entry(id, identifier, resourcePack, type));
                }
            }

            if (list.isEmpty() && string != null) {
                LOGGER.info("Resource {} was filtered by pack {}", id, string);
            }

            return list.stream().map(Entry::toReference).toList();
        }
    }

    public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        Object2IntMap<Identifier> object2IntMap = new Object2IntOpenHashMap();
        int i = this.packList.size();

        for(int j = 0; j < i; ++j) {
            FilterablePack filterablePack = (FilterablePack)this.packList.get(j);
            filterablePack.removeFiltered(object2IntMap.keySet());
            if (filterablePack.underlying != null) {
                Iterator var7 = filterablePack.underlying.findResources(this.type, this.namespace, startingPath, allowedPathPredicate).iterator();

                while(var7.hasNext()) {
                    Identifier identifier = (Identifier)var7.next();
                    object2IntMap.put(identifier, j);
                }
            }
        }

        Map<Identifier, Resource> map = Maps.newTreeMap();
        ObjectIterator var12 = Object2IntMaps.fastIterable(object2IntMap).iterator();

        while(var12.hasNext()) {
            Object2IntMap.Entry<Identifier> entry = (Object2IntMap.Entry)var12.next();
            int k = entry.getIntValue();
            Identifier identifier2 = (Identifier)entry.getKey();
            ResourcePack resourcePack = ((FilterablePack)this.packList.get(k)).underlying;
            map.put(identifier2, new Resource(resourcePack.getName(), this.createOpener(identifier2, resourcePack), this.createMetadataSupplier(identifier2, k)));
        }

        return map;
    }

    private Resource.InputSupplier<ResourceMetadata> createMetadataSupplier(Identifier id, int index) {
        return () -> {
            Identifier identifier = getMetadataPath(id);

            for(int j = this.packList.size() - 1; j >= index; --j) {
                FilterablePack filterablePack = (FilterablePack)this.packList.get(j);
                ResourcePack resourcePack = filterablePack.underlying;
                if (resourcePack != null && resourcePack.contains(this.type, identifier)) {
                    InputStream inputStream = resourcePack.open(this.type, identifier);

                    ResourceMetadata var8;
                    try {
                        var8 = ResourceMetadata.create(inputStream);
                    } catch (Throwable var11) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable var10) {
                                var11.addSuppressed(var10);
                            }
                        }

                        throw var11;
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                    return var8;
                }

                if (filterablePack.isFiltered(identifier)) {
                    break;
                }
            }

            return ResourceMetadata.NONE;
        };
    }

    private static void applyFilter(FilterablePack pack, Map<Identifier, EntryList> idToEntryList) {
        Iterator<Map.Entry<Identifier, EntryList>> iterator = idToEntryList.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<Identifier, EntryList> entry = (Map.Entry)iterator.next();
            Identifier identifier = (Identifier)entry.getKey();
            EntryList entryList = (EntryList)entry.getValue();
            if (pack.isFiltered(identifier)) {
                iterator.remove();
            } else if (pack.isFiltered(entryList.metadataId())) {
                entryList.entries.forEach(Entry::ignoreMetadata);
            }
        }

    }

    private void findAndAdd(FilterablePack pack, String startingPath, Predicate<Identifier> allowedPathPredicate, Map<Identifier, EntryList> idToEntryList) {
        ResourcePack resourcePack = pack.underlying;
        if (resourcePack != null) {
            Iterator var6 = resourcePack.findResources(this.type, this.namespace, startingPath, allowedPathPredicate).iterator();

            while(var6.hasNext()) {
                Identifier identifier = (Identifier)var6.next();
                Identifier identifier2 = getMetadataPath(identifier);
                ((EntryList)idToEntryList.computeIfAbsent(identifier, (id) -> {
                    return new EntryList(identifier2, Lists.newArrayList());
                })).entries().add(new Entry(identifier, identifier2, resourcePack, type));
            }

        }
    }

    public Map<Identifier, List<Resource>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        Map<Identifier, EntryList> map = Maps.newHashMap();
        Iterator var4 = this.packList.iterator();

        while(var4.hasNext()) {
            FilterablePack filterablePack = (FilterablePack)var4.next();
            applyFilter(filterablePack, map);
            this.findAndAdd(filterablePack, startingPath, allowedPathPredicate, map);
        }

        TreeMap<Identifier, List<Resource>> treeMap = Maps.newTreeMap();
        map.forEach((id, entryList) -> {
            treeMap.put(id, entryList.toReferenceList());
        });
        return treeMap;
    }

    public Stream<ResourcePack> streamResourcePacks() {
        return this.packList.stream().map((pack) -> {
            return pack.underlying;
        }).filter(Objects::nonNull);
    }

    static Identifier getMetadataPath(Identifier id) {
        return new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
    }

    private static record FilterablePack(String name, @Nullable ResourcePack underlying, @Nullable Predicate<Identifier> filter) {
        //FilterablePack(String f_215432_, @Nullable ResourcePack f_215433_, @Nullable Predicate<Identifier> f_215434_) {
        //    this.name = f_215432_;
        //    this.underlying = f_215433_;
        //    this.filter = f_215434_;
        //}

        public void removeFiltered(Collection<Identifier> ids) {
            if (this.filter != null) {
                ids.removeIf(this.filter);
            }

        }

        public boolean isFiltered(Identifier id) {
            return this.filter != null && this.filter.test(id);
        }

        public String name() {
            return this.name;
        }

        @Nullable
        public ResourcePack underlying() {
            return this.underlying;
        }

        @Nullable
        public Predicate<Identifier> filter() {
            return this.filter;
        }
    }

    public class Entry {
        private final Identifier id;
        private final Identifier metadataId;
        private final ResourcePack pack;
        private boolean checksMetadata = true;
        private final ResourceType type;

        public Entry(Identifier id, Identifier metadataId, ResourcePack pack, ResourceType type) {
            this.pack = pack;
            this.id = id;
            this.metadataId = metadataId;
            this.type = type;
        }

        public void ignoreMetadata() {
            this.checksMetadata = false;
        }

        public Resource toReference() {
            String string = this.pack.getName();
            return this.checksMetadata ? new Resource(string, this.createOpener(this.id, this.pack), () -> {
                if (this.pack.contains(this.type, this.metadataId)) {
                    InputStream inputStream = this.pack.open(this.type, this.metadataId);

                    ResourceMetadata var2;
                    try {
                        var2 = ResourceMetadata.create(inputStream);
                    } catch (Throwable var5) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable var4) {
                                var5.addSuppressed(var4);
                            }
                        }

                        throw var5;
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                    return var2;
                } else {
                    return ResourceMetadata.NONE;
                }
            }) : new Resource(string, this.createOpener(this.id, this.pack));
        }

        private Resource.InputSupplier<InputStream> createOpener(Identifier id, ResourcePack pack) {
            return null;
        }
    }


    private static record EntryList(Identifier metadataId, List<Entry> entries) {
        //EntryList(Identifier f_215420_, List<Entry> f_215421_) {
        //    this.metadataId = f_215420_;
        //    this.entries = f_215421_;
        //}

        List<Resource> toReferenceList() {
            return this.entries().stream().map(Entry::toReference).toList();
        }

        public Identifier metadataId() {
            return this.metadataId;
        }

        public List<Entry> entries() {
            return this.entries;
        }
    }

    private static class DebugInputStream extends FilterInputStream {
        private final String leakMessage;
        private boolean closed;

        public DebugInputStream(InputStream parent, Identifier id, String packName) {
            super(parent);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            (new Exception()).printStackTrace(new PrintStream(byteArrayOutputStream));
            this.leakMessage = "Leaked resource: '" + id + "' loaded from pack: '" + packName + "'\n" + byteArrayOutputStream;
        }

        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                NamespaceResourceManager.LOGGER.warn(this.leakMessage);
            }

            super.finalize();
        }
    }


}
