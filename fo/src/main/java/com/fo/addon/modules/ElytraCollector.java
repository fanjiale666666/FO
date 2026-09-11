package com.fo.addon.modules;

import com.fo.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.List;

/**
 * ElytraCollector — 鞘翅全自动采集（V1 核心模块）。
 *
 * <p>移植自 Ying (CC0) 的 ElytraCollectorModule：
 * 基于世界种子定位末地船 → 自动飞行（起飞/爬升/巡航/降落状态机）→
 * 取鞘翅 → 潜影盒存取（盒满后存末影箱）→ 物资补货 → 防虚空退出。
 *
 * <p>TODO: 移植 Ying 完整实现（seedfinding 种子定位 + 飞行状态机 + 存储会话）。
 */
public class ElytraCollector extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgStorage = settings.createGroup("Storage");

    // ========== General ==========
    private final Setting<String> seed = sgGeneral.add(new StringSetting.Builder()
        .name("seed")
        .description("世界种子 (0 = 当前世界).")
        .defaultValue("0")
        .build());

    private final Setting<Integer> searchRange = sgGeneral.add(new IntSetting.Builder()
        .name("search-range")
        .description("搜索半径，单位方块 (从玩家位置).")
        .defaultValue(10000)
        .min(100)
        .sliderMin(100)
        .sliderMax(50000)
        .build());

    private final Setting<Boolean> start = sgGeneral.add(new BoolSetting.Builder()
        .name("start")
        .description("开始自动采集.")
        .defaultValue(false)
        .build());

    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
        .name("debug")
        .description("输出调试日志 (用于校准高度公式).")
        .defaultValue(false)
        .build());

    // ========== Storage ==========
    private final Setting<Integer> freeSlotDump = sgStorage.add(new IntSetting.Builder()
        .name("dump-free-slots")
        .description("背包可用空格 ≤ 此值时，拿到鞘翅后自动去末影箱存鞘翅.")
        .defaultValue(10)
        .min(1)
        .sliderMin(1)
        .sliderMax(36)
        .build());

    private final Setting<List<String>> supplies = sgStorage.add(new StringListSetting.Builder()
        .name("supplies")
        .description("物资列表，格式: 物品ID;最低值;目标库存 (如 minecraft:firework_rocket;32;256). 背包物资低于最低值时自动从末影箱补货，拿到目标库存为止.")
        .defaultValue(List.of("minecraft:firework_rocket;32;256"))
        .build());

    private final Setting<Boolean> lowYExit = sgStorage.add(new BoolSetting.Builder()
        .name("low-y-exit")
        .description("Y 低于阈值时自动退出游戏 (防虚空掉物).")
        .defaultValue(true)
        .build());

    private final Setting<Integer> lowYThreshold = sgStorage.add(new IntSetting.Builder()
        .name("low-y-threshold")
        .description("低于此 Y 自动退出游戏.")
        .defaultValue(-30)
        .min(-300)
        .sliderMin(-300)
        .sliderMax(0)
        .build());

    public ElytraCollector() {
        super(AddonTemplate.CATEGORY, "elytra-collector", "鞘翅全自动采集：种子定位末地船、自动飞行、潜影盒/末影箱存取。");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        // TODO: 移植 Ying 飞行状态机 + 存储会话
    }
}
