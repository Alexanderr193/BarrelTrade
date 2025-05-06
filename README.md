# BarrelShop [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Spigot plugin for creating shop barrels in Minecraft

## Features
- Turn any barrel into a shop with renamed paper
- Simple item loading via commands
- Multiple currency support
- Lightweight and efficient
- No database required (uses persistent data containers)

## Installation
1. Download the latest `.jar` from Releases
2. Drop it into your `plugins/` folder
3. Restart/reload your server

## Usage

### Creating a Shop
1. Rename a paper to `"shop"` in an anvil
2. Right-click any barrel while holding the renamed paper

### Managing Items
```
/load <slot> <currency> <price>
```
- Slot: 1-27 (barrel slot number)
- Currency: EMERALD, DIAMOND, IRON, COAL, NETHERITE
- Price: 1-64

### Example:
```
/load 5 DIAMOND 3
```
- Sets slot 5 to cost 3 diamonds

## Command Help
```
Usage: /load <slot> <currency> <price>
Currencies: EMERALD, DIAMOND, IRON, COAL, NETHERITE
Slot range: 1-27
Price range: 1-64
```

## Why This Plugin?
- Simple and intuitive shop creation
- No complex GUIs or sign setups
- Lightweight alternative to chest shops
- Perfect for survival economy servers

## Support
Report issues on GitHub Issues

## License
Apache 2.0

```
Copyright 2025 Alexanderr193

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Libraries used
- [GSON](https://github.com/google/gson) - Apache 2.0 License