package land;

import javax.swing.*;
import java.awt.*;

/**
 * 河流地形
 */
public class River {
    /**
     * 河流图标的宽度与长度
     */
    public static final int RIVER_WIDTH = 33;
    public static final int RIVER_LENGTH = 33;
<<<<<<< HEAD
    private int x, y;
    Image riverImag = new ImageIcon("images/river.gif").getImage();

    /**
     * River的构造方法
     *
     * @param x 传递要构造的长度
     * @param y 传递要构造的宽度
=======
    /**
     * 河流图标的坐标
     */
    private final int x;
    private final int y;
    Image RiverImag = new ImageIcon("images/river.gif").getImage();

    /**
     * 河流图标的构造方法
     * @param x 传递构造的横坐标参数
     * @param y 传递构造的纵坐标参数
>>>>>>> edbacf45cf2668c13bbbd2acb709768afeaa0093
     */
    public River(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
<<<<<<< HEAD
     * 绘制画出河流
     *
     * @param g 定义Graphics对象进行绘图
     */
    public void draw(Graphics g) {
        g.drawImage(riverImag, x, y, null);
    }

    /**
     * 绘制河流长方形
     *
     * @return 返回一个长方形对象
=======
     * 绘制画出河流图标
     * @param g 传入Graphics对象进行绘图
     */
    public void draw(Graphics g){
        g.drawImage(RiverImag,x,y,null);
    }

    /**
     * 为河流图标绘制一片区域
     * @return 返回指定参数的长方形实例对象
>>>>>>> edbacf45cf2668c13bbbd2acb709768afeaa0093
     */
    public Rectangle getRect() {
        return new Rectangle(x, y, RIVER_WIDTH, RIVER_LENGTH);
    }
}
