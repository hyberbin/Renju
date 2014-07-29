/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jplus.action;

import com.jplus.model.ChessBean;
import com.jplus.model.EdgeBean;
import com.jplus.model.SteepBean;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 *
 * @author hyberbin
 */
public class MainFrame extends Frame implements ActionListener {

    public static int size = 10;//棋盘大小
    private static final int buttonsize = 25;//按钮大小相关参数
    private Map chessmap;//本局棋盘
    private LinkedList<EdgeBean> RedEdgelist = null;//红棋边缘（可下位置）
    private LinkedList<EdgeBean> BlackEdgelist = null;//黑棋边缘（可下位置）
    private SteepBean steepbean;//本局棋的所有步法及相关配置
    private Strategy strate;
    private Button button1;
    private Button button2;

    /**
     * 构造方法创建棋盘
     *
     * @param sb 棋局的相关参数
     */
    public MainFrame() {
    }

    public void begin(SteepBean sb) {
        steepbean = sb;
        chessmap = new Map(sb.isIsFirstRead(), this);
        RedEdgelist = chessmap.getRedEdgelist();
        BlackEdgelist = chessmap.getBlackEdgelist();
        strate = new Strategy(this);
        Panel chessPanel = new Panel();
        //设置窗体布局为矩阵式
        chessPanel.setLayout(new GridLayout(size, size));
        //添加按钮
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                chessPanel.add(chessmap.getButtonMap()[i][j]);
            }
        }
        add(chessPanel, "North");
        Panel buttonPanel = new Panel();
        button1 = new Button();
        button2 = new Button();
        button1.setLabel("Go Back");
        button2.setLabel("Replay");
        buttonPanel.add(button1, "North");
        buttonPanel.add(button2, "South");
        add(buttonPanel, "South");
        button2.addActionListener(this);
        button1.addActionListener(this);
        //设置窗体大小
        setSize(size * buttonsize, size * buttonsize + 40);
        //设置窗体默认位置
        Toolkit tk = Toolkit.getDefaultToolkit();
        setLocation((tk.getScreenSize().width - getSize().width) / 2,
                (tk.getScreenSize().height - getSize().height) / 2);
        //设置窗体标题
        setTitle("五子棋");
        //设置关闭事件
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                if (JOptionPane.showConfirmDialog(null, "确认退出？") == 0) {
                    System.exit(0);
                }
            }
        });
        //设置窗体大小不可改变
        setResizable(false);
        //设置窗体可见
        setVisible(true);
        if (steepbean.isIsFirstRead()) {
            strate.start();
        }
    }

    public void palyAgain() {
        this.dispose();
        chessmap = null;
        new Renju().setVisible(true);
    }

    public LinkedList<EdgeBean> getBlackEdgelist() {
        return BlackEdgelist;
    }

    public void setBlackEdgelist(LinkedList<EdgeBean> BlackEdgelist) {
        this.BlackEdgelist = BlackEdgelist;
    }

    public LinkedList<EdgeBean> getRedEdgelist() {
        return RedEdgelist;
    }

    public void setRedEdgelist(LinkedList<EdgeBean> RedEdgelist) {
        this.RedEdgelist = RedEdgelist;
    }

    public Map getChessmap() {
        return chessmap;
    }

    public void setChessmap(Map chessmap) {
        this.chessmap = chessmap;
    }

    public SteepBean getSteepbean() {
        return steepbean;
    }

    public void setSteepbean(SteepBean steepbean) {
        this.steepbean = steepbean;
    }

    public Strategy getStrate() {
        return strate;
    }

    public void setStrate(Strategy strate) {
        this.strate = strate;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button2) {
            if (JOptionPane.showConfirmDialog(null, "确认重来？") == 0) {
                palyAgain();
            }
        } else if (e.getSource() == button1) {
            //JOptionPane.showMessageDialog(null, "暂时不提供！");           
            LinkedList<ChessBean> bsteep = steepbean.getBlackSteep();
            LinkedList<ChessBean> rsteep = steepbean.getRedSteep();
            if (bsteep.isEmpty() || rsteep.isEmpty()) {
                JOptionPane.showMessageDialog(null, "还没开始下怎么就后退？");
                return;
            }
            int x1 = bsteep.getLast().getX();
            int x2 = rsteep.getLast().getX();
            int y1 = bsteep.getLast().getY();
            int y2 = rsteep.getLast().getY();
            chessmap.getMap()[x1][y1] = 0;
            chessmap.getMap()[x2][y2] = 0;
            chessmap.getButtonMap()[x1][y1].setBackground(new Color(240, 240, 240));
            chessmap.getButtonMap()[x2][y2].setBackground(new Color(240, 240, 240));
            bsteep.removeLast();
            rsteep.removeLast();
            if (!bsteep.isEmpty() && !rsteep.isEmpty()) {
                LinkedList<ChessBean> list = new LinkedList<ChessBean>();
                list.addAll(rsteep);
                list.addAll(bsteep);
                chessmap.setBlackEdgelist(new LinkedList<EdgeBean>());
                chessmap.setRedEdgelist(new LinkedList<EdgeBean>());
                for (ChessBean cb : list) {
                    chessmap.resetAllScore(cb);
                }
            }
            chessmap.setDropedCount(chessmap.getDropedCount() - 2);
        }
    }
}
