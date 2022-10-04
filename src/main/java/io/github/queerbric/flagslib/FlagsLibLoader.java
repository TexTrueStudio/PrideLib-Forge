package io.github.queerbric.flagslib;

import com.google.gson.Gson;
import net.fakefabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

public class FlagsLibLoader implements SimpleResourceReloadListener<List<FlagsLibFlag>> {
	private static final Identifier ID = new Identifier("flagslib", "flags");
	private static final Logger LOGGER = LogManager.getLogger("flagslib");
	private static final Gson GSON = new Gson();
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

	static class Config {
		String[] flags;
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	//@Override
	public CompletableFuture<List<FlagsLibFlag>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> loadFlags(manager));
	}

	@Override
	public CompletableFuture<Void> apply(List<FlagsLibFlag> list, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> applyFlags(list));
	}

	public static List<FlagsLibFlag> loadFlags(ResourceManager manager) {
		var flags = new ArrayList<FlagsLibFlag>();

		outer:
		for (var entry : manager.findResources("flags", path -> path.getPath().endsWith(".json")).entrySet()) {
			Identifier id = entry.getKey();
			String[] parts = id.getPath().split("/");
			String name = parts[parts.length - 1];
			name = name.substring(0, name.length() - 5);

			try (var reader = new InputStreamReader(entry.getValue().getInputStream())) {
				FlagsLibFlag.Properties builder = GSON.fromJson(reader, FlagsLibFlag.Properties.class);

				for (String color : builder.colors) {
					if (!HEX_COLOR_PATTERN.matcher(color).matches()) {
						LOGGER.warn("[flagslib] Malformed flag data for flag " + name + ", " + color
								+ " is not a valid color, must be a six-digit hex color like #FF00FF");
						continue outer;
					}
				}

				var flag = new FlagsLibFlag(name, builder);
				flags.add(flag);
			} catch (Exception e) {
				LOGGER.warn("[flagslib] Malformed flag data for flag " + name, e);
			}
		}

		var prideFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "flagslib.json");
		if (prideFile.exists()) {
			try (var reader = new FileReader(prideFile)) {
				Config config = GSON.fromJson(reader, Config.class);

				if (config.flags != null) {
					List<String> list = Arrays.asList(config.flags);
					flags.removeIf(flag -> !list.contains(flag.getId()));
				}
			} catch (Exception e) {
				LOGGER.warn("[flagslib] Malformed flag data for flagslib.json config");
			}
		} else {
			var id = new Identifier("flagslib", "flags.json");

			Optional<Resource> resource = manager.getResource(id);
			if (resource.isPresent()) {
				try (var reader = new InputStreamReader(resource.get().getInputStream())) {
					Config config = GSON.fromJson(reader, Config.class);

					if (config.flags != null) {
						List<String> list = Arrays.asList(config.flags);
						flags.removeIf(flag -> !list.contains(flag.getId()));
					}
				} catch (Exception e) {
					LOGGER.warn("[flagslib] Malformed flag data for flags.json", e);
				}
			}
		}

		return flags;
	}

	private static void applyFlags(List<FlagsLibFlag> flags) {
		FlagsLibFlags.setFlags(flags);
	}
}
