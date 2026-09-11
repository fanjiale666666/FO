package com.fo.addon.modules;

import com.fo.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.List;

/**
 * AutoTrash — 自动扔垃圾（V1，自研）。
 *
 * <p>参考 meteor-miku (GPL) 的自动扔垃圾设计思路自研实现，并增强为黑白名单模式：
 * 黑名单模式：列表中的物品自动丢弃；
 * 白名单模式：仅保留列表中的物品，其余全部丢弃。
 * 支持自定义延迟。
 *
 * <p>TODO: 实现丢弃逻辑（物品匹配 + 延迟 + 槽位遍历）。
 */
public class AutoTrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("名单模式：黑名单=只丢列表中的物品；白名单=只保留列表中的物品.")
        .defaultValue(Mode.Blacklist)
        .build());

    private final Setting<List<String>> items = sgGeneral.add(new StringListSetting.Builder()
        .name("items")
        .description("物品列表 (如 minecraft:dirt).")
        .defaultValue(List.of())
        .build());

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("丢弃间隔 (tick).")
        .defaultValue(2)
        .min(1)
        .sliderMin(1)
        .sliderMax(20)
        .build());

    private final Setting<Boolean> toggleOffOnClear = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle-off-on-clear")
        .description("背包清空后自动关闭模块.")
        .defaultValue(false)
        .build());

    public AutoTrash() {
        super(AddonTemplate.CATEGORY, "auto-trash", "自动扔垃圾：黑/白名单模式自动丢弃指定物品。");
    }

    public enum Mode {
        Blacklist("黑名单"),
        Whitelist("白名单");

        private final String label;

        Mode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        // TODO: 实现黑白名单丢弃逻辑
    }
}
