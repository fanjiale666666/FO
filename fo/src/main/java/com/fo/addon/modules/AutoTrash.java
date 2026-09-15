package com.fo.addon.modules;

import com.fo.addon.AddonTemplate;
import com.fo.addon.utils.TrashDefaults;
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
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        .name("名单模式")
        .description("名单模式：黑名单=只丢列表中的物品；白名单=只保留列表中的物品，其余全丢.")
        .defaultValue(TrashLogic.Mode.WHITELIST)
        .build());

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("物品列表")
        .description("物品列表.")
        .defaultValue(TrashDefaults.toItems())
        .build());

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("丢弃延迟")
        .description("每次丢弃之间的间隔（游戏刻）.")
        .defaultValue(2)
        .min(1)
        .sliderMin(1)
        .sliderMax(20)
        .build());

    private final Setting<Boolean> dropAll = sgGeneral.add(new BoolSetting.Builder()
        .name("丢弃整组")
        .description("丢弃整组物品；关闭时每次只丢一个.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> excludeHotbar = sgGeneral.add(new BoolSetting.Builder()
        .name("排除快捷栏")
        .description("不处理快捷栏中的物品（推荐开启，保护常用物品）.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> toggleOffOnClear = sgGeneral.add(new BoolSetting.Builder()
        .name("清空后自动关闭")
        .description("背包中无可丢弃物品后自动关闭模块.")
        .defaultValue(false)
        .build());

    private int tickTimer = 0;

    public AutoTrash() {
        super(AddonTemplate.CATEGORY, "FO 自动扔垃圾", "自动扔垃圾：黑/白名单模式自动丢弃指定物品。");
    }

    /** 供 ElytraCollector 联动调用：强制白名单模式；列表非空则沿用用户配置，空才填默认 61 项；额外弹一次系统通知 */
    public void enableForElytraLink() {
        mode.set(TrashLogic.Mode.WHITELIST);
        boolean filledDefault = TrashDefaults.shouldFillDefault(items.get());
        if (filledDefault) {
            items.set(TrashDefaults.toItems());
        }
        if (!isActive()) toggle();

        // 额外弹一次系统通知 (Toast)，明确本次联动用的什么列表
        try {
            String text = filledDefault
                ? "物品列表为空，已重置为基础白名单 " + TrashDefaults.DEFAULT_WHITELIST_IDS.size() + " 项"
                : "沿用你的白名单列表（" + items.get().size() + " 项）";
            MeteorToast toast = new MeteorToast.Builder("已联动开启 FO 自动扔垃圾")
                .icon(Items.SHULKER_BOX)
                .text(text)
                .build();
            mc.getToastManager().add(toast);
        } catch (Exception ignored) {
            // 通知失败不影响联动主逻辑
        }
    }

    /** 供 ElytraCollector 联动回滚调用：仅关闭模块（不还原模式/列表） */
    public void disableForElytraLink() {
        if (isActive()) toggle();
    }

    @Override
    public void onActivate() {
        tickTimer = 0;
        // 启动时打印当前模式与列表数量，方便确认设置是否生效
        info("已启动 (模式: " + mode.get() + ", 列表物品数: " + items.get().size() + ").");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        List<Item> list = items.get();
        boolean listConfigured = !list.isEmpty();
        if (!listConfigured) {
            // 保护：列表为空时不丢任何物品（尤其白名单模式，防止清空背包）。
            // 注意：不强制关闭模块——之前 toggle() 自动关会让用户误以为"设置被重置成黑名单"，
            // 且模块开不起来。改为仅提示一次。
            if (mode.get() == TrashLogic.Mode.WHITELIST) {
                info("白名单模式但物品列表为空，未丢弃任何物品 (请在物品列表中添加要保留的物品).");
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
