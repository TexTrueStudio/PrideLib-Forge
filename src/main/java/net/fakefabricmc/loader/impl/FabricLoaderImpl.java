/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fakefabricmc.loader.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.fakefabricmc.loader.api.FabricLoader;

//import net.fabricmc.api.EnvType;
import net.fakefabricmc.loader.api.ModContainer;
//import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraftforge.api.distmarker.Dist;

@SuppressWarnings("deprecation")
public final class FabricLoaderImpl implements FabricLoader {
    public static final FabricLoaderImpl INSTANCE = InitHelper.get();

    //@Override
    public <T> List<T> getEntrypoints(String key, Class<T> type) {
        return null;
    }

    //@Override
    //public <T> List<EntrypointContainer<T>> getEntrypointContainers(String key, Class<T> type) {
    //    return null;
    //}

    @Override
    public Optional<ModContainer> getModContainer(String id) {
        return Optional.empty();
    }

    @Override
    public Collection<ModContainer> getAllMods() {
        return null;
    }

    @Override
    public boolean isModLoaded(String id) {
        return false;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return false;
    }

    @Override
    public Dist getEnvironmentType() {
        return null;
    }

    @Override
    public Object getGameInstance() {
        return null;
    }

    @Override
    public Path getGameDir() {
        return null;
    }

    @Override
    public File getGameDirectory() {
        return null;
    }

    @Override
    public Path getConfigDir() {
        return null;
    }

    @Override
    public File getConfigDirectory() {
        return null;
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize) {
        return new String[0];
    }

    /**
     * Provides singleton for static init assignment regardless of load order.
     */
    public static class InitHelper {
        private static FabricLoaderImpl instance;

        public static FabricLoaderImpl get() {
            if (instance == null) instance = new FabricLoaderImpl();

            return instance;
        }
    }
}