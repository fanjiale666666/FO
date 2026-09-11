package com.fo.addon.modules;

import com.fo.addon.AddonTemplate;
import com.fo.addon.utils.TrashLogic;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

/**
 * AutoTrash — 自动扔垃圾（V1，自研）。
 *
 * <p>参考 meteor-miku (GPL) 自动扔垃圾的设计思路自研实现，并增强为黑白名单模式：
 * 黑名单：列表中的物品自动丢弃；
 * 白名单：仅保留列表中的物品，其余全部丢弃（列表为空时保护性暂停，防止误清空背包）。
 */
public class AutoTrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<TrashLogic.Mode> mode = sgGeneral.add(new EnumSetting.Builder<TrashLogic.Mode>()
        .name("mode")
        .description("名单模式：黑名单=只丢列表中的物品；白名单=只保留列表中的物品，其余全丢.")
        .defaultValue(TrashLogic.Mode.BLACKLIST)
        .build());

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("物品列表.")
        .defaultValue(List.of())
        .build());

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("每次丢弃之间的间隔（游戏刻）.")
        .defaultValue(2)
        .min(1)
        .sliderMin(1)
        .sliderMax(20)
        .build());

    private final Setting<Boolean> dropAll = sgGeneral.add(new BoolSetting.Builder()
        .name("drop-all")
        .description("丢弃整组物品；关闭时每次只丢一个.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> excludeHotbar = sgGeneral.add(new BoolSetting.Builder()
        .name("exclude-hotbar")
        .description("不处理快捷栏中的物品（推荐开启，保护常用物品）.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> toggleOffOnClear = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle-off-on-clear")
        .description("背包中无可丢弃物品后自动关闭模块.")
        .defaultValue(false)
        .build());

    private int tickTimer = 0;

    public AutoTrash() {
        super(AddonTemplate.CATEGORY, "auto-trash", "自动扔垃圾：黑/白名单模式自动丢弃指定物品。");
    }

    @Override
    public void onActivate() {
        tickTimer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        List<Item> list = items.get();
        boolean listConfigured = !list.isEmpty();
        if (!listConfigured) {
            // 保护：列表为空时不丢任何物品（尤其白名单模式，防止清空背包）
            if (mode.get() == TrashLogic.Mode.WHITELIST) {
                error("白名单列表为空，已暂停（防止误丢）.");
                toggle();
            }
            return;
        }

        if (tickTimer > 0) {
            tickTimer--;
            return;
        }

        boolean dropped = false;
        int start = excludeHotbar.get() ? 9 : 0;
        int end = mc.player.getInventory().size();

        for (int i = start; i < end; i++) {
            // 保护：跳过当前主手槽，避免误丢正在使用的物品
            if (i == mc.player.getInventory().getSelectedSlot()) continue;

            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            boolean inList = list.contains(stack.getItem());
            if (TrashLogic.shouldDrop(mode.get(), listConfigured, inList)) {
                dropItem(i);
                dropped = true;
                break; // 一次 tick 只丢一种物品
            }
        }

        if (dropped) {
            tickTimer = delay.get();
        } else if (toggleOffOnClear.get()) {
            info("背包中已无垃圾物品，自动关闭.");
            toggle();
        }
    }

    private void dropItem(int slot) {
        if (mc.interactionManager == null) return;

        int screenSlot = SlotUtils.indexToId(slot);
        mc.interactionManager.clickSlot(
            mc.player.currentScreenHandler.syncId,
            screenSlot,
            dropAll.get() ? 1 : 0, // 1=整组(Ctrl+Q)，0=单个(Q)
            SlotActionType.THROW,
            mc.player
        );
    }
}
