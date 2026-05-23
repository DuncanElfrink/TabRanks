# 🎮 TabRanks Plugin

Mooie TABlist voor Minecraft met gekleurde namen en aanpasbare rangen.

## ✨ Functies

- Mooie TABlist met header en footer
- 12 ingebouwde rangen met kleuren en prefixen
- Rangen worden opgeslagen (ook na herstart)
- Tab-completie bij commando's
- Volledig aanpasbaar via `config.yml`

---

## 📦 Installatie

### Bouwen met Maven
```bash
cd TabRanks
mvn clean package
```
Het `.jar` bestand staat daarna in de `target/` map.

### Installeren
1. Kopieer `TabRanks-1.0.0.jar` naar je `plugins/` map
2. Start/herstart de server
3. Pas `plugins/TabRanks/config.yml` aan naar wens

**Vereiste:** Paper of Spigot 1.20+, Java 17+

---

## 🎖️ Rangen (standaard)

| Rang         | Prefix                  | Kleur     |
|--------------|-------------------------|-----------|
| `owner`      | `[OWNER]`               | Rood      |
| `admin`      | `[ADMIN]`               | Donkerrood|
| `developer`  | `[DEV]`                 | Groen     |
| `moderator`  | `[MOD]`                 | Blauw     |
| `helper`     | `[HELPER]`              | Aqua      |
| `builder`    | `[BUILDER]`             | Grijs     |
| `streamer`   | `[▶ STREAMER]`          | Roze      |
| `youtuber`   | `[▶ YOUTUBE]`           | Rood      |
| `mvp`        | `[MVP]`                 | Geel      |
| `vip_plus`   | `[VIP+]`                | Goud      |
| `vip`        | `[VIP]`                 | Goud      |
| `player`     | `[SPELER]`              | Wit       |

---

## 💬 Commando's

| Commando                  | Beschrijving                        | Permissie         |
|---------------------------|-------------------------------------|-------------------|
| `/rank <speler> <rang>`   | Stel de rang van een speler in      | `tabranks.admin`  |
| `/rankinfo`               | Bekijk jouw eigen rang              | Iedereen          |
| `/ranklist`               | Lijst van alle rangen               | Iedereen          |

### Voorbeelden
```
/rank Notch owner
/rank Steve vip_plus
/rank Alex streamer
/ranklist
```

---

## 🔐 Permissies

| Permissie             | Standaard | Beschrijving                    |
|-----------------------|-----------|---------------------------------|
| `tabranks.admin`      | OP        | Rangen instellen via commando   |
| `tabranks.rank.owner` | false     | Automatisch owner-rang via perm |
| `tabranks.rank.vip`   | false     | Automatisch vip-rang via perm   |
| *(enzovoort...)*      |           |                                 |

> **Tip:** Als je LuckPerms gebruikt, kun je rangen ook via permissions toewijzen.
> Handmatige rangen (via `/rank`) gaan voor op permission-rangen.

---

## ⚙️ Configuratie (`config.yml`)

```yaml
tablist:
  header: "&6&l✦ &e&lMijnServer &6&l✦\n&7Welkom op onze server!"
  footer: "&7Online: &e{online}&7/&e{max}\n&8discord.mijnserver.nl"
  update-interval: 40   # in ticks (20 ticks = 1 seconde)

ranks:
  owner:
    prefix: "&4[&c&lOWNER&4] &c"
    suffix: ""
    tabcolor: "&c"
    priority: 1          # Lager = hoger in de TABlist
    display-name: "Owner"

default-rank: player
save-data: true
```

### Kleurcodes
```
&0 Zwart      &8 Donkergrijs
&1 Donkerblauw &9 Blauw
&2 Donkergroen &a Groen
&3 Cyaan      &b Aqua
&4 Donkerrood  &c Rood
&5 Paars      &d Roze
&6 Goud       &e Geel
&7 Grijs      &f Wit

&l Vet   &o Cursief   &n Onderstreept   &r Reset
```

---

## 📁 Bestandsstructuur

```
plugins/TabRanks/
├── config.yml       ← Rangen & TABlist instellen
└── players.yml      ← Automatisch aangemaakt (spelerdata)
```

---

## 🛠️ Compatibiliteit

- ✅ Paper 1.20, 1.20.1, 1.20.4
- ✅ Spigot 1.20+
- ✅ Java 17+
- ⚠️ Werkt naast LuckPerms (zonder conflicten)
