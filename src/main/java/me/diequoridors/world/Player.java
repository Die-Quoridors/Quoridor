package me.diequoridors.world;

import java.awt.*;

public class Player {

    public static final int[][] playerStartPosMap = {{4, 0}, {4, 8}, {0, 4}, {8, 4}};
    public static final Color[] playerColorMap = {Color.WHITE, Color.BLACK, Color.CYAN, Color.PINK};

    public int x;
    public int y;

    public final Color color;
    private final World world;

    public Player(int x, int y, Color color, World world) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.world = world;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
