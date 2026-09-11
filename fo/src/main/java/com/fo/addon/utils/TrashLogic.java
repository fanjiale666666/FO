package com.fo.addon.utils;

/**
 * AutoTrash 黑白名单纯决策逻辑（不依赖 Minecraft 运行时，可单元测试）。
 *
 * <p>参考 meteor-miku (GPL) 自动扔垃圾的设计思路自研，并增强黑白名单模式。
 */
public final class TrashLogic {

    public enum Mode {
        /** 黑名单：列表中的物品丢弃，其余保留 */
        BLACKLIST,
        /** 白名单：仅保留列表中的物品，其余全部丢弃 */
        WHITELIST
    }

    private TrashLogic() {}

    /**
     * 判断某物品是否应当被丢弃。
     *
     * @param mode          名单模式
     * @param listConfigured 物品列表是否非空（安全保护：列表为空时一律不丢，防止白名单误清空背包）
     * @param itemInList    物品是否在名单中
     */
    public static boolean shouldDrop(Mode mode, boolean listConfigured, boolean itemInList) {
        if (!listConfigured) return false;
        return mode == Mode.BLACKLIST ? itemInList : !itemInList;
    }
}
