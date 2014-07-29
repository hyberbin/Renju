/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jplus.model;

/**
 * 一条线的端点记录
 *
 * @author yingbin
 */
public class EdgeBean implements Comparable {

    private int score = 10;//这条线的分数
    private int edg1x = -1;//第一个端点的横坐标
    private int edg1y = -1;//第一个端点的纵坐标
    private int edg2x = -1;//第二个端点的横坐标
    private int edg2y = -1;//第二个端点的纵坐标
    private int useable = 0;//0表示不可用的边缘，1代表第一个可用，2代表第二个可用,3代表都可用
    private int type;//边缘类型 值为4种情况代表四个方向，横竖撇剌
    private EdgeBean leftEdge;//前一个可下位置的前一个
    private EdgeBean rightEdge;//后一个可下位置的后一个
    public static boolean gameover = false;

    public EdgeBean(int type) {
        this.type = type;
    }

    public int getUseable() {
        return useable;
    }

    public void setEdge1(int x, int y) {
        edg1x = x;
        edg1y = y;
    }

    public void setEdge2(int x, int y) {
        edg2x = x;
        edg2y = y;
    }

    public void setUseable(int useable) {
        this.useable = useable;
    }

    public int getEdg1x() {
        return edg1x;
    }

    public void setEdg1x(int edg1x) {
        this.edg1x = edg1x;
    }

    public int getEdg1y() {
        return edg1y;
    }

    public void setEdg1y(int edg1y) {
        this.edg1y = edg1y;
    }

    public int getEdg2x() {
        return edg2x;
    }

    public void setEdg2x(int edg2x) {
        this.edg2x = edg2x;
    }

    public int getEdg2y() {
        return edg2y;
    }

    public void setEdg2y(int edg2y) {
        this.edg2y = edg2y;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score >= 50) {
            gameover = true;
        }
        this.score = score;
    }

    public int getType() {
        return type;
    }

    public EdgeBean getLeftEdge() {
        return leftEdge;
    }

    public void setLeftEdge(EdgeBean leftEdge) {
        this.leftEdge = leftEdge;
    }

    public EdgeBean getRightEdge() {
        return rightEdge;
    }

    public void setRightEdge(EdgeBean rightEdge) {
        this.rightEdge = rightEdge;
    }

    @Override
    public int compareTo(Object o) {
        return ((EdgeBean) o).getScore() - this.score;
    }

    /**
     * 将前一个端点与后一个端点加起来后的分数
     *
     * @return
     */
    public int leftAdd() {
        if (leftEdge == null) {
            return 0;
        } else {
            return score + leftEdge.getScore() + 10;
        }
    }

    public int rightAdd() {
        if (rightEdge == null) {
            return 0;
        } else {
            return score + rightEdge.getScore() + 10;
        }
    }
}
