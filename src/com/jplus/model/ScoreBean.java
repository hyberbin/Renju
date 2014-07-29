/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jplus.model;

/**
 * 一个棋子的分数 四个分数如果有一个等于50 就Game Over!!! 为什么要用封装类，这样在扫描后面的分数时前面的分数也能跟着改变
 *
 * @author hyberbin
 */
public class ScoreBean{
    
    private EdgeBean edgebean;//一个颜色块的边缘

    
    public EdgeBean getEdgebean() {
        return edgebean;
    }
    
    public void setEdgebean(EdgeBean edgebean) {
        this.edgebean = edgebean;
    }
}
