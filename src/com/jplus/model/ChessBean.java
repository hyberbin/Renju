/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jplus.model;

import com.jplus.action.MainFrame;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 棋盘、棋子基类
 *
 * @author hyberbin
 */
public class ChessBean extends Button implements ActionListener {

    public final Color RED = Color.red;
    public final Color BLACK = Color.BLACK;
    private int x;//棋子的X坐标
    private int y;//棋子的Y坐标
    private ScoreBean Xscore;//横向分数
    private ScoreBean Yscore;//纵向有分数
    private ScoreBean Piescore;//斜上分数
    private ScoreBean Lascore;//斜下分数
    private MainFrame mainframe;

    /**
     * 构造方法 设置此地的XY坐标
     *
     * @param x 棋子的X坐标
     * @param y 棋子的Y坐标
     */
    public ChessBean(MainFrame mainframe, int x, int y) {
        this.x = x;
        this.y = y;
        Xscore = new ScoreBean();
        Yscore = new ScoreBean();
        Piescore = new ScoreBean();
        Lascore = new ScoreBean();
        this.addActionListener(this);        
        this.mainframe = mainframe;
    }

    public ScoreBean getLascore() {
        return Lascore;
    }

    public void setLascore(ScoreBean Lascore) {
        this.Lascore = Lascore;
    }

    public ScoreBean getPiescore() {
        return Piescore;
    }

    public void setPiescore(ScoreBean Piescore) {
        this.Piescore = Piescore;
    }

    public ScoreBean getXscore() {
        return Xscore;
    }

    public void setXscore(ScoreBean Xscore) {
        this.Xscore = Xscore;
    }

    public ScoreBean getYscore() {
        return Yscore;
    }

    public void setYscore(ScoreBean Yscore) {
        this.Yscore = Yscore;
    }

    /**
     * 返回此地Y坐标
     *
     * @return
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * 返回此地X坐标
     *
     * @return
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * 添加棋子点击事件
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (mainframe.getChessmap().setChess(x, y)) {
            mainframe.getStrate().run();//开始想策略
        }
    }
}
