package com.fo.addon.modules;

import com.fo.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.List;

/**
 * AutoUse — 自动使用物品（V1，自研）。
 *
 * <p>参考 meteor-miku (GPL) 的自动使用物品设计思路自研实现：
 * 按时间或血量/饥饿值阈值，按优先级和自定义延迟自动使用指定物品
 * （如自动吃食物、自动用金苹果等）。
 *
 * <p>TODO: 实现使用逻辑（阈值判定 + 优先级 + 延迟）。
 */
public class AutoUse extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> items = sgGeneral.add(new StringListSetting.Builder()
        .name("items")
        .description("按优先级排序的物品列表 (如 minecraft:cooked_beef;minecraft:golden_apple).")
        .defaultValue(List.of("minecraft:cooked_beef"))
        .build());

    private final Setting<Integer> healthThreshold = sgGeneral.add(new IntSetting.Builder()
        .name("health-threshold")
        .description("血量低于此百分比时使用物品 (0 = 禁用).")
        .defaultValue(50)
        .min(0)
        .max(100)
        .sliderMin(0)
        .sliderMax(100)
        .build());

    private final Setting<Integer> hungerThreshold = sgGeneral.add(new IntSetting.Builder()
        .name("hunger-threshold")
        .description("饥饿值低于此百分比时使用物品 (0 = 禁用).")
        .defaultValue(30)
        .min(0)
        .max(100)
        .sliderMin(0)
        .sliderMax(100)
        .build());

    private final Setting<Integer> interval = sgGeneral.add(new IntSetting.Builder()
        .name("interval")
        .description("使用间隔 (tick).")
        .defaultValue(5)
        .min(1)
        .sliderMin(1)
        .sliderMax(100)
        .build());

    private final Setting<Boolean> silent = sgGeneral.add(new BoolSetting.Builder()
        .name("silent")
        .description("静默使用（不切换背包选中槽）. 实验性.")
        .defaultValue(false)
        .build());

    public AutoUse() {
        super(AddonTemplate.CATEGORY, "auto-use", "自动使用物品：按时间/血量/饥饿阈值自动使用指定物品。");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        // TODO: 实现阈值判定与使用逻辑
    }
}
