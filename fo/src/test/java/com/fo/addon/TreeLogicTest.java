package com.fo.addon;

import com.fo.addon.utils.TreeLogic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeLogicTest {

    @Test
    void bonemealWhenSaplingPresent() {
        assertEquals(TreeLogic.Action.BONEMEAL,
            TreeLogic.decide(true, false, true, true, true));
    }

    @Test
    void noBonemealWhenDisabled() {
        assertEquals(TreeLogic.Action.NONE,
            TreeLogic.decide(true, false, false, true, true));
    }

    @Test
    void noBonemealWhenNoBoneMeal() {
        assertEquals(TreeLogic.Action.NONE,
            TreeLogic.decide(true, false, true, false, true));
    }

    @Test
    void plantWhenAirAndHasSapling() {
        assertEquals(TreeLogic.Action.PLANT,
            TreeLogic.decide(false, true, true, true, true));
    }

    @Test
    void noneWhenBlocked() {
        // 上方既不是树苗也不是空气（有方块挡住）
        assertEquals(TreeLogic.Action.NONE,
            TreeLogic.decide(false, false, true, true, true));
    }

    @Test
    void noneWhenNoSaplingToPlant() {
        assertEquals(TreeLogic.Action.NONE,
            TreeLogic.decide(false, true, true, true, false));
    }

    @Test
    void bonemealTakesPriorityOverPlant() {
        // 树苗已种下但状态为可替换（理论边缘情况），骨粉优先
        assertEquals(TreeLogic.Action.BONEMEAL,
            TreeLogic.decide(true, true, true, true, true));
    }
}
