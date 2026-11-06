# CubeRunner 2.5.2 → Paper 26.3 port

The original plugin is CubeRunner 2.5.2 by **PoutineQc**, written for Bukkit/Spigot 1.8–1.12 and licensed under the
GNU LGPL (see `LICENSE`). This port runs it on **Paper 26.3** (Java 25).

## Building

```
./gradlew build          # → build/libs/CubeRunner-<version>.jar
```

Gradle downloads the Java 25 toolchain itself. Versions live in `gradle.properties`
(`paper_version`, `worldedit_version`, `vault_version`, `plugin_version`).

## Requirements

| | |
|---|---|
| Server | Paper 26.3 (uses Paper API: Adventure components, `PlayerProfile`, plugin meta) |
| WorldEdit 7 or FastAsyncWorldEdit | optional; needed to set an arena's zone (`/cr setzone`) |
| Vault + an economy plugin | optional; only with `economyRewards: true` |
| MySQL | optional; flat files (`arenaData.yml`, `playerData.yml`, `signData.yml`) are the default |

## Layout

| Path | What it is |
|---|---|
| `src/main/java/me/poutineqc/cuberunner/` | Plugin sources (moved from `src/`) |
| `src/main/resources/` | `plugin.yml`, `config.yml`, `achievements.yml`, `LanguageFiles/` (moved from the repo root) |
| `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties` | Gradle build, replacing the old Eclipse project |

## API changes handled

- **Flattening (1.13).** Wool and stained clay lost their data values. `utils/ColorBlocks` maps the arena's color mask
  (`colorIndice`) to the 16 `*_WOOL` and 16 `*_TERRACOTTA` materials. The bits keep the legacy data-value order
  (white … black, the same as `DyeColor`), so existing `arenaData.yml` / MySQL arenas keep their colors.
  GUI icons use the flattened materials:

  | 1.8–1.12 | 26.3 |
  |---|---|
  | `STAINED_GLASS_PANE:10` | `PURPLE_STAINED_GLASS_PANE` |
  | `INK_SACK:10` / `:8` / `:12` | `LIME_DYE` / `GRAY_DYE` / `LIGHT_BLUE_DYE` |
  | `WOOL:5` / `:8` | `LIME_WOOL` / `LIGHT_GRAY_WOOL` |
  | `EMPTY_MAP` | `MAP` |
  | `BANNER` + base color in meta | `<COLOR>_BANNER` |
  | `SKULL_ITEM:3` + owner name | `PLAYER_HEAD` + name-only `PlayerProfile` |
  | `Enchantment.DURABILITY` | `Enchantment.UNBREAKING` |

- **Banner patterns** use the 1.20.5 names: `RHOMBUS_MIDDLE` → `RHOMBUS`, `CIRCLE_MIDDLE` → `CIRCLE`,
  `HALF_VERTICAL_MIRROR` → `HALF_VERTICAL_RIGHT`, `HALF_HORIZONTAL_MIRROR` → `HALF_HORIZONTAL_BOTTOM`,
  `DIAGONAL_RIGHT_MIRROR` → `DIAGONAL_UP_RIGHT`.
- **NMS → Bukkit API.**
  - The falling cubes were `EntityFallingBlock`s built with reflection (`Block.getById`, `fromLegacyData`, `motX`, …).
    `game/FallingCubes` spawns a `FallingBlock` with the 1.8 anvil damage values instead (2 damage per block fallen, max 40).
  - The UUID of the player a cube was aimed at moved from the custom name to the persistent data container.
  - Titles use `Player#showTitle` instead of `PacketPlayOutTitle`. The NMS version sniffing is gone.
- **WorldEdit 6 → 7.** `Selection` was removed. `game/WorldEditSelection` reads the session's `Region`. It lives in its
  own class, so the plugin loads without WorldEdit and just tells admins to install it.
- **Two-sided signs (1.20).** `utils/SignLines` reads and writes the front side as components. Only front-side edits
  create a CubeRunner sign.
- **Text.** Messages stay legacy `&`/`§` strings internally, so the language files and `config.yml` are unchanged.
  They become Adventure components at the API boundary (`Utils.text`). `org.bukkit.ChatColor` is replaced by
  `utils/LegacyColor`.
- **Other deprecations:** `Criteria.DUMMY` objectives with component titles, `getPluginMeta()`, the `MAX_HEALTH`
  attribute, component inventory titles, `Sound.UI_BUTTON_CLICK`.
- **Removed:** MCStats metrics (the service is offline) and the Spigot update checker (it pointed at the original
  resource). The `checkForUpdates` config option is gone.
- **MySQL: prepared statements.** Every query is a `PreparedStatement` with its values bound as parameters
  (`MySQL.update(sql, params...)`, `MySQL.query(sql, rowHandler, params...)`), and statements and result sets are
  closed. Before, values were concatenated into the SQL and statements leaked. Table names can't be parameters, so
  `tablePrefix` is limited to letters, digits and underscores (anything else falls back to `cuberunner_` with a
  warning). Column names come only from the `CRStats` enum. Stats are bound with their real types instead of as strings.
- **plugin.yml:** `api-version: '26.3'` and `softdepend: [WorldEdit, FastAsyncWorldEdit, Vault]`. The duplicated
  `amountplayer.minplayer` key is fixed (the second one was meant to be `maxplayer`; modern SnakeYAML rejects duplicate keys).

## Bugs fixed along the way

- Bit 31 of the color mask (black terracotta) could not be toggled, because `(int) Math.pow(2, 31)` saturates.
- The MySQL port was read from the `host` key.
- Changing the min/max players left the old sidebar line behind.
- Eliminating a hiding player during the block shower could throw a `ConcurrentModificationException`.
- Clicking an empty slot in a CubeRunner GUI threw an NPE, and so did a kill credit for a player who had already left.
- Any arena or player name containing an apostrophe broke its MySQL queries (and allowed SQL injection).

## Verified

- Compiles against `paper-api 26.3.build.244-stable` with no errors or deprecation warnings.
- Loads, enables, writes its default files, and disables cleanly on Paper 26.3 build 244.
- A throwaway probe plugin on that server checked the following:
  - all 32 color blocks resolve;
  - every banner pattern and player heads round-trip through `ItemStack`;
  - a falling cube spawns with the expected block data, damage settings, no-drop flag, target tag and push.

- MySQL storage was tested on MySQL 8.4 (Docker) with Paper's bundled driver:
  - the tables are created on first start;
  - an arena named `it's a trap` loads and saves its color mask (including bit 31);
  - player stats update, and a player renamed to `O'Brien` is stored correctly;
  - an orphaned sign row is deleted;
  - a player named `x'); DROP TABLE canary; --` is stored literally, and the canary table survives.

## Not verified yet / known limitations

- **No in-game playtest yet.** It still needs checking that cubes land and crush players, along with the GUIs, signs,
  achievements and Vault rewards.
- WorldEdit 7.4.6 is the compile target. Use whichever WorldEdit/FAWE build supports your server version.
