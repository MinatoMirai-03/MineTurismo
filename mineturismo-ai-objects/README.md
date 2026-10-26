# MineTurismo AI Objects

The official additional vehicle add-on for MineTurismo (requires MineTurismo).
It can be used as an example of how to lay out vehicle models, textures and JSON files when making an add-on.

Most of the vehicles in this add-on, as well as most of the source code of the project, were made using generative AI tools.

## Building

1. Build the main mod first (run `./gradlew :1.21.10:remapJar` in the parent directory of this folder).
2. Build the same version in this folder:

   ```sh
   ./gradlew :1.21.10:remapJar      # use :26.3:jar for 26.x
   ```

   The jar is written to `dist/`.

This add-on is licensed under the same "MineTurismo License" as the main mod (see [LICENSE](../LICENSE) in the repository root).
