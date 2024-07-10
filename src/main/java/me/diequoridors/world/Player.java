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
        int travelDistance = Math.abs(this.x - x) + Math.abs(this.y - y);
        if (travelDistance > 1 || travelDistance == 0) {
            return;
        }

        for (Wall wall : world.walls) {
            if (this.y - y > 0) { // Up
                if (wall.x == this.x - 1 && wall.y == this.y - 1 && wall.rotation == WallRotation.Horizontal) {
                    return;
                }
                if (wall.x == this.x && wall.y == this.y - 1 && wall.rotation == WallRotation.Horizontal) {
                    return;
                }
            } else if (this.y - y < 0) { // Down
                if (wall.x == this.x - 1 && wall.y == this.y && wall.rotation == WallRotation.Horizontal) {
                    return;
                }
                if (wall.x == this.x && wall.y == this.y && wall.rotation == WallRotation.Horizontal) {
                    return;
                }
            } else if (this.x - x > 0) { // Left
                if (wall.x == this.x - 1 && wall.y == this.y - 1 && wall.rotation == WallRotation.Vertical) {
                    return;
                }
                if (wall.x == this.x - 1 && wall.y == this.y && wall.rotation == WallRotation.Vertical) {
                    return;
                }
            } else if (this.x - x < 0) { // Right
                if (wall.x == this.x && wall.y == this.y - 1 && wall.rotation == WallRotation.Vertical) {
                    return;
                }
                if (wall.x == this.x && wall.y == this.y && wall.rotation == WallRotation.Vertical) {
                    return;
                }
            }
        }
        this.x = x;
        this.y = y;
    }
    
    public boolean placeWall(int x, int y,  WallRotation rotation) {
        long placedWalls = world.walls.stream().filter(wall -> wall.placer == this).count();
        if (placedWalls >= world.wallLimit) {
            return false;
        }
        world.walls.add(new Wall(x, y, rotation, this));
        return true;
    }
}
