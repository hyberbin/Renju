/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jplus.action;

import com.jplus.model.EdgeBean;
import com.jplus.model.StrategyBean;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 策略
 *
 * @author hyberbin
 */
public class Strategy extends Thread {

    private LinkedList<EdgeBean> MainRlist;//红棋边缘（可下位置）
    private LinkedList<EdgeBean> MainBlist;//黑棋边缘（可下位置）
    private MainFrame mainframe;
    private boolean isRedTrue;
    private int[][] map;

    /**
     * 构造方法复制一个棋盘
     */
    public Strategy(MainFrame mf) {
        mainframe = mf;        
        isRedTrue = mainframe.getChessmap().isRedTrue();
        MainBlist = mf.getBlackEdgelist();
        MainRlist = mf.getRedEdgelist();
        map = mf.getChessmap().getMap();
    }

    /**
     * 获得所有边缘中有可能两个连在一起的情况
     *
     * @param list 可放置的位置
     * @return
     */
    private LinkedList<StrategyBean> getPossible(LinkedList<EdgeBean> list) {
        LinkedList<StrategyBean> stragList = new LinkedList<StrategyBean>();
        for (EdgeBean eb : list) {
            int score = eb.leftAdd();
            int x = eb.getEdg1x();
            int y = eb.getEdg1y();
            if (score >= 40 && map[x][y] == 0) {
                eb.getLeftEdge().setRightEdge(null);//清空，以免重复
                StrategyBean sb = new StrategyBean(x, y, eb);
                sb.setScore(score);
                stragList.add(sb);
            }
            score = eb.rightAdd();
            x = eb.getEdg2x();
            y = eb.getEdg2y();
            if (score >= 40 && map[x][y] == 0) {
                eb.getRightEdge().setLeftEdge(null);//清空，以免重复
                StrategyBean sb = new StrategyBean(x, y, eb);
                sb.setScore(score);
                stragList.add(sb);
            }
        }
        Collections.sort(stragList);
        return stragList;
    }

    /**
     * 中间攻击或者防守
     *
     * @return
     */
    private StrategyBean middleAttack() {
        LinkedList<StrategyBean> Rlist = getPossible(MainRlist);
        LinkedList<StrategyBean> Blist = getPossible(MainBlist);
        int rsbs = Rlist.size() == 0 ? 0 : Rlist.getFirst().getScore();
        int bsbs = Blist.size() == 0 ? 0 : Blist.getFirst().getScore();
        if (isRedTrue) {
            if (rsbs > 40) {
                return Rlist.getFirst();//如果中间攻击他他后会死
            }
            if (bsbs > 40) {
                return Blist.getFirst();//如果我遭到中间攻击后会死快堵上
            }
            if (!Blist.isEmpty() && MainBlist.getFirst().getScore() < Blist.getFirst().getScore()) {//如果他最大风险小于中间攻击
                return Blist.getFirst();
            }
        } else {
            if (bsbs > 40) {
                return Blist.getFirst();//如果中间攻击后会死
            }
            if (rsbs > 40) {
                return Rlist.getFirst();//如果我中间攻击后会死快堵上
            }
            if (!Rlist.isEmpty() && MainRlist.getFirst().getScore() < Rlist.getFirst().getScore()) {//如果他最大风险小于中间攻击
                return Rlist.getFirst();
            }
        }
        return null;
    }

    /**
     * 第一次判断 有可能出击敌人 也有可能挽救自己
     *
     * @return 攻击策略
     */
    private StrategyBean attack() {
        if (MainBlist.size() == 0 || MainRlist.size() == 0) {
            return randomDefensive();
        }
        EdgeBean sb = MainBlist.getFirst();
        EdgeBean sr = MainRlist.getFirst();
        if (isRedTrue) {//轮到红棋下
            if (sr.getScore() > 35) {//自身为40，那么一步搞定它
                return sr.getUseable() == 1 ? new StrategyBean(sr.getEdg1x(), sr.getEdg1y(), null) : new StrategyBean(sr.getEdg2x(), sr.getEdg2y(), null);
            } else if (sb.getScore() > 35) {//如果黑棋为40，赶快堵上
                return sb.getUseable() == 1 ? new StrategyBean(sb.getEdg1x(), sb.getEdg1y(), null) : new StrategyBean(sb.getEdg2x(), sb.getEdg2y(), null);
            }
        }
        if (!isRedTrue) {//轮到黑棋下
            if (sb.getScore() > 35) {//自身为40，那么一步搞定它
                return sb.getUseable() == 1 ? new StrategyBean(sb.getEdg1x(), sb.getEdg1y(), null) : new StrategyBean(sb.getEdg2x(), sb.getEdg2y(), null);
            } else if (sr.getScore() > 35) {//如果红棋为40，赶快堵上
                return sr.getUseable() == 1 ? new StrategyBean(sr.getEdg1x(), sr.getEdg1y(), null) : new StrategyBean(sr.getEdg2x(), sr.getEdg2y(), null);
            }
        }
        return null;
    }

    /**
     * 随机策略
     */
    private StrategyBean randomStrategy() {
        System.out.println("随机策略");
        int x = -1;
        int y = -1;
        do {
            x = getRandom(MainFrame.size);
            y = getRandom(MainFrame.size);
        } while (map[x][y] != 0);
        StrategyBean sb = new StrategyBean(x, y, null);
        return sb;

    }

    /**
     * 执行策略
     *
     * @param sb 策略模型
     */
    public void execute(StrategyBean sb) {
        if (!mainframe.getChessmap().setChess(sb.getX(), sb.getY())) {
            System.out.println("无效策略！:" + sb.getX() + "------" + sb.getY());
            MainBlist.remove(sb.getEdgebean());
            MainRlist.remove(sb.getEdgebean());
            start();
        }
    }

    /**
     * 专门针对赵鹏的诱惑
     *
     * @return
     */
    private StrategyBean killZoffe() {
        LinkedList<EdgeBean> list = isRedTrue ? MainBlist : MainRlist;
        StrategyBean llist = null;
        if (list.getFirst().getScore() < 30) { //如果是10分不鸟他
            llist = getStrategy(isRedTrue ? MainRlist : MainBlist);
        }
        return llist;
    }

    private StrategyBean findMaxUseed() {
        if(!(MainRlist.getFirst().getScore()<30||MainBlist.getFirst().getScore()<30)) return null;
        int[][] map0 = new int[MainFrame.size][MainFrame.size];
        int maxx = -1;
        int maxy = -1;
        int max = -1;
        for (int i = 0; i < map[0].length; i++) {
            System.arraycopy(map[i], 0, map0[i], 0, map[0].length);
        }
        LinkedList<EdgeBean> list = isRedTrue ? MainBlist : MainRlist;
        for (EdgeBean eb : list) {
            int x1 = eb.getEdg1x();
            int x2 = eb.getEdg2x();
            int y1 = eb.getEdg1y();
            int y2 = eb.getEdg2y();
            if (((eb.getUseable() == 1 || eb.getUseable() == 3) && ++map0[x1][y1] > max)) {
                max = map0[x1][y1];
                maxx = x1;
                maxy = y1;
            }
            if (eb.getUseable() > 1 && ++map0[x2][y2] > max) {
                max = map0[x2][y2];
                maxx = x2;
                maxy = y2;
            }
        }
        return max < 3 ? null : new StrategyBean(maxx, maxy, null);
    }

    /**
     * 随机防守策略
     */
    private StrategyBean randomDefensive() {
        LinkedList<EdgeBean> list = isRedTrue ? MainBlist : MainRlist;
        if (list.size() == 0) {
            return randomStrategy();
        }
        return getStrategy(list);
    }

    /**
     * 随机取得最优防守策略 按照分数排序，如果有多个分数一样那么随机防一个
     */
    private StrategyBean getStrategy(LinkedList<EdgeBean> list) {
        LinkedList<StrategyBean> samescore = getStrategyList(list);//等效的策略 
        StrategyBean finalStrategy = samescore.remove(getRandom(samescore.size()));//最终最优策略
        if (finalStrategy.getEdgebean().getUseable() != 3) {//如果为3的边缘还有一个
            finalStrategy.getEdgebean().setScore(0);//已经不能成为策略，边缘没了
            list.remove(finalStrategy.getEdgebean());
        } else if (finalStrategy.getEdgebean().getEdg1x() == finalStrategy.getX() && finalStrategy.getEdgebean().getEdg1y() == finalStrategy.getY()) {
            finalStrategy.getEdgebean().setEdg1x(-1);//已经不能成为策略，边缘没了
            finalStrategy.getEdgebean().setEdg1y(-1);
            finalStrategy.getEdgebean().setUseable(2);//第二个端点还可以用
        } else {
            finalStrategy.getEdgebean().setEdg2x(-1);//已经不能成为策略，边缘没了
            finalStrategy.getEdgebean().setEdg2y(-1);
            finalStrategy.getEdgebean().setUseable(1);//第一个端点还可以用
        }
        return finalStrategy;
    }

    /**
     * 运行算法选出决策并执行
     */
    @Override
    public void run() {
        isRedTrue = mainframe.getChessmap().isRedTrue();
        long t = System.currentTimeMillis();
        StrategyBean strate;
        do {
            if (mainframe.getChessmap().getDropedCount() <=2) {//第一步采取随机策略
                strate = randomDefensive();
                break;
            }
            strate = attack();
            if (strate != null) {//看能不能致命一击或者挽救自己
                System.out.println("出击或者挽救！");
                break;
            }  //中间攻击策略
            strate = middleAttack();
            if (strate != null) {//看能不能中间出击或者挽救
                System.out.println("中间出击或者挽救！");
                break;
            }
            strate = findMaxUseed();
            if (isRedTrue && strate != null) {//看有没有共用边缘比较多的
                System.out.println("共用边缘比较多！");
                break;
            }
            strate = killZoffe();
            if (mainframe.getChessmap().getDropedCount() >4 && strate != null) {//看能不能防赵鹏
                System.out.println("防赵鹏！");
                break;
            }
            System.out.println("随机防守策略");
            strate = randomDefensive();//没办法只能按最大的来了
            break;
        } while (false);
        execute(strate);
        System.out.println("本次决策用时：" + (System.currentTimeMillis() - t) + "毫秒！");
        if (mainframe.getSteepbean().isIsBothRobotTrue()) {//如果机器人和机器人下那么再决策一下
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
            }
            run();
        }

    }

    /**
     * 获得最好的策略表
     *
     * @param list 可下的棋子边缘
     * @return
     */
    private LinkedList<StrategyBean> getStrategyList(LinkedList<EdgeBean> list) {
        LinkedList<StrategyBean> samescore = new LinkedList<StrategyBean>();//等效的策略       
        int maxscore = list.getFirst().getScore();
        for (EdgeBean cb : list) {
            if (cb.getScore() == maxscore) {
                if (cb.getUseable() == 1) {//一条线上的第一个端点可用
                    StrategyBean sb = new StrategyBean(cb.getEdg1x(), cb.getEdg1y(), cb);
                    samescore.add(sb);
                } else if (cb.getUseable() == 2) {//一条线上的第二个端点可用
                    StrategyBean sb = new StrategyBean(cb.getEdg2x(), cb.getEdg2y(), cb);
                    samescore.add(sb);
                    cb.setScore(0);
                } else if (cb.getUseable() == 3) {//一条线上的端点都可用
                    StrategyBean sb = new StrategyBean(cb.getEdg2x(), cb.getEdg2y(), cb);
                    samescore.add(sb);
                    StrategyBean sb2 = new StrategyBean(cb.getEdg1x(), cb.getEdg1y(), cb);
                    samescore.add(sb2);
                }
            } else {
                break;
            }
        }
        return samescore;
    }

    /**
     * 从size个结果中选取一个
     *
     * @param size size个可能性
     * @return
     */
    private int getRandom(int size) {
        return (int) (Math.random() * size);
    }
}
