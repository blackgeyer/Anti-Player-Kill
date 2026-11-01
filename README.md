# Anti Player Kill (AntiPK)

![License](https://img.shields.io/badge/License-GPLv3-blue.svg)
![Folia Supported](https://img.shields.io/badge/Folia-Supported-brightgreen.svg)

> **Author / Автор:** blackgeyer (also known as BlackGeier) | **Version / Версия:** 1.1.0 | **License / Лицензия:** GPLv3  
> **Plugin Showcase / Видео с демонстрацией:** [Watch on Odysee](https://odysee.com/@blackgeyer:7/anti-player-kill-minecraft-plugin---001:6?r=FigUHodafNMPFnT1U4KyPvSuhe935aCt)

> **Discord**: https://discord.gg/TvgqnJNKXW

English
**Anti Player Kill** is a Paper/Folia plugin designed to prevent unauthorized player kills (RDM), enforce punishment rules, and provide system mechanisms for inventory safety and consensual PvP duels.

---

## Features

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

## Requirements

* **Java:** 21+ 
* **Server Version:** Spigot / Paper / Purpur etc. any bukkit-based core or Folia 1.20.5+
* **Adventure API** (If your core doesn't have Adventure API built in)

---

## Commands & Permissions

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

Русский
**Anti Player Kill** — плагин для Paper/Folia, предотвращающий неразрешённые убийства игроков (RDM), обеспечивающий соблюдение правил наказаний и предоставляющий механизмы сохранности инвентаря и добровольных PvP-дуэлей.

---

## Возможности

* **Обнаружение боя и агрессии:** Отслеживает удары и пороги урона в настраиваемом временном окне, чтобы точно определять неразрешённую агрессию.
* **Система предупреждений и наказаний:** 
  * Накапливает предупреждения за неразрешённые убийства с динамическим затуханием по времени.
  * Выполняет настраиваемые консольные команды наказаний (например, `ban`, `kick`, `tempban`) при достижении порога предупреждений.
  * Поддерживает множители за повторные нарушения и пользовательские ограничения длительности.
* **Система дампов инвентаря:**
  * Автоматически сохраняет снимок инвентаря игрока при неразрешённой смерти.
  * Очищает выпадающие предметы при смерти, чтобы агрессоры не могли украсть добычу или злоупотребить ресурсами.
  * Административные команды для просмотра, получения списка и восстановления дампов инвентаря жертвам.
* **Система групп и пати:** Позволяет игрокам создавать группы для управления командным взаимодействием и групповыми дуэлями.
* **Добровольное PvP / дуэли:** Позволяет игрокам и группам начинать дуэли по обоюдному согласию, полностью обходя проверки RDM и наказания.
* **Форматирование MiniMessage:** Полная поддержка расширенного текста и современного цветового форматирования во всех системных сообщениях.

---

## Требования

* **Java:** 21+ 
* **Версия сервера:** Spigot / Paper / Purpur и т. д., любое ядро на базе Bukkit или Folia 1.20.5+
* **Adventure API** (если в вашем ядре нет встроенного Adventure API)

---

## Команды и права

### Команды

| Команда | Описание | Право |
| :--- | :--- | :--- |
| `/antipk reload` | Перезагружает конфигурацию плагина | `antipk.reload` |
| `/antipk dump <nickname> <dumps \| info \| restore> [page \| id \| confirm]` | Управление дампами инвентаря игроков и их восстановление | `antipk.dumps` |
| `/pvp <nickname \| confirm \| deny \| end>` | Управление запросами на добровольное PvP и дуэлями | Все |
| `/group <create \| invite \| accept \| exile \| leave \| delete \| info>` | Команды управления группами и пати | Все |

### Права

* `antipk.bypass.aggression` — позволяет игрокам убивать других без пометки как агрессоров (по умолчанию: `false`).
* `antipk.reload` — даёт доступ к `/antipk reload` (по умолчанию: `op`).
* `antipk.dumps` — даёт доступ к командам управления `/antipk dump` (по умолчанию: `op`).

