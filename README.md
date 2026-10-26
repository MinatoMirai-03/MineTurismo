# MineTurismo

MineTurismo is a Minecraft mod that adds drivable cars, motorcycles, trucks, helicopters and rail vehicles with deep customization, AI town traffic and railways, and an in-game smartphone. The Fabric version is available now; Forge and NeoForge versions are in preparation.

> [!IMPORTANT]
> **This project was largely created with AI.** Most of the source code and most of the vehicles in the official add-on "MineTurismo AI Objects" were made using generative AI tools.
> The vehicles in the main mod are different: only 5 of them were made with AI, and most of the others are the work of human creators.

See the [License](#license) before redistributing or modifying the mod.

## Features

- **Vehicles**: cars, convertibles, motorcycles, trucks (trailer coupling and cargo), helicopters and rail vehicles
- **Driving**: drifting, burnouts, wheelies, J-turns, handbrake, towing with a tow rope, and more
- **Customization** (with various wrenches)
  - Paint (many colors and metallic finishes), liveries, license plates (designs from many countries)
  - Lights (headlight color and flashing), police lights and sirens (US/EU/JP patterns), horns
  - Rear wings, taxi signs, brake calipers, exhausts (flame and smoke particles), under-neon
  - Text parts, brand logos, wheel style/color/width/camber, ride height, body width, limousine stretch, and more
- **Traffic**: build roads with waypoints, traffic lights and one-way markers for AI cars (Town Traffic), railways and passenger transport
- **"PopPhone" smartphone**: vehicle dispatch, music player, weather and clock, used car shop, shop, slots, mini-games, and more
- **Other**: breakable objects (tires, cones, barricades), security alarm, ghost lap recording, and more

## Supported Loaders and Versions

| Loader | Status | Minecraft |
|---|---|---|
| Fabric | Available | 1.14.4 – 26.3 |
| Forge | In preparation | To be announced |
| NeoForge | In preparation | To be announced |

- Each Minecraft version and loader has its own jar. Use the jar that matches your setup.
- On older versions, some visuals are simplified because the game lacks the required features (e.g. chrome text is a single color on 1.14.4).

### About the Forge and NeoForge versions

The Forge and NeoForge versions are being prepared for release and will be published in this repository when ready (their source code is not included here until then). Available features and visuals may differ from the Fabric version depending on the loader and Minecraft version. The release date will be announced once decided.

## Installation (Fabric)

Requirements: [Fabric Loader](https://fabricmc.net/) and [Fabric API](https://modrinth.com/mod/fabric-api) (Fabric API 0.42.0 or later on 1.16.2 – 1.16.5)

| Minecraft | Fabric Loader |
|---|---|
| 1.14.4 – 1.21.11 | 0.17.3 or later |
| 26.1, 26.2 | 0.19.3 or later (not tested yet) |
| 26.3 | 0.19.5 or later |

1. Install Fabric Loader and Fabric API.
2. Download `mineturismo-<mod version>+<Minecraft version>.jar` matching your Minecraft version from the download page and put it in your `mods` folder.
3. To use the additional vehicle add-on "MineTurismo AI Objects", also put the add-on jar for the same version in your `mods` folder.

## Bug Reports and Questions

Please use the [Issues](https://github.com/MinatoMirai-03/MineTurismo/issues) page of this repository. It helps to include your Minecraft and mod versions, other installed mods, steps to reproduce, and the log (`logs/latest.log`) or crash report.

## Making Add-ons

You may create and distribute add-ons that depend on this mod (additional vehicles, parts, data, resources, etc.) under the terms of the [License](#license), including selling them.

- `mineturismo-ai-objects/` is the official add-on and serves as an example of how to lay out vehicle models (`.glb` / `.obj`), textures and JSON files.
- License plates (`plates.json`) and brand logos (`logos.json`) are added to the mod's lists just by placing them in your add-on jar.
- Do not include any files of this mod in your add-on, and do not modify this mod's classes (e.g. with Mixins).

## Building from Source (Fabric)

All Minecraft versions are built from a single source tree with [Stonecutter](https://stonecutter.kikugie.dev/) (the base version is 1.21.10).

- Requirements: JDK 25 (Gradle is run through the included Gradle Wrapper)
- Build a single version:

  ```sh
  ./gradlew :1.21.10:remapJar        # output in versions/1.21.10/build/libs/ (use :26.3:jar for 26.x)
  ```

- Build all versions (output in `dist/`; takes a long time and a lot of disk space):

  ```sh
  ./gradlew :1.21.10:collectJars
  ```

- Build comment-free source jars for add-on authors:

  ```sh
  ./gradlew :1.21.10:publicJars      # output in dist/public/
  ```

- The official add-on is built the same way in `mineturismo-ai-objects/`.

Client-side code for versions below 1.18 lives in `src/main/java` as per-version copies (1.18 and later use `src/client/java`).

## License

This mod and its official add-on are provided under the custom "MineTurismo License". Please read [LICENSE](LICENSE) for details. Summary:

| | |
|---|---|
| ✅ Use (single-player and servers) | Allowed |
| ✅ Commercial use (monetized videos/streams, running servers, selling add-ons, etc.) | Allowed, but you must follow the [Minecraft EULA](https://www.minecraft.net/eula) and [Usage Guidelines](https://www.minecraft.net/usage-guidelines) |
| ✅ Creating and distributing add-ons that depend on this mod | Allowed (without including this mod's files, modifying its classes, or copying its source) |
| ✅ Modifying the mod for private use | Allowed |
| ✅ Including in modpacks | Only by referencing the official download page |
| ❌ Redistribution (modified or not) | Not allowed |
| ❌ Distributing modified versions | Not allowed |
| ❌ Bundling the jar in modpacks | Not allowed |
| ❌ Reusing the source code, models, textures, sounds, etc. | Not allowed |

Some 3D vehicle models in the main mod are works by [Lexyc16](https://sketchfab.com/Lexyc16), [Fenrate](https://sketchfab.com/Fenrate) and [iSteven](https://sketchfab.com/OneSteven), licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/) and modified for use in this mod. The MineTurismo License does not apply to them.

Bundled fonts are subject to their own licenses (SIL Open Font License 1.1, see `*_FONT_LICENSE.txt`).

Real car names and company names appearing in this mod are trademarks or registered trademarks of their respective owners. This mod is not affiliated with those companies.

NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.

## Author

[MinatoMirai](https://github.com/MinatoMirai-03)
