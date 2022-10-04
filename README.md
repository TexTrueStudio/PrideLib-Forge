<p align="center">
 <img width="100px" src="src/main/resources/assets/flagslib/icon.png" align="center" alt="FlagsLib Logo" />
 <h2 align="center">FlagsLib(PrideLib Unofficial Forge Port)</h2>
 <p align="center">A small JIJ dependency for modders who want to use a centralized data-driven and configurable source of custom flags in their mods. </p>
 <p align="center">
    <a title="Java 17" target="_blank"><img src="https://img.shields.io/badge/language-Java%2017-9B599A.svg?style=flat-square"></a>
    <a title="GitHub license" target="_blank" href="https://github.com/TexTrueStudio/PrideLib-Forge/blob/de6616b2d28304382686a725e71e04835f1cfea6/LICENSE"><img src="https://img.shields.io/github/license/LambdAurora/SpruceUI?style=flat-square"></a>
    <a title="Environment: Client" target="_blank"><img src="https://img.shields.io/badge/environment-client-1976d2?style=flat-square"></a>
    <a title="Mod loader: Forge" target="_blank"><img src="https://img.shields.io/badge/Modloader-Forge-blue?style=flat-square"></a>
</p>

***

FlagsLib is a small JIJ dependency for modders who want to use a centralized data-driven and configurable source of custom flags in their mods.
FlagsLib can then be used to draw randomized graphical elements, or generate other colored visuals. FlagsLib lets flags be added through resource packs, 
optionally part of a mod. Flags used by FlagsLib can be configured either with resource packs, or a config file.

## Including FlagsLib in a Mod (Unable)
```gradle
repositories {
maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}
dependencies {
	modImplementation include("maven.modrinth:flagslib-forge:${flagslib_version}")
}
```
## Configuring Flags
Look [THIS](https://github.com/TexTrueStudio/PrideLib-Forge/wiki) wiki?