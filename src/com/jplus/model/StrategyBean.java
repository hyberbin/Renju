/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jplus.model;

/**
 * 策略模型
 *
 * @author hyberbin
 */
public class StrategyBean implements Comparable {

    private int x;//策略横坐标
    private int y;//策略纵坐标
    private int score;
    private EdgeBean edgebean;//此策略对应的边缘

    public EdgeBean getEdgebean() {
        return edgebean;
    }

    public void setEdgebean(EdgeBean edgebean) {
        this.edgebean = edgebean;
    }

    public StrategyBean(int x, int y, EdgeBean edgebean) {
        this.x = x;
        this.y = y;
        this.edgebean = edgebean;
    }

    public StrategyBean() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        if (score == 0) {
            return ((StrategyBean) o).getEdgebean().getScore() - this.edgebean.getScore();
        }
        return ((StrategyBean) o).getScore() - this.getScore();
    }
}
