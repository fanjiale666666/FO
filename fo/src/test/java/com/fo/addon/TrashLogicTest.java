package com.fo.addon;

import com.fo.addon.utils.TrashLogic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrashLogicTest {

    @Test
    void blacklistDropsListedItems() {
        assertTrue(TrashLogic.shouldDrop(TrashLogic.Mode.BLACKLIST, true, true));
    }

    @Test
    void blacklistKeepsUnlistedItems() {
        assertFalse(TrashLogic.shouldDrop(TrashLogic.Mode.BLACKLIST, true, false));
    }

    @Test
    void whitelistKeepsListedItems() {
        assertFalse(TrashLogic.shouldDrop(TrashLogic.Mode.WHITELIST, true, true));
    }

    @Test
    void whitelistDropsUnlistedItems() {
        assertTrue(TrashLogic.shouldDrop(TrashLogic.Mode.WHITELIST, true, false));
    }

    @Test
    void emptyListNeverDropsAnything() {
        // 安全保护：列表为空时无论模式如何都不丢（防止白名单误清空背包）
        assertFalse(TrashLogic.shouldDrop(TrashLogic.Mode.BLACKLIST, false, true));
        assertFalse(TrashLogic.shouldDrop(TrashLogic.Mode.WHITELIST, false, false));
        assertFalse(TrashLogic.shouldDrop(TrashLogic.Mode.WHITELIST, false, true));
    }
}
