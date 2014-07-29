package com.jplus.action;

import com.jplus.model.ChessBean;
import com.jplus.model.EdgeBean;
import com.jplus.model.ScoreBean;
import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * 棋盘地图
 *
 * @author hyberbin
 */
public class Map {

    private final int size = MainFrame.size;//棋盘大小
    //map的值只允许出现三种情况 0代表没有棋子，1代表有红棋子，2代表有黑棋子
    private int[][] map;//棋盘地图
    private ChessBean[][] buttonMap;
    private int dropedCount = 0;//已经下了的棋子个数
    private boolean isRedTrue;//当前是否红下
    private LinkedList<EdgeBean> RedEdgelist;//红棋的边缘
    private LinkedList<EdgeBean> BlackEdgelist;//黑棋的边缘
    private MainFrame mainframe;

    /**
     * 构造方法 初始化地图
     *
     * @param isRedTrue 是否红先下
     * @param mf 主调对象
     */
    public Map(boolean isRedTrue, MainFrame mf) {
        this.mainframe = mf;
        dropedCount = 0;
        this.isRedTrue = isRedTrue;
        map = new int[size][size];
        buttonMap = new ChessBean[size][size];
        RedEdgelist = new LinkedList<EdgeBean>();
        BlackEdgelist = new LinkedList<EdgeBean>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = 0;
                buttonMap[i][j] = new ChessBean(mf, i, j);
            }
        }
    }

    /**
     * 放置棋子
     *
     * @param x X坐标
     * @param y Y坐标
     */
    public boolean setChess(int x, int y) {
        if (x < 0 || y < 0 || x > MainFrame.size - 1 || y > MainFrame.size - 1 || map[x][y] != 0) {
            return false;//只有此地没有下时可下
        }
        ChessBean button = buttonMap[x][y];
        dropedCount++;
        if (isRedTrue) {
            map[x][y] = 1;
            button.setBackground(Color.red);
            mainframe.getSteepbean().getRedSteep().add(button);
            isRedTrue = false;
        } else {
            map[x][y] = 2;
            button.setBackground(Color.black);
            mainframe.getSteepbean().getBlackSteep().add(button);
            isRedTrue = true;
        }
        //只用分析最后一个新加的，把它对其它有影响的分数重新打上  
        resetAllScore(isRedTrue ? mainframe.getSteepbean().getBlackSteep().getLast() : mainframe.getSteepbean().getRedSteep().getLast());
        if (dropedCount == size * size || EdgeBean.gameover) {
            String notice = isRedTrue ? "黑方胜！" : "红方胜！";
            if (dropedCount == size * size) {
                notice = "平局！ Game Over!!!确定后退出";
            }
            JOptionPane.showMessageDialog(null, notice + " Game Over!!!");
            int mess = JOptionPane.showConfirmDialog(null, "重玩一局？");
            if (mess == 0) {
                EdgeBean.gameover=false;
                mainframe.palyAgain();
            } else if (mess == 1) {
                System.exit(0);
            }
        }
        return true;
    }

    /**
     * 小工具，用于复制地图
     *
     * @param map 原地图
     * @param map0 目的地图
     */
    private void arraycopy(int[][] map, int[][] map0) {
        for (int i = 0; i < map[0].length; i++) {
            System.arraycopy(map[i], 0, map0[i], 0, map[0].length);
        }
    }

    /**
     * 给整个棋盘重新打分
     *
     * @return 最大分数
     */
    public void resetAllScore(ChessBean cb) {
        int[][] map0 = new int[size][size];
        int[][] map1 = new int[size][size];
        int[][] map2 = new int[size][size];
        int[][] map3 = new int[size][size];
        arraycopy(map, map0);//复制一个地图用来分析横向的
        arraycopy(map, map1);//复制一个地图用来分析纵向的
        arraycopy(map, map2);//复制一个地图用来分析斜上的
        arraycopy(map, map3);//复制一个地图用来分析斜下的
        //获得坐标值
        int i = cb.getX();
        int j = cb.getY();
        //分析相同的颜色
        LinkedList<EdgeBean> list = map[i][j] == 1 ? RedEdgelist : BlackEdgelist;

        if (map0[i][j] != 0) {
            ScoreBean sbean = new ScoreBean();
            map0[i][j] = 0;
            list.add(goX(sbean, i, j, map0));
        }
        if (map1[i][j] != 0) {
            ScoreBean sbean = new ScoreBean();
            map1[i][j] = 0;
            list.add(goY(sbean, i, j, map1));
        }
        if (map2[i][j] != 0) {
            ScoreBean sbean = new ScoreBean();
            map2[i][j] = 0;
            list.add(goPie(sbean, i, j, map2));
        }
        if (map3[i][j] != 0) {
            ScoreBean sbean = new ScoreBean();
            map3[i][j] = 0;
            list.add(goLa(sbean, i, j, map3));
        }
        ckeckAlledge();//检查所有边缘 把不可用的移除//因为可能有32分的就应该先检查边缘
        Collections.sort(RedEdgelist);//对分数重新排序
        Collections.sort(BlackEdgelist);//对分数重新排序
    }

    /**
     * 检查所有边缘 把不可用的移除
     */
    public void ckeckAlledge() {
        for (int i = 0; i < RedEdgelist.size(); i++) {
            if (!checkEdge(RedEdgelist.get(i))) {
                RedEdgelist.remove(i);//如果边缘一个都不可用就移除该边缘
                i--;
            }
        }
        for (int i = 0; i < BlackEdgelist.size(); i++) {
            if (!checkEdge(BlackEdgelist.get(i))) {
                BlackEdgelist.remove(i);//如果边缘一个都不可用就移除该边缘
                i--;
            }
        }
        //把端点的端点加上去
        for (EdgeBean edge : RedEdgelist) {
            setEdgeEdge(edge);
        }
        for (EdgeBean edge : BlackEdgelist) {
            setEdgeEdge(edge);
        }
    }

    /**
     * 设置端点的端点
     *
     * @param edge 端点
     */
    private void setEdgeEdge(EdgeBean edge) {
        int x1 = edge.getEdg1x();
        int y1 = edge.getEdg1y();
        int x2 = edge.getEdg2x();
        int y2 = edge.getEdg2y();
        if (dropedCount > 3 && edge.getScore() <= 10) {
            return;//孤立的子不设置
        }
        if (edge.getType() == 1) {//横向的
            if (y1 > 0 && map[x1][y1] == 0 && map[x1][y1 + 1] == map[x1][y1 - 1]) {//边缘的边缘没越界且边缘的左边颜色相同
                edge.setLeftEdge(buttonMap[x1][y1 - 1].getXscore().getEdgebean());
            }
            if (y2 > 0 && map[x2][y2] == 0 && y2 < MainFrame.size - 1 && map[x2][y2 - 1] == map[x2][y2 + 1]) {//边缘的边缘没越界且边缘的右边颜色相同
                edge.setRightEdge(buttonMap[x2][y2 + 1].getXscore().getEdgebean());
            }
        }
        if (edge.getType() == 2) {//纵向的
            if (x1 > 0 && map[x1][y1] == 0 && map[x1 + 1][y1] == map[x1 - 1][y1]) {//边缘的边缘没越界且边缘的左边颜色相同
                edge.setLeftEdge(buttonMap[x1 - 1][y1].getYscore().getEdgebean());
            }
            if (x2 > 0 && map[x2][y2] == 0 && x2 < MainFrame.size - 1 && map[x2 - 1][y2] == map[x2 + 1][y2]) {//边缘的边缘没越界且边缘的右边颜色相同
                edge.setRightEdge(buttonMap[x2 + 1][y2].getYscore().getEdgebean());
            }
        }
        if (edge.getType() == 3) {//斜上方向
            if (x1 > 0 && map[x1][y1] == 0 && y1 < MainFrame.size - 1 && map[x1 + 1][y1 - 1] == map[x1 - 1][y1 + 1]) {//边缘的边缘没越界且边缘的左边颜色相同
                edge.setLeftEdge(buttonMap[x1 - 1][y1 + 1].getPiescore().getEdgebean());
            }
            if (y2 > 0 && map[x2][y2] == 0 && x2 < MainFrame.size - 1 && map[x2 - 1][y2 + 1] == map[x2 + 1][y2 - 1]) {//边缘的边缘没越界且边缘的右边颜色相同
                edge.setRightEdge(buttonMap[x2 + 1][y2 - 1].getPiescore().getEdgebean());
            }
        }
        if (edge.getType() == 4) {//斜下方向
            if (x1 > 0 && map[x1][y1] == 0 && y1 > 0 && map[x1 + 1][y1 + 1] == map[x1 - 1][y1 - 1]) {//边缘的边缘没越界且边缘的左边颜色相同
                edge.setLeftEdge(buttonMap[x1 - 1][y1 - 1].getLascore().getEdgebean());
            }
            if (x2 > 0 && map[x2][y2] == 0 && x2 < MainFrame.size - 1 && y2 < MainFrame.size - 1 && map[x2 - 1][y2 - 1] == map[x2 + 1][y2 + 1]) {//边缘的边缘没越界且边缘的右边颜色相同
                edge.setRightEdge(buttonMap[x2 + 1][y2 + 1].getLascore().getEdgebean());
            }
        }
    }

    /**
     * 检查边缘是否有用
     *
     * @param edgebean 边缘模型
     * @return 是否有用
     */
    private boolean checkEdge(EdgeBean edgebean) {
        int ex1 = edgebean.getEdg1x();
        int ex2 = edgebean.getEdg2x();
        int ey1 = edgebean.getEdg1y();
        int ey2 = edgebean.getEdg2y();
        boolean b = false;
        if ((ex1 == -1 || ey1 == -1) && (ex2 == -1 || ey2 == -1)) {
            edgebean.setUseable(0);//没有一个可用
        } else if ((ex1 == -1 || ey1 == -1 || map[ex1][ey1] > 0) && (ex2 != -1 && ey2 != -1) && map[ex2][ey2] == 0) {
            edgebean.setUseable(2);
            b = true;//第二个可用
        } else if (((ex1 != -1 && ey1 != -1) && map[ex1][ey1] == 0) && (ex2 == -1 || ey2 == -1 || map[ex2][ey2] > 0)) {
            edgebean.setUseable(1);
            b = true;//第一个可用
        } else if ((ex1 != -1 && ey1 != -1) && (ex2 != -1 && ey2 != -1) && map[ex1][ey1] == 0 && map[ex2][ey2] == 0) {
            edgebean.setUseable(3);//两个都可用
            edgebean.setScore(edgebean.getScore() - edgebean.getScore() % 10 + 5);
            b = true;
        }
        if (b && edgebean.getUseable() < 3&&edgebean.getScore()<40) {//如果只有一个端点可用，并且端点在边框上，永远构成不了威胁则移除
            int x1 = edgebean.getEdg1x();
            int y1 = edgebean.getEdg1y();
            int x2 = edgebean.getEdg2x();
            int y2 = edgebean.getEdg2y();
            if (edgebean.getType() == 1) {//横向
                if (y1 == 0 && edgebean.getUseable() == 1) {
                    b = false;
                } else if (y2 == size - 1 && edgebean.getUseable() == 2) {
                    b = false;
                }
            } else if (edgebean.getType() == 2) {//纵向
                if (x1 == 0 && edgebean.getUseable() == 1) {
                    b = false;
                } else if (x2 == size - 1 && edgebean.getUseable() == 2) {
                    b = false;
                }
            } else if (edgebean.getType() == 4) {//纵向
                if (x1 == 0 && y1 == 0 && edgebean.getUseable() == 1) {
                    b = false;
                } else if (x2 == size - 1 && y2 == size - 1 && edgebean.getUseable() == 2) {
                    b = false;
                }
            } else if (edgebean.getType() == 3) {//纵向
                if (x1 == 0 && y1 == size - 1 && edgebean.getUseable() == 1) {
                    b = false;
                } else if (x2 == size - 1 && y2 == 0 && edgebean.getUseable() == 2) {
                    b = false;
                }
            }
        }
        return b;
    }

    /**
     * 计算与本棋子同行的分数放在边缘模型中
     *
     * @param x Y坐标
     * @param y Y坐标
     * @param scorebean 分数
     * @param map0 对照模型
     * @return 边缘模型
     */
    private EdgeBean goX(ScoreBean scorebean, int x, int y, int[][] map0) {
        EdgeBean edgebean = new EdgeBean(1);
        int nowy = y;
        int value = 10;
        buttonMap[x][y].setXscore(scorebean);
        while (y != 0) {//先往左找
            if (map[x][y] != 0 && map[x][y] == map[x][y - 1]) {//相邻两个颜色相比较
                value += 10;//有一样的就加10分
                map0[x][y - 1] = 0;//标记对照，以免重复
                buttonMap[x][y - 1].setXscore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg1x(x);//设置端点
                edgebean.setEdg1y(y - 1);
                break;
            }
            y--;
        }
        y = nowy;
        while (y != size - 1) {//再向右找
            if (map[x][y] != 0 && map[x][y] == map[x][y + 1]) {//相邻两个颜色相比较                
                value += 10;//有一样的就加10分
                map0[x][y + 1] = 0;
                buttonMap[x][y + 1].setXscore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg2x(x);//设置端点
                edgebean.setEdg2y(y + 1);
                break;
            }
            y++;
        }
        scorebean.setEdgebean(edgebean);
        edgebean.setScore(value);
        return edgebean;
    }

    /**
     * 计算与本棋子同列的分数
     *
     * @param x Y坐标
     * @param y Y坐标
     * @param scorebean 分数
     * @return 边缘模型
     */
    private EdgeBean goY(ScoreBean scorebean, int x, int y, int[][] map0) {
        EdgeBean edgebean = new EdgeBean(2);
        int nowx = x;
        int value = 10;
        buttonMap[x][y].setYscore(scorebean);
        while (x != 0) {//先往上找
            if (map[x][y] != 0 && map[x][y] == map[x - 1][y]) {//相邻两个颜色相比较
                value += 10;//有一样的就加10分
                map0[x - 1][y] = 0;
                buttonMap[x - 1][y].setYscore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg1x(x - 1);//设置端点
                edgebean.setEdg1y(y);
                break;
            }
            x--;
        }
        x = nowx;
        while (x != size - 1) {//先往下找
            if (map[x][y] != 0 && map[x][y] == map[x + 1][y]) {//相邻两个颜色相比较
                value += 10;//有一样的就加10分
                map0[x + 1][y] = 0;
                buttonMap[x + 1][y].setYscore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg2x(x + 1);//设置端点
                edgebean.setEdg2y(y);
                break;
            }
            x++;
        }
        scorebean.setEdgebean(edgebean);
        edgebean.setScore(value);
        return edgebean;
    }

    /**
     * 计算与本棋子在斜上线的分数
     *
     * @param x Y坐标
     * @param y Y坐标
     * @param scorebean 分数
     * @return 边缘模型
     */
    private EdgeBean goPie(ScoreBean scorebean, int x, int y, int[][] map0) {
        EdgeBean edgebean = new EdgeBean(3);
        int nowx = x;
        int nowy = y;
        int value = 10;
        buttonMap[x][y].setPiescore(scorebean);
        while (x > 0 && y < size - 1) {//先往上找
            if (map[x][y] != 0 && map[x][y] == map[x - 1][y + 1]) {//相邻两个颜色相比较
                value += 10;//有一样的就加10分
                map0[x - 1][y + 1] = 0;
                buttonMap[x - 1][y + 1].setPiescore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg1x(x - 1);//设置端点
                edgebean.setEdg1y(y + 1);
                break;
            }
            x--;
            y++;
        }
        x = nowx;
        y = nowy;
        while (y > 0 && x < size - 1) {//先往下找
            if (map[x][y] != 0 && map[x][y] == map[x + 1][y - 1]) {//相邻两个颜色相比较
                value += 10;//有一样的就加10分
                map0[x + 1][y - 1] = 0;
                buttonMap[x + 1][y - 1].setPiescore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg2x(x + 1);//设置端点
                edgebean.setEdg2y(y - 1);
                break;
            }
            x++;
            y--;
        }
        scorebean.setEdgebean(edgebean);
        edgebean.setScore(value);
        return edgebean;
    }

    /**
     * 计算与本棋子在斜下线的分数
     *
     * @return 边缘模型
     * @param x Y坐标
     * @param y Y坐标
     * @param scorebean 分数
     */
    private EdgeBean goLa(ScoreBean scorebean, int x, int y, int[][] map0) {
        EdgeBean edgebean = new EdgeBean(4);
        int nowx = x;
        int nowy = y;
        int value = 10;
        buttonMap[x][y].setLascore(scorebean);
        while (x > 0 && y > 0) {
            if (map[x][y] != 0 && map[x][y] == map[x - 1][y - 1]) {//相邻两个颜色相比较
                value += 10;//有一样的就加10分
                map0[x - 1][y - 1] = 0;
                buttonMap[x - 1][y - 1].setLascore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg1x(x - 1);//设置端点
                edgebean.setEdg1y(y - 1);
                break;
            }
            x--;
            y--;
        }
        x = nowx;
        y = nowy;
        while (y < size - 1 && x < size - 1) {
            if (map[x][y] != 0 && map[x][y] == map[x + 1][y + 1]) {//相邻两个颜色相比较
                value += 10;//有一样的就加10分
                map0[x + 1][y + 1] = 0;
                buttonMap[x + 1][y + 1].setLascore(scorebean);
            } else {//已经找到相同颜色线的边缘就跳出
                edgebean.setEdg2x(x + 1);//设置端点
                edgebean.setEdg2y(y + 1);
                break;
            }
            x++;
            y++;
        }
        scorebean.setEdgebean(edgebean);
        edgebean.setScore(value);
        return edgebean;
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

    public boolean isRedTrue() {
        return isRedTrue;
    }

    public ChessBean[][] getButtonMap() {
        return buttonMap;
    }

    public void setButtonMap(ChessBean[][] buttonMap) {
        this.buttonMap = buttonMap;
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public int getDropedCount() {
        return dropedCount;
    }

    public void setDropedCount(int dropedCount) {
        this.dropedCount = dropedCount;
    }

    public void setIsRedTrue(boolean isRedTrue) {
        this.isRedTrue = isRedTrue;
    }
}
