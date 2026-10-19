# Version splits

One source tree, two jars (26.1/26.2 and 26.3). Add new finds here.

## 26.3 is a separate jar

`./gradlew build` produces `mobesp-X.Y.Z+26.1.jar` (26.1.x and 26.2, `minecraft >=26.1 <26.3`). `./gradlew build -PmcTarget=26.3`
produces `mobesp-X.Y.Z+26.3.jar` (pins in `gradle-26.3.properties`, `minecraft >=26.3`). Files under `src/mc26_3/java` and
`src/mc26_3/resources` replace the same-named file of `src/main`; `src/mc26_3/remove.txt` lists main files the 26.3 jar drops.
`verifyMixinClasses` (part of `check`) fails when a mixins.json entry has no class in that variant. Check each jar against the
Minecraft versions it claims without starting the game:
`python C:\Users\Rasmus\.claude\skills\minecraft-mods\scripts\linkcheck.py <jar> <mapped minecraft jar>`.

| What | 26.1 | 26.2 | 26.3 | Fix |
|---|---|---|---|---|
| Current screen, set screen | field `Minecraft.screen`, `Minecraft.setScreen` | `Minecraft.gui.screen()`, `Minecraft.gui.setScreen` | same as 26.2 | `util/ScreenCompat` resolves by name (26.1 jar); direct calls in the `src/mc26_3` overlay |
| Key codes | `InputConstants.KEY_ESCAPE` = 256 (GLFW) | same | `KEY_ESCAPE` = 41 (SDL scancode); `KeyEvent.key()` returns it | compare `event.key()` with `InputConstants.KEY_*`, never a number; the constant is inlined per variant |
| Default key binding | `KeyMapping(name, Type.KEYSYM, 77, cat)` | same | `Type.KEYSYM` gone (`KEYBOARD`), key ids are scancodes | `new KeyMapping(name, InputConstants.KEY_M, cat)` (3 arguments exist on all three) |

Full 26.3 table: `C:\Users\Rasmus\Desktop\Ny mappe (3)\mc-26.3-splits.md`.
