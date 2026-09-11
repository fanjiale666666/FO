package com.fo.addon.pathing;

import com.fo.addon.utils.Debug;

public class PathManagers {
    private static IPathManager instance = new NopPathManager();

    static {
        if (BaritonePathManager.isAvailable()) {
            try {
                instance = new BaritonePathManager();
                Debug.info("[Ying] Baritone 已连接，防卡住寻路可用。");
                Debug.chat("[Ying] Baritone 已连接，防卡住寻路可用。");
            } catch (Throwable e) {
                Debug.info("[Ying] Baritone 初始化失败: " + e);
                Debug.chat("[Ying] Baritone 初始化失败: " + e);
            }
        } else {
            Debug.info("[Ying] 未检测到 Baritone，防卡住寻路不可用。");
            Debug.chat("[Ying] 未检测到 Baritone，防卡住寻路不可用。");
        }
    }

    public static IPathManager get() {
        return instance;
    }
}