package com.fo.addon;

import com.fo.addon.utils.TrashDefaults;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrashDefaultsTest {

    @Test
    void defaultWhitelistHas61Items() {
        // 用户实测配置：两张截图合并共 61 种物品
        assertEquals(61, TrashDefaults.DEFAULT_WHITELIST_IDS.size());
    }

    @Test
    void defaultWhitelistIdsAreUnique() {
        // 无重复项（避免联动时列表里出现重复物品）
        assertEquals(61, Set.copyOf(TrashDefaults.DEFAULT_WHITELIST_IDS).size());
    }

    @Test
    void defaultWhitelistContainsKeySurvivalItems() {
        // 抽查关键保留物品在默认白名单中
        Set<String> ids = Set.copyOf(TrashDefaults.DEFAULT_WHITELIST_IDS);
        assertTrue(ids.contains("minecraft:elytra"));
        assertTrue(ids.contains("minecraft:totem_of_undying"));
        assertTrue(ids.contains("minecraft:ender_chest"));
        assertTrue(ids.contains("minecraft:golden_carrot"));
        assertTrue(ids.contains("minecraft:firework_rocket"));
    }

    @Test
    void defaultWhitelistCoversAllShulkerBoxColors() {
        // 17 种潜影盒 (含原版) 全部在白名单中
        Set<String> ids = Set.copyOf(TrashDefaults.DEFAULT_WHITELIST_IDS);
        assertTrue(ids.contains("minecraft:shulker_box"));
        assertTrue(ids.contains("minecraft:white_shulker_box"));
        assertTrue(ids.contains("minecraft:black_shulker_box"));
        assertTrue(ids.contains("minecraft:red_shulker_box"));
    }
}
