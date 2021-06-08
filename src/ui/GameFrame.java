package ui;

import home.Home;
import land.*;
import missile.Missile;
import score.*;
import tank.Tank;
import utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameFrame extends JFrame implements ActionListener, Runnable {
    /**
     * 存储坦克的列表
     */
    public static List<Tank> tankList = new ArrayList<Tank>(0);
    /**
     * 存储子弹的列表
     */
    public static List<Missile> missileList = new ArrayList<Missile>(0);
    /**
     * 存储普通墙的列表
     */
    public static List<Wall> wallList = new ArrayList<Wall>(0);
    /**
     * 存储金属墙的列表
     */
    public static List<HardWall> hardWallList = new ArrayList<HardWall>(0);
    /**
     * 存储河流的列表
     */
    public static List<River> riverList = new ArrayList<River>(0);
    /**
     * 存储界面输的列表
     */
    public static List<Tree> treeList = new ArrayList<Tree>(0);
    /**
     * 存储爆炸的列表
     */
    public static List<Explode> explodeList = new ArrayList<Explode>(0);
    //实例化一个基地对象
    public static Home home = new Home();
    public static Tank hero = new Tank(220, 480, true, Tank.Direction.STOP);
    //决定音乐的开关
    public static boolean musicSwitch = false;
    //决定线程的开启与关闭
    public static boolean threadSwitch = true;
    /**
     * 敌人坦克初始数量
     */
    static int enemyCount = 1;
    /**
     * 战斗轮数
     */
    static int round = 1;
    /**
     * 默认地图难度
     */
    static int difficulty = 0;

    {
        MapUtils.changeMap(difficulty);
    }

    //初始化一个坦克
    {
        tankList.add(new Tank(80, 50, false, Tank.Direction.D));
    }
    public GameFrame() {
        //为英雄坦克注册键盘监听事件
        addKeyListener(hero);
        createMenu();//创建菜单

        setTitle("坦克大战");
        setVisible(true);
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Thread thread = new Thread(this);
        thread.start();

        // 在面板上绘制坦克、子弹、墙、基地
        JPanel jPanel = new JPanel() {
            {
                setBackground(Color.gray);
            }

            /**
             * 绘画游戏界面
             * @param g 传入Graphics参数绘制图像
             */
            public void paint(Graphics g) {
                super.paint(g);
                g.drawString("你的分数：" + Missile.getCount(), 10, 20);
                g.drawString("你的生命值：" + hero.getLife(), 10, 40);
                g.drawString("敌人对你的上海:" + Missile.getHurt(), 10, 60);
                g.drawString("第：" + round + " 轮战斗" + "，敌人总数：" + tankList.size(), 10, 80);
                g.drawRect(45, 515, 700, 15);
                g.fillRect(45, 515, hero.getLife() * 7, 15);

                hero.draw(g);//画出英雄坦克
                home.draw(g);//画出自己的基地
                hero.collideWithTanks(tankList);//玩家撞上敌方坦克
                hero.collideWithHome(home);//玩家撞上自己的基地

                //把子弹列表中的子弹绘制出来
                for (int i=0;i<missileList.size();i++) {
                    Missile missile = missileList.get(i);
                    missile.draw(g);
                    missile.hitTanks(tankList);//玩家子弹攻击地方
                    missile.hitTank(hero);//敌方子弹攻击玩家
                    missile.hitHome();//敌方子弹攻击我方基地

                    for (int j=0;j<wallList.size();j++) {
                        Wall w = wallList.get(j);
                        missile.hitWalls(w);//子弹攻击到普通墙上
                    }
                    for (HardWall hw : hardWallList) {
                        missile.hitWalls(hw);//子弹攻击到金属墙上
                    }
                }

                //绘制出坦克列表中的坦克
                for (Tank tank : tankList) {
                    tank.draw(g);

                    //绘制普通墙
                    for (Wall w : wallList) {
                        w.draw(g);
                        tank.collideWithWall(w);//每个坦克撞到普通墙上
                        hero.collideWithWall(w);//玩家撞到普通墙上
                    }

                    //绘制金属墙
                    for (HardWall hw : hardWallList) {
                        hw.draw(g);
                        tank.collideWithHardWall(hw);//每个坦克撞到金属墙上
                        hero.collideWithHardWall(hw);//玩家撞到金属墙上
                    }

                    //绘制河流
                    for (River river : riverList) {
                        river.draw(g);
                        tank.collideWithRiver(river);//每个坦克撞到河流
                    }
                    //绘制丛林
                    for (Tree tree : treeList) {
                        tree.draw(g);
                    }

                    tank.collideWithTanks(tankList);//敌方坦克撞到自己方坦克上
                    tank.collideWithHome(home);//地方坦克撞到我方基地
                }

                //绘制出所有爆炸
                for (Explode explode : explodeList) {
                    explode.draw(g);
                }

                //没关卡敌人的设置
                if (tankList.size() == 0 && round < 6) {
                    for (int i = 0; i < enemyCount * 2; i++) {
                        Tank t = null;
                        if (i < 2) {
                            t = new Tank(100 + 70 * i, 50, false, Tank.Direction.L);
                        } else if (i > 3) {
                            t = new Tank(510, i * 50 + 20, false, Tank.Direction.R);
                        } else {
                            t = new Tank(50 + 50 * i, 500, false, Tank.Direction.D);
                        }
                        tankList.add(t);
                    }
                    enemyCount++;
                    round++;
                }
            }
        };
        add(jPanel);
    }

    /**
     * 设置地图难度
     *
     * @param difficulty 传入地图难度参数
     */
    public static void setDifficulty(int difficulty) {
        GameFrame.difficulty = difficulty;
    }

    public static void main(String[] args) {
        new GameFrame();
    }

    public void createMenu() {
        //菜单栏
        JMenuBar jMenuBar = new JMenuBar();
        //菜单项
        JMenu jMenu1 = new JMenu("游戏");
        JMenu jMenu2 = new JMenu("历史记录");
        JMenu jMenu3 = new JMenu("帮助");
        JMenu jMenu4 = new JMenu("游戏难度");
        //菜单项按钮
        JMenuItem jMenuItem1 = new JMenuItem("暂停/继续");
        JMenuItem jMenuItem2 = new JMenuItem("继续");
        JMenuItem jMenuItem3 = new JMenuItem("背景音乐开/关");
        JMenuItem jMenuItem4 = new JMenuItem("最高记录");
        JMenuItem jMenuItem5 = new JMenuItem("玩家得分记录");
        JMenuItem jMenuItem6 = new JMenuItem("关于游戏");
        JMenuItem jMenuItem7 = new JMenuItem("普通模式");
        JMenuItem jMenuItem8 = new JMenuItem("人间模式");
        JMenuItem jMenuItem9 = new JMenuItem("地狱模式");
        JMenuItem jMenuItem10 = new JMenuItem("重新开始");
        JMenuItem jMenuItem11 = new JMenuItem("返回到主界面");
        JMenuItem jMenuItem12 = new JMenuItem("自定义地图");

        //添加菜单
        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);
        jMenuBar.add(jMenu4);
        jMenuBar.add(jMenu3);

        //设置游戏菜单项
        jMenu1.add(jMenuItem1);
        jMenu1.add(jMenuItem10);
        jMenu1.add(jMenuItem3);
        jMenu1.add(jMenuItem11);
        jMenu2.add(jMenuItem4);
        jMenu2.add(jMenuItem5);
        jMenu3.add(jMenuItem6);
        jMenu3.add(jMenuItem12);

        jMenu4.add(jMenuItem7);
        jMenu4.add(jMenuItem8);
        jMenu4.add(jMenuItem9);

        //为按钮添加监听事件
        jMenuItem1.addActionListener(this);
        jMenuItem1.setAccelerator(KeyStroke.getKeyStroke("F1"));
        jMenuItem1.setActionCommand("stop");

        jMenuItem2.addActionListener(this);
        jMenuItem2.setActionCommand("continue");

        jMenuItem3.addActionListener(this);
        jMenuItem3.setAccelerator(KeyStroke.getKeyStroke("F2"));
        jMenuItem3.setActionCommand("music");

        jMenuItem4.addActionListener(this);
        jMenuItem4.setActionCommand("rank");

        jMenuItem5.addActionListener(this);
        jMenuItem5.setActionCommand("history");

        jMenuItem6.addActionListener(this);
        jMenuItem6.setActionCommand("help");

        jMenuItem7.addActionListener(this);
        jMenuItem7.setActionCommand("difficulty1");

        jMenuItem8.addActionListener(this);
        jMenuItem8.setActionCommand("difficulty2");

        jMenuItem9.addActionListener(this);
        jMenuItem9.setActionCommand("difficulty3");

        jMenuItem10.addActionListener(this);
        jMenuItem10.setActionCommand("restart");

        jMenuItem11.addActionListener(this);
        jMenuItem11.setActionCommand("back");

        jMenuItem12.addActionListener(this);
        jMenuItem12.setActionCommand("diy");

        //将菜单放在窗体上
        this.setJMenuBar(jMenuBar);

    }

    public void run() {
        //每隔20毫秒重新画图
        while (threadSwitch) {
            try {
                if (Missile.getCount() == 31 || !hero.isAlive()||!home.isAlive()) {
                    threadSwitch = false;
                    if (Missile.getCount() == 31) {
                        new Thread(new MusicUtils(MusicUtils.PLAY_WIN)).start();
                        JOptionPane.showMessageDialog(null, "赢得胜利");
                        //判断是否是最高分
                        maxScore();
                    }
                    if (!hero.isAlive()||!home.isAlive()) {
                        new Thread(new MusicUtils(MusicUtils.PLAY_LOSE)).start();
                        JOptionPane.showMessageDialog(null, "游戏结束！");
                        maxScore();
                    }
                    try {
                        FileWriter fileWriter = new FileWriter("txt/score.txt");
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write("\t\t" + StartFrame.getUserName() + "获得的分数为：" + Missile.getCount() + "\n");
                        bufferedWriter.close();
                        fileWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
            repaint();
        }
    }

    /**
     * 最高分判定与写入
     */
    public void maxScore() {
        String str = HighestScore.readText();
        String[] newStr = str.split(":");
        int bullet = Integer.parseInt(newStr[1]);
        if (Missile.getCount() > bullet) {
            bullet = Missile.getCount();
            HighestScore.write(StartFrame.getUserName(), bullet);
            if (StartFrame.getUserName().equals("匿名玩家")) {
                JOptionPane.showMessageDialog(null, "恭喜您获得了最高分" + bullet);
            } else {
                JOptionPane.showMessageDialog(null, StartFrame.getUserName() + ". " + "恭喜您获得了最高分" + bullet);
            }
        }
    }

    /**
     * 初始化所有变量
     *
     * @param response 传入是否需要初始化变量的参数
     */
    public void initVariable(int response) {
        if (response == 0) {
            threadSwitch = true;
            Home.setAlive(true);//重新激活基地
            Home.setHomeLocation(290, 250);//重置基地位置
            hero.setLife(100);//重新设置英雄坦克血量
            hero.setAlive(true);//冲洗激活英雄坦克
            hero.setTankLocation(220, 480, Tank.Direction.STOP);//重置英雄坦克位置
            round = 1;//重置战斗轮数
            enemyCount = 1;//重置地方坦克数量
            Tank.setBotSpeed(3);//重置地方坦克速度
            Tank.setTankColor(0);//重置坦克颜色
            Missile.setCount(0);//重置得分
            Missile.setMissileColor(0);//重置子弹颜色
            Missile.setBotSpeed(20);//设置地方坦克子弹速度
            Missile.setHurt(20);//设置地方坦克伤害
            tankList.clear();//坦克清空
            missileList.clear();//子弹清空
            wallList.clear();//普通墙清空
            hardWallList.clear();//金属墙清空
            riverList.clear();//河流清空
            treeList.clear();//丛林清空
            setDifficulty(0);//重置地图难度
            this.dispose();
            new GameFrame();
        } else {
            threadSwitch = true;
            new Thread(this).start();//线程启动
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("stop")) {
            if (threadSwitch) {
                //停止线程
                threadSwitch = false;
            } else {
                threadSwitch = true;
                //启动线程
                new Thread(this).start();
            }
        }
        if (e.getActionCommand().equals("commit")) {
            if (!threadSwitch) {
                threadSwitch = true;
                //线程启动
                new Thread(this).start();
            }
        }
        if (e.getActionCommand().equals("music")) {
            if (!musicSwitch) {
                MusicUtils.playMusic();
            } else {
                MusicUtils.stopMusic();
            }
            musicSwitch = !musicSwitch;
        }
        if (e.getActionCommand().equals("restart")) {
            threadSwitch = false;
            Object[] options = {"确定", "取消"};
            int response = JOptionPane.showOptionDialog(this, "您确认要开始游戏！", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            initVariable(response);
        }
        if (e.getActionCommand().equals("back")) {
            threadSwitch = false;
            Object[] options = {"确定", "取消"};
            int response = JOptionPane.showOptionDialog(this, "您确认要返回到主界面！", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            initVariable(response);
        }
        if (e.getActionCommand().equals("difficulty1")) {
            tankList.clear();
            missileList.clear();
            wallList.clear();
            Tank.setBotSpeed(7);//改变地方坦克速度
            Tank.setTankColor(1);//改变地方坦克颜色
            Missile.setBotSpeed(8);//改变地方子弹速度
            Missile.setMissileColor(1);//改变地方坦克颜色
            setDifficulty(1);//改变地图难度
            Home.setHomeLocation(370, 500);//重置基地位置
            hero.setTankLocation(220, 480, Tank.Direction.STOP);//重置英雄坦克位置
            this.dispose();
            new GameFrame();
        }
        if (e.getActionCommand().equals("difficulty2")) {
            tankList.clear();
            missileList.clear();
            wallList.clear();
            Tank.setBotSpeed(9);//改变地方坦克速度
            Tank.setTankColor(2);//改变地方坦克颜色
            Missile.setBotSpeed(10);//改变地方子弹速度
            Missile.setMissileColor(2);//改变地方坦克颜色
            Missile.setHurt(30);//设置敌人坦克伤害
            setDifficulty(2);//改变地图难度
            Home.setHomeLocation(370, 250);//重置基地位置
            hero.setTankLocation(220, 480, Tank.Direction.STOP);//重置英雄坦克位置
            this.dispose();
            new GameFrame();
        }
        if (e.getActionCommand().equals("difficulty3")) {
            tankList.clear();
            missileList.clear();
            Tank.setBotSpeed(11);//改变地方坦克速度
            Tank.setTankColor(3);//改变地方坦克颜色
            Missile.setBotSpeed(12);//改变地方子弹速度
            Missile.setMissileColor(3);//改变地方坦克颜色
            Missile.setHurt(40);//设置敌人坦克伤害
            setDifficulty(3);//改变地图难度
            Home.setHomeLocation(390, 250);//重置基地位置
            hero.setTankLocation(220, 480, Tank.Direction.STOP);//重置英雄坦克位置
            this.dispose();
            new GameFrame();
        }
        if (e.getActionCommand().equals("history")) {
            try {
                new ScoreFrame();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getActionCommand().equals("rank")) {
            String str = HighestScore.readText();
            String[] newStr = str.split(":");
            int bullet = Integer.parseInt(newStr[1]);
            if (newStr[0].equals("")) {
                JOptionPane.showMessageDialog(null, "匿名玩家" + "获得了最高分：" + bullet);
            } else {
                JOptionPane.showMessageDialog(null, newStr[0] + "获得了最高分：" + bullet);
            }
        }
        if (e.getActionCommand().equals("help")) {
            threadSwitch = false;//停止线程
            JOptionPane.showMessageDialog(null, "W、向上，A、向左，S、向下，D、向右，P、发射炮弹，R、加血" + "\n", "提示", JOptionPane.INFORMATION_MESSAGE);
            threadSwitch = true;
            new Thread(this).start();//线程启动
        }
        if (e.getActionCommand().equals("diy")) {
            tankList.clear();
            missileList.clear();
            wallList.clear();
            hardWallList.clear();
            riverList.clear();
            treeList.clear();
            Home.setAlive(true);
            hero.setAlive(true);
            this.dispose();
            new DiyMapUtils();
        }
    }
}
