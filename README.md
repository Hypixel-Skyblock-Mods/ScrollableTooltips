# Scrollable Tooltips

Scrollable Tooltips is a lightweight client-side Fabric mod that lets you use
the mouse wheel to read item tooltips that are taller than the screen. It is
especially useful for the long item lore found throughout Hypixel SkyBlock.

## Features

- Scrolls vertically while hovering a non-empty item slot in an inventory.
- Activates only when the item tooltip is taller than the screen or would
  otherwise extend outside the visible area.
- Starts oversized tooltips at the top and moves them in predictable 10-pixel
  steps.
- Clamps both ends so the complete tooltip can be read without losing it off
  screen.
- Resets automatically when the hovered item changes or the screen closes.
- Leaves normal wheel behavior unchanged when no scrollable tooltip is active.

Scrollable Tooltips changes only client-side rendering and input handling. It
does not modify items, inventory clicks, or server behavior.

## Supported versions

- Minecraft 26.1.2 + Fabric
- Minecraft 26.2 + Fabric

The mod requires Fabric Loader 0.19.3 or newer, Fabric Language Kotlin, and
Java 25. It does not require Fabric API.

## Building

Build every supported Minecraft version:

```bash
./gradlew build
```

Production JARs are written to:

```text
versions/mc26_1_2/build/libs/ScrollableTooltips-1.0.1+mc26.1.2.jar
versions/mc26_2/build/libs/ScrollableTooltips-1.0.1+mc26.2.jar
```

The implementation is independent. Older tooltip-scrolling mods were consulted
only to understand expected user-facing behavior; no source or assets were
copied.
