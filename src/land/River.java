package land;

import utils.ImageUtils;

import javax.swing.*;
import java.awt.*;

/**
 * 河流地形，坦克不能跨河
 */
public class River {
    /**
     * 河流图标的宽度与长度
     */
    public static final int RIVER_WIDTH = 33;
    public static final int RIVER_LENGTH = 33;
    /**
     * 河流图标的坐标
     */
    private final int x;
    private final int y;


    /**
     * 河流图标的构造方法
     *
     * @param x 传递构造的横坐标参数
     * @param y 传递构造的纵坐标参数
     */
    public River(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 绘制画出河流图标
     *
     * @param g 传入Graphics实例对象进行绘图
     */
    public void draw(Graphics g) {
        g.drawImage(ImageUtils.RIVER_IMAGE, x, y, null);
    }

    /**
     * 为河流图标绘制一片区域
     *
     * @return 返回指定参数的长方形实例对象
     */
    public Rectangle getRect() {
        return new Rectangle(x, y, RIVER_WIDTH, RIVER_LENGTH);
    }
}
