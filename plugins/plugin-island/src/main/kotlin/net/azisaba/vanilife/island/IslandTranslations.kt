package net.azisaba.vanilife.island

import net.azisaba.packed.lang.PackLanguage
import net.azisaba.packed.lang.Translation

object IslandTranslations {
    const val DIALOG_VANILIFE_DISPLAY_NAME: String = "dialog.vanilife.display_name"
    const val DIALOG_VANILIFE_DISPLAY_NAME_NEW_NAME: String = "dialog.vanilife.display_name.new_name"
    const val DIALOG_VANILIFE_MEMBERS: String = "dialog.vanilife.members"
    const val DIALOG_VANILIFE_MEMBERS_PLAYER_NAME: String = "dialog.vanilife.members.player_name"
    const val DIALOG_VANILIFE_MEMBERS_ADD: String = "dialog.vanilife.members.add"
    const val DIALOG_VANILIFE_MEMBERS_REMOVE: String = "dialog.vanilife.members.remove"
    const val DIALOG_VANILIFE_SKY_COLOR: String = "dialog.vanilife.sky_color"
    const val DIALOG_VANILIFE_SKY_COLOR_RAIN_LEVEL: String = "dialog.vanilife.sky_color.rain_level"
    const val DIALOG_VANILIFE_SKY_COLOR_THUNDER_LEVEL: String = "dialog.vanilife.sky_color.thunder_level"
    const val DIALOG_VANILIFE_SPAWN_LOCATION: String = "dialog.vanilife.spawn_location"
    const val DIALOG_VANILIFE_SPAWN_LOCATION_DESCRIPTION: String = "dialog.vanilife.spawn_location.description"
    const val DIALOG_VANILIFE_TRAVEL: String = "dialog.vanilife.travel"
    const val DIALOG_VANILIFE_TRAVEL_DESCRIPTION: String = "dialog.vanilife.travel.description"
    const val DIALOG_VANILIFE_TRAVEL_EMPTY: String = "dialog.vanilife.travel.empty"
    const val DIALOG_VANILIFE_TRAVEL_LOCKED: String = "dialog.vanilife.travel.locked"
    const val DIALOG_VANILIFE_TRAVEL_CANNOT_VISIT: String = "dialog.vanilife.travel.cannot_visit"
    const val DIALOG_VANILIFE_VISITORS: String = "dialog.vanilife.visitors"
    const val DIALOG_VANILIFE_VISITORS_ACCESS: String = "dialog.vanilife.visitors.access"
    const val DIALOG_VANILIFE_VISITORS_PUBLIC: String = "dialog.vanilife.visitors.public"
    const val DIALOG_VANILIFE_VISITORS_PRIVATE: String = "dialog.vanilife.visitors.private"
    const val DIALOG_VANILIFE_VISITORS_SAVE: String = "dialog.vanilife.visitors.save"
    const val DIALOG_VANILIFE_VISITORS_PLAYER_NAME: String = "dialog.vanilife.visitors.player_name"
    const val DIALOG_VANILIFE_VISITORS_BAN_INPUT: String = "dialog.vanilife.visitors.ban_input"
    const val DIALOG_VANILIFE_VISITORS_KICK: String = "dialog.vanilife.visitors.kick"
    const val DIALOG_VANILIFE_VISITORS_BAN: String = "dialog.vanilife.visitors.ban"
    const val DIALOG_VANILIFE_VISITORS_UNBAN: String = "dialog.vanilife.visitors.unban"

    const val ISLAND_FEATURE_VISIT_OTHER_ISLANDS: String = "island.feature.visit_other_islands"
    const val ISLAND_FEATURE_CUSTOM_SPAWN_POINT: String = "island.feature.custom_spawn_point"
    const val ISLAND_FEATURE_EXPAND_STORAGE: String = "island.feature.expand_storage"
    const val ISLAND_FEATURE_CUSTOM_SKY_COLOR: String = "island.feature.custom_sky_color"
    const val ISLAND_FEATURE_FLIGHT: String = "island.feature.flight"

    const val ISLAND_LEVEL_UP: String = "island.level_up"
    const val ISLAND_LEVEL_UP_UNLOCKED_FEATURE: String = "island.level_up.unlocked.feature"
    const val ISLAND_LEVEL_UP_UNLOCKED_WRACK_TYPES: String = "island.level_up.unlocked.wrack_types"
    const val ISLAND_VISITOR_KICKED: String = "island.visitor.kicked"

    fun us(): PackLanguage = mapOf(
        DIALOG_VANILIFE_DISPLAY_NAME to Translation.literal("Change island's name"),
        DIALOG_VANILIFE_DISPLAY_NAME_NEW_NAME to Translation.literal("New Name"),
        DIALOG_VANILIFE_MEMBERS to Translation.literal("Island Members (Co-op)"),
        DIALOG_VANILIFE_MEMBERS_PLAYER_NAME to Translation.literal("Player Name to Add"),
        DIALOG_VANILIFE_MEMBERS_ADD to Translation.literal("Add Member"),
        DIALOG_VANILIFE_MEMBERS_REMOVE to Translation.literal("Remove Member: ") + Translation.placeholder(),
        DIALOG_VANILIFE_SKY_COLOR to Translation.literal("Change island's sky color"),
        DIALOG_VANILIFE_SKY_COLOR_RAIN_LEVEL to Translation.literal("Rain Level"),
        DIALOG_VANILIFE_SKY_COLOR_THUNDER_LEVEL to Translation.literal("Thunder Level"),
        DIALOG_VANILIFE_SPAWN_LOCATION to Translation.literal("Change island's spawn point"),
        DIALOG_VANILIFE_SPAWN_LOCATION_DESCRIPTION to Translation.literal("Do you want to change your island spawn point to your current location?"),
        DIALOG_VANILIFE_TRAVEL to Translation.literal("Travel to Another Island"),
        DIALOG_VANILIFE_TRAVEL_DESCRIPTION to Translation.literal("Select an island to visit."),
        DIALOG_VANILIFE_TRAVEL_EMPTY to Translation.literal("No other islands are available to visit."),
        DIALOG_VANILIFE_TRAVEL_LOCKED to Translation.literal("Unlocks at Island Level ") + Translation.placeholder(),
        DIALOG_VANILIFE_TRAVEL_CANNOT_VISIT to Translation.literal("You cannot visit this island."),
        DIALOG_VANILIFE_VISITORS to Translation.literal("Visitor & Access Settings"),
        DIALOG_VANILIFE_VISITORS_ACCESS to Translation.literal("Island Access"),
        DIALOG_VANILIFE_VISITORS_PUBLIC to Translation.literal("Public (Open)"),
        DIALOG_VANILIFE_VISITORS_PRIVATE to Translation.literal("Private (Locked)"),
        DIALOG_VANILIFE_VISITORS_SAVE to Translation.literal("Save Access Setting"),
        DIALOG_VANILIFE_VISITORS_PLAYER_NAME to Translation.literal("Player Name to Ban"),
        DIALOG_VANILIFE_VISITORS_BAN_INPUT to Translation.literal("Save & Ban Entered Player"),
        DIALOG_VANILIFE_VISITORS_KICK to Translation.literal("Kick: ") + Translation.placeholder(),
        DIALOG_VANILIFE_VISITORS_BAN to Translation.literal("Ban: ") + Translation.placeholder(),
        DIALOG_VANILIFE_VISITORS_UNBAN to Translation.literal("Unban: ") + Translation.placeholder(),
        ISLAND_FEATURE_VISIT_OTHER_ISLANDS to Translation.literal("Travel"),
        ISLAND_FEATURE_CUSTOM_SPAWN_POINT to Translation.literal("Change Spawn Point"),
        ISLAND_FEATURE_EXPAND_STORAGE to Translation.literal("Expanded Storage"),
        ISLAND_FEATURE_CUSTOM_SKY_COLOR to Translation.literal("Sky Color"),
        ISLAND_FEATURE_FLIGHT to Translation.literal("Flight Mode"),
        ISLAND_LEVEL_UP to Translation.literal("Level Up!"),
        ISLAND_LEVEL_UP_UNLOCKED_FEATURE to Translation.placeholder() + Translation.literal(" is now available!"),
        ISLAND_LEVEL_UP_UNLOCKED_WRACK_TYPES to Translation.placeholder() + Translation.literal(" new types of wrack can now appear!"),
        ISLAND_VISITOR_KICKED to Translation.literal("You have been sent back from that island."),
    )

    fun jp(): PackLanguage = mapOf(
        DIALOG_VANILIFE_DISPLAY_NAME to Translation.literal("島の名前を変更"),
        DIALOG_VANILIFE_DISPLAY_NAME_NEW_NAME to Translation.literal("新しい名前"),
        DIALOG_VANILIFE_MEMBERS to Translation.literal("島のメンバー管理 (共同生活)"),
        DIALOG_VANILIFE_MEMBERS_PLAYER_NAME to Translation.literal("追加するプレイヤー名"),
        DIALOG_VANILIFE_MEMBERS_ADD to Translation.literal("メンバーを追加"),
        DIALOG_VANILIFE_MEMBERS_REMOVE to Translation.literal("メンバー解除: ") + Translation.placeholder(),
        DIALOG_VANILIFE_SKY_COLOR to Translation.literal("島の空の着色を変更"),
        DIALOG_VANILIFE_SKY_COLOR_RAIN_LEVEL to Translation.literal("雨レベル"),
        DIALOG_VANILIFE_SKY_COLOR_THUNDER_LEVEL to Translation.literal("雷レベル"),
        DIALOG_VANILIFE_SPAWN_LOCATION to Translation.literal("島のスポーン地点を変更"),
        DIALOG_VANILIFE_SPAWN_LOCATION_DESCRIPTION to Translation.literal("島のスポーン地点を現在の位置に変更しますか？"),
        DIALOG_VANILIFE_TRAVEL to Translation.literal("他の島へ旅行する"),
        DIALOG_VANILIFE_TRAVEL_DESCRIPTION to Translation.literal("訪問する島を選択してください。"),
        DIALOG_VANILIFE_TRAVEL_EMPTY to Translation.literal("現在訪問できる他の島がありません。"),
        DIALOG_VANILIFE_TRAVEL_LOCKED to Translation.literal("島レベル ") + Translation.placeholder() + Translation.literal(" で解放されます。"),
        DIALOG_VANILIFE_TRAVEL_CANNOT_VISIT to Translation.literal("この島には訪問できません。"),
        DIALOG_VANILIFE_VISITORS to Translation.literal("訪問設定・訪問者管理"),
        DIALOG_VANILIFE_VISITORS_ACCESS to Translation.literal("島の公開設定"),
        DIALOG_VANILIFE_VISITORS_PUBLIC to Translation.literal("公開（誰でも訪問可能）"),
        DIALOG_VANILIFE_VISITORS_PRIVATE to Translation.literal("非公開（メンバーのみ訪問可能）"),
        DIALOG_VANILIFE_VISITORS_SAVE to Translation.literal("公開設定を保存"),
        DIALOG_VANILIFE_VISITORS_PLAYER_NAME to Translation.literal("Banするプレイヤー名"),
        DIALOG_VANILIFE_VISITORS_BAN_INPUT to Translation.literal("設定保存・入力したプレイヤーをBan"),
        DIALOG_VANILIFE_VISITORS_KICK to Translation.literal("追い出す (Kick): ") + Translation.placeholder(),
        DIALOG_VANILIFE_VISITORS_BAN to Translation.literal("出入り禁止 (Ban): ") + Translation.placeholder(),
        DIALOG_VANILIFE_VISITORS_UNBAN to Translation.literal("Ban解除: ") + Translation.placeholder(),
        ISLAND_FEATURE_VISIT_OTHER_ISLANDS to Translation.literal("旅行"),
        ISLAND_FEATURE_CUSTOM_SPAWN_POINT to Translation.literal("スポーン地点の変更"),
        ISLAND_FEATURE_EXPAND_STORAGE to Translation.literal("おおきな収納"),
        ISLAND_FEATURE_CUSTOM_SKY_COLOR to Translation.literal("そらの着色"),
        ISLAND_FEATURE_FLIGHT to Translation.literal("飛行モード"),
        ISLAND_LEVEL_UP to Translation.literal("レベルアップ！"),
        ISLAND_LEVEL_UP_UNLOCKED_FEATURE to Translation.placeholder() + Translation.literal("を利用できるようになりました！"),
        ISLAND_LEVEL_UP_UNLOCKED_WRACK_TYPES to Translation.placeholder() + Translation.literal("種類の新しい漂流物を解放しました！"),
        ISLAND_VISITOR_KICKED to Translation.literal("島から追い出されたため、自分の島へ戻りました。"),
    )
}
