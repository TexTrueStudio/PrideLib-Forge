<p align="center">
 <img width="100px" src="src/main/resources/assets/pride/icon.png" align="center" alt="PrideLib-Forge port(Unofficial) Logo" />
 <h2 align="center">PrideLib-Forge port (Unofficial)</h2>
 <p align="center">A small JIJ dependency for modders who want to use a centralized data-driven and configurable source of pride flags in their mods. </p>
 <p align="center">
    <a title="Java 17" target="_blank"><img src="https://img.shields.io/badge/language-Java%2017-9B599A.svg?style=flat-square"></a>
    <a title="GitHub license" target="_blank" href="https://github.com/TexTrueStudio/PrideLib-Forge/blob/de6616b2d28304382686a725e71e04835f1cfea6/LICENSE"><img src="https://img.shields.io/github/license/LambdAurora/SpruceUI?style=flat-square"></a>
    <a title="Environment: Client" target="_blank"><img src="https://img.shields.io/badge/environment-client-1976d2?style=flat-square"></a>
    <a title="Mod loader: Forge" target="_blank"><img src="https://img.shields.io/badge/Modloader-Forge-blue?style=flat-square"></a>
</p>

PrideLib-Forge is a small JIJ dependency for modders who want to use a centralized data-driven and configurable source of pride flags in their mods. 
PrideLib can then be used to draw randomized graphical elements, or generate other colored visuals. PrideLib lets flags be added through resource packs, 
optionally part of a mod. Flags used by PrideLib can be configured either with resource packs, or a config file.

## Including PrideLib in a Mod (Unable)
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
	modImplementation include("maven.modrinth:pridelib-forge:${pridelib_version}")
}
```

## Adding Flags
Flags are json files located at `assets/[namespace]/flags/[name].json`. The namespace is unimportant, the name of the file is its ID, and only one of each ID will be loaded. Flags have a shape (which if omitted will default to `pride:horizontal_stripes`) and a set of colors. The following example is the trans flag.
```json
{
	"shape": "horizontal_stripes",
	"colors": [
		"#55cdfc",
		"#f7a8b8",
		"#ffffff",
		"#f7a8b8",
		"#55cdfc"
	]
}
```

## Configuring Flags
Sometimes you don't actually want all of the available flags to be used in game. In this scenario, you'd use a flag configuration file. These are located in 2 different places, either `assets/pride/flags.json` for resource packs, or `config/pride.json` for a config file. Config files have a higher priority than resource pack ones. They use the same format, but the scenario where you might use one over the other may vary. Here is an example limiting available flags to only the trans flag, even if other flags are registered.
```json
{
	"flags": [
		"transgender"
	]
}
```

## Default Flags
In order to provide a small amount of structure, PrideLib comes bundled with a small handful of common flags.

| ID | Description |
| --- | --- |
| `asexual` | Asexual pride |
| `bisexual` | Bisexual pride |
| `intersex` | Intersex pride |
| `lesbian` | Lesbian pride |
| `nonbinary` | Non-binary pride |
| `pansexual` | Pansexual pride |
| `rainbow` | Rainbow/queer/gay/umbrella pride |
| `transgender` | Transgender pride |

## Using in Code
Flags can be obtained from the class `PrideFlags`, it also provides helper methods for selecting a random flag. The returned `PrideFlag` type also offers a `render` method for quick 2D rendering in a GUI or similar. For more complex usage, you can retrieve the raw data. **Note that if no flags are available for whatever reason, getRandomFlag will return `null`**.

Since you may particularly want to make use of this library during Pride Month (i.e. June), there is also a utility method offered in `PrideFlags`: `isPrideMonth`. This also checks for a system property, `everyMonthIsPrideMonth`, which if it is set to `true` the method will always return `true`. You can do this by adding this to your JVM arguments: `-DeveryMonthIsPrideMonth=true`

## Built-in Flag Shapes
The following flag shapes are built-in to PrideLib. Any mod can contribute additional shapes by registering them with `PrideFlagShapes`.

## horizontal_stripes
A basic flag consisting of an arbitrary number of equally-sized colored stripes. Each color is a stripe. The first color is the top stripe, the last is the bottom stripe.

You can accomplish non-equally-sized stripes by repeating the same color multiple times.

Examples: rainbow flag, trans flag, pan flag, nonbinary flag, many more

## vertical_stripes
A basic flag consisting of an arbitrary number of equally-sized colored stripes. Each color is a stripe. The first color is the left stripe, the last is the right stripe.

You can accomplish non-equally-sized stripes by repeating the same color multiple times.

Examples: androgynous flag (not built-in)

## circle
A flag with a solid background and a hollow circle in the foreground. The first color is the background, the second color is the color of the circle. Additional colors are ignored.

Examples: intersex flag

## arrow
A flag with a triangle pointing to the right on the left side of the flag. The first color is the color of the triangle, the rest are treated as in horizontal_stripes and make up the background.

Examples: demisexual flag (not built-in)

## progress
Something very close to the "Progress" pride flag design. Hardcoded due to its unique shape; ignores all colors.

Not provided by any built-in flags; you can add this JSON file to provide it:

```json
{
	"shape": "progress",
	"colors": []
}
```

