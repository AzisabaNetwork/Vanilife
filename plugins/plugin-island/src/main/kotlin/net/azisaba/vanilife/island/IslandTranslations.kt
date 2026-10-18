package net.azisaba.vanilife.island

import net.azisaba.packed.lang.PackLanguage
import net.azisaba.packed.lang.Translation

object IslandTranslations {
    const val DIALOG_VANILIFE_DISPLAY_NAME: String = "dialog.vanilife.display_name"
    const val DIALOG_VANILIFE_DISPLAY_NAME_NEW_NAME: String = "dialog.vanilife.display_name.new_name"
    const val DIALOG_VANILIFE_SKY_COLOR: String = "dialog.vanilife.sky_color"
    const val DIALOG_VANILIFE_SKY_COLOR_RAIN_LEVEL: String = "dialog.vanilife.sky_color.rain_level"
    const val DIALOG_VANILIFE_SKY_COLOR_THUNDER_LEVEL: String = "dialog.vanilife.sky_color.thunder_level"
    const val DIALOG_VANILIFE_SPAWN_LOCATION: String = "dialog.vanilife.spawn_location"
    const val DIALOG_VANILIFE_SPAWN_LOCATION_DESCRIPTION: String = "dialog.vanilife.spawn_location.description"
    const val DIALOG_VANILIFE_TRAVEL: String = "dialog.vanilife.travel"
    const val DIALOG_VANILIFE_TRAVEL_DESCRIPTION: String = "dialog.vanilife.travel.description"
    const val DIALOG_VANILIFE_TRAVEL_EMPTY: String = "dialog.vanilife.travel.empty"
    const val DIALOG_VANILIFE_TRAVEL_LOCKED: String = "dialog.vanilife.travel.locked"

    const val ISLAND_FEATURE_VISIT_OTHER_ISLANDS: String = "island.feature.visit_other_islands"
    const val ISLAND_FEATURE_CUSTOM_SPAWN_POINT: String = "island.feature.custom_spawn_point"
    const val ISLAND_FEATURE_EXPAND_STORAGE: String = "island.feature.expand_storage"
    const val ISLAND_FEATURE_CUSTOM_SKY_COLOR: String = "island.feature.custom_sky_color"
    const val ISLAND_FEATURE_FLIGHT: String = "island.feature.flight"

    const val ISLAND_LEVEL_UP: String = "island.level_up"
    const val ISLAND_LEVEL_UP_UNLOCKED_FEATURE: String = "island.level_up.unlocked.feature"
    const val ISLAND_LEVEL_UP_UNLOCKED_WRACK_TYPES: String = "island.level_up.unlocked.wrack_types"

    fun us(): PackLanguage = mapOf(
        DIALOG_VANILIFE_DISPLAY_NAME to Translation.literal("Change island's name"),
        DIALOG_VANILIFE_DISPLAY_NAME_NEW_NAME to Translation.literal("New Name"),
        DIALOG_VANILIFE_SKY_COLOR to Translation.literal("Change island's sky color"),
        DIALOG_VANILIFE_SKY_COLOR_RAIN_LEVEL to Translation.literal("Rain Level"),
        DIALOG_VANILIFE_SKY_COLOR_THUNDER_LEVEL to Translation.literal("Thunder Level"),
        DIALOG_VANILIFE_SPAWN_LOCATION to Translation.literal("Change island's spawn point"),
        DIALOG_VANILIFE_SPAWN_LOCATION_DESCRIPTION to Translation.literal("Do you want to change your island spawn point to your current location?"),
        DIALOG_VANILIFE_TRAVEL to Translation.literal("Travel to Another Island"),
        DIALOG_VANILIFE_TRAVEL_DESCRIPTION to Translation.literal("Select an island to visit."),
        DIALOG_VANILIFE_TRAVEL_EMPTY to Translation.literal("No other islands are available to visit."),
        DIALOG_VANILIFE_TRAVEL_LOCKED to Translation.literal("Unlocks at Island Level ") + Translation.placeholder(),
        ISLAND_FEATURE_VISIT_OTHER_ISLANDS to Translation.literal("Travel"),
        ISLAND_FEATURE_CUSTOM_SPAWN_POINT to Translation.literal("Change Spawn Point"),
        ISLAND_FEATURE_EXPAND_STORAGE to Translation.literal("Expanded Storage"),
        ISLAND_FEATURE_CUSTOM_SKY_COLOR to Translation.literal("Sky Color"),
        ISLAND_FEATURE_FLIGHT to Translation.literal("Flight Mode"),
        ISLAND_LEVEL_UP to Translation.literal("Level Up!"),
        ISLAND_LEVEL_UP_UNLOCKED_FEATURE to Translation.placeholder() + Translation.literal(" is now available!"),
        ISLAND_LEVEL_UP_UNLOCKED_WRACK_TYPES to Translation.placeholder() + Translation.literal(" new types of wrack can now appear!"),
    )

    fun jp(): PackLanguage = mapOf(
        DIALOG_VANILIFE_DISPLAY_NAME to Translation.literal("島の名前を変更"),
        DIALOG_VANILIFE_DISPLAY_NAME_NEW_NAME to Translation.literal("新しい名前"),
        DIALOG_VANILIFE_SKY_COLOR to Translation.literal("島の空の着色を変更"),
        DIALOG_VANILIFE_SKY_COLOR_RAIN_LEVEL to Translation.literal("雨レベル"),
        DIALOG_VANILIFE_SKY_COLOR_THUNDER_LEVEL to Translation.literal("雷レベル"),
        DIALOG_VANILIFE_SPAWN_LOCATION to Translation.literal("島のスポーン地点を変更"),
        DIALOG_VANILIFE_SPAWN_LOCATION_DESCRIPTION to Translation.literal("島のスポーン地点を現在の位置に変更しますか？"),
        DIALOG_VANILIFE_TRAVEL to Translation.literal("他の島へ旅行する"),
        DIALOG_VANILIFE_TRAVEL_DESCRIPTION to Translation.literal("訪問する島を選択してください。"),
        DIALOG_VANILIFE_TRAVEL_EMPTY to Translation.literal("現在訪問できる他の島がありません。"),
        DIALOG_VANILIFE_TRAVEL_LOCKED to Translation.literal("島レベル ") + Translation.placeholder() + Translation.literal(" で解放されます。"),
        ISLAND_FEATURE_VISIT_OTHER_ISLANDS to Translation.literal("旅行"),
        ISLAND_FEATURE_CUSTOM_SPAWN_POINT to Translation.literal("スポーン地点の変更"),
        ISLAND_FEATURE_EXPAND_STORAGE to Translation.literal("おおきな収納"),
        ISLAND_FEATURE_CUSTOM_SKY_COLOR to Translation.literal("そらの着色"),
        ISLAND_FEATURE_FLIGHT to Translation.literal("飛行モード"),
        ISLAND_LEVEL_UP to Translation.literal("レベルアップ！"),
        ISLAND_LEVEL_UP_UNLOCKED_FEATURE to Translation.placeholder() + Translation.literal("を利用できるようになりました！"),
        ISLAND_LEVEL_UP_UNLOCKED_WRACK_TYPES to Translation.placeholder() + Translation.literal("種類の新しい漂流物を解放しました！"),
    )
}
