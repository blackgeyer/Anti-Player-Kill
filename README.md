# AntiPlayerKill (AntiPK)

**AntiPlayerKill** is a Paper/Purpur plugin designed to prevent unauthorized player kills (RDM), enforce punishment rules, and provide system mechanisms for inventory safety and consensual PvP duels.

---

## ⚡ Features

* **Combat & Aggression Detection:** Tracks player hits and damage thresholds within a configurable time window to accurately flag unauthorized aggression.
* **Warning & Punishment System:** 
  * Accumulates warnings for unauthorized kills with dynamic time-based decay.
  * Triggers configurable console punishment commands (e.g., `ban`, `kick`, `tempban`) upon reaching warning thresholds.
  * Supports repeat offense multipliers and custom duration caps.
* **Inventory Dump System:**
  * Automatically saves a snapshot of a player's inventory upon an unauthorized death.
  * Clears dropped items on death to prevent aggressors from stealing loot or exploiting resources.
  * Administrative commands to inspect, list, and restore inventory dumps to victims.
* **Party & Group System:** Allows players to form groups to manage team interactions and group duels.
* **Consensual PvP / Duels:** Enables players and groups to initiate mutually agreed duels, completely bypassing RDM checks and punishments.
* **MiniMessage Formatting:** Full support for rich text and modern color formatting across all system messages.

---

## 📋 Requirements

* **Java:** 21+ 
* **Server Version:** Paper / Purpur 1.20.5+

---

## 🛠️ Commands & Permissions

### Commands

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/antipk reload` | Reloads plugin configuration | `antipk.reload` |
| `/antipk dump <nickname> <dumps \| info \| restore> [page \| id \| confirm]` | Manages and restores player inventory dumps | `antipk.dumps` |
| `/pvp <nickname \| confirm \| deny \| end>` | Manages consensual PvP requests and duels | Everyone |
| `/group <create \| invite \| accept \| exile \| leave \| delete \| info>` | Group and party management commands | Everyone |

### Permissions

* `antipk.bypass.aggression` — Allows players to kill others without being flagged as aggressors (Default: `false`).
* `antipk.reload` — Grants access to `/antipk reload` (Default: `op`).
* `antipk.dumps` — Grants access to `/antipk dump` management commands (Default: `op`).

---

## Default Configuration (not all of it, showed only the most of importants)(`config.yml`)

```yaml
warns:
  warns-is-enabled: true
  warn-msg: "<red>[!] You committed an unauthorized kill. Violation logged: [<white>%warns%/%max_warns%</white>].</red>"
  last-warn-msg: "<dark_red><bold>[WARNING]</bold></dark_red> <red>Maximum limit reached. Any subsequent unauthorized kill will trigger an immediate punishment.</red>"
  static-warn-msg: "<red>[!] You committed an unauthorized kill of a player.</red>"
  warns-msg-is-enabled: true
  warn-is-dynamic: true
  warn-decay-minutes: 180
  warn-limit: 2

actions:
  actions-is-enabled: true
  punishment-command: "ban %player% %duration% %reason%"
  default-reason: "Exceeded maximum RDM violation limit."
  relapse-detect: true
  base-punishment-duration: "24h"
  repeat-multiplier: 2.0
  max-duration: "30d"

combat:
  ttl-seconds: 30
  min-damage-threshold: 4.0
  min-hits-threshold: 2

dumps:
  dump-creating: true
  inv-clear-when-die: true
  clear-time: "7d"
