# Mob ESP

Client-only Fabric mod that gives mobs a glow outline (ESP). GitHub RasmusKD/Mob-Esp, branch main. Deliberately not on Modrinth; listed on CurseForge only (ESP carries moderation risk).

Read the `minecraft-mods` skill first (`C:\Users\Rasmus\.claude\skills\minecraft-mods\SKILL.md`). It holds the cross-mod rules: dual-version builds, release flow, CurseForge/Modrinth, testing in instances, config and icon style. Memory note for this mod: `curseforge-listings.md`.

## Build

- Java 25: `JAVA_HOME=C:\Users\Rasmus\.jdks\jdk-25.0.3+9`, then `./gradlew build`. The jar is written to `build/libs/`.
- Two jars: `./gradlew build` (26.1.x and 26.2, `mobesp-X.Y.Z+26.1.jar`) and `./gradlew build -PmcTarget=26.3` (`+26.3`); run `clean` between them. See `VERSIONS.md`.
- No unit tests; the check is a clean build plus a run in a Modrinth App instance (see the skill).
- It leaves tropical fish to Rare Fish Finder when that mod is installed. The ESP only ever votes yes on glow. Cache nothing from an ItemStack during a component rebind (it throws).

## Release

No workflow. Git source and the CurseForge 2.4.3 jar match from branch `mc-26.3` on (the empty-scan fix was rebuilt from the jar, bytecode identical for `MobTypes` and `SpawnEggRenderer`); `main` still has 2.4.2. Check `git log` and `mod_version` in `gradle.properties` before assuming a jar matches. Do not publish unless Rasmus asks.

## Keep skills and docs current (same rule as Nordvec)

When you learn something new, update the doc the next agent reads in the same change: this file for build and release facts, `README.md` for user-facing behavior, and the `minecraft-mods` skill for cross-mod lessons. Correct or delete outdated text instead of adding a contradicting note. Then run `python C:\Users\Rasmus\.claude\skills\minecraft-mods\scripts\check_citations.py`.

No em or en dashes in any text. Commit messages describe the code change.
