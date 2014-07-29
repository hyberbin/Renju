/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jplus.model;

import java.util.LinkedList;

/**
 * 步法模型 如果有电脑参与，电脑为红棋
 *
 * @author hyberbin
 */
public class SteepBean {

    private boolean isFirstRead = true;//是否红下第一步
    private boolean isRobotTrue = true;//是否有电脑
    private boolean isBothRobotTrue = false;//是双方都是电脑
    private LinkedList<ChessBean> redSteep = new LinkedList<ChessBean>();//
    private LinkedList<ChessBean> blackSteep = new LinkedList<ChessBean>();//

    public LinkedList<ChessBean> getBlackSteep() {
        return blackSteep;
    }

    public void setBlackSteep(LinkedList<ChessBean> blackSteep) {
        this.blackSteep = blackSteep;
    }

    public boolean isIsFirstRead() {
        return isFirstRead;
    }

    public void setIsFirstRead(boolean isFirstRead) {
        this.isFirstRead = isFirstRead;
    }
    public boolean isIsRobotTrue() {
        return isRobotTrue;
    }

    public void setIsRobotTrue(boolean isRobotTrue) {
        this.isRobotTrue = isRobotTrue;
    }

    public LinkedList<ChessBean> getRedSteep() {
        return redSteep;
    }

    public void setRedSteep(LinkedList<ChessBean> redSteep) {
        this.redSteep = redSteep;
    }

    public boolean isIsBothRobotTrue() {
        return isBothRobotTrue;
    }

    public void setIsBothRobotTrue(boolean isBothRobotTrue) {
        this.isBothRobotTrue = isBothRobotTrue;
    }
}
