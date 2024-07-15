package me.diequoridors.world;

import me.diequoridors.Game;
import me.diequoridors.ui.Renderer;

import java.awt.*;

public class Player {

    public static final int[][] playerStartPosMap = {{4, 0}, {4, 8}, {0, 4}, {8, 4}};
    public static final Color[] playerColorMap = {Color.WHITE, Color.BLACK, Color.CYAN, Color.PINK};
    public static final int[][] playerWinArea = {{0, 8, 8, 1}, {0, 0, 8, 1}, {8, 0, 1, 8}, {0, 0, 1, 8}};
    public static final int[][] playerWallDepotArea = {{0, 0}, {0, 10}, {0, 0}, {10, 0}};
    public static final WallRotation[] playerTargetDirection = {WallRotation.Vertical, WallRotation.Vertical, WallRotation.Horizontal, WallRotation.Horizontal};

    public int x;
    public int y;

    public final Color color;
    private final Game game;
    private final boolean isClone;

    public Player(int x, int y, Color color, Game game) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.game = game;
        this.isClone = false;
    }

    public Player(Player player) {
        this.x = player.x;
        this.y = player.y;
        this.color = player.color;
        this.game = player.game;
        this.isClone = true;
    }

    private boolean isValidMove(int x, int y) {
        for (Wall wall : game.world.walls) {
            if (this.y - y > 0) { // Up
                if (wall.x == this.x - 1 && wall.y == this.y - 1 && wall.rotation == WallRotation.Horizontal) {
                    return false;
                }
                if (wall.x == this.x && wall.y == this.y - 1 && wall.rotation == WallRotation.Horizontal) {
                    return false;
                }
            } else if (this.y - y < 0) { // Down
                if (wall.x == this.x - 1 && wall.y == this.y && wall.rotation == WallRotation.Horizontal) {
                    return false;
                }
                if (wall.x == this.x && wall.y == this.y && wall.rotation == WallRotation.Horizontal) {
                    return false;
                }
            } else if (this.x - x > 0) { // Left
                if (wall.x == this.x - 1 && wall.y == this.y - 1 && wall.rotation == WallRotation.Vertical) {
                    return false;
                }
                if (wall.x == this.x - 1 && wall.y == this.y && wall.rotation == WallRotation.Vertical) {
                    return false;
                }
            } else if (this.x - x < 0) { // Right
                if (wall.x == this.x && wall.y == this.y - 1 && wall.rotation == WallRotation.Vertical) {
                    return false;
                }
                if (wall.x == this.x && wall.y == this.y && wall.rotation == WallRotation.Vertical) {
                    return false;
                }
            }
        }

        return true;
    }

    public void move(int x, int y) {
        int travelDistance = Math.abs(this.x - x) + Math.abs(this.y - y);
        if (travelDistance > 1 || travelDistance == 0) {
            return;
        }
        if (x < 0 || y < 0 || x >= Renderer.gridSize || y >= Renderer.gridSize) {
            return;
        }
        if (!isValidMove(x, y)) {
            return;
        }

        this.x = x;
        this.y = y;

        if (!isClone && game.networkAdapter != null) {
            game.networkAdapter.sendPlayerMove(this);
        }
    }
    
    public void placeWall(int x, int y, WallRotation rotation) {
        long placedWalls = game.world.walls.stream().filter(wall -> wall.placer == this).count();
        if (placedWalls >= game.world.wallLimit) {
            return;
        }
        Wall wall = new Wall(x, y, rotation, this);
        game.world.walls.add(wall);
        if (!isClone && game.networkAdapter != null) {
            game.networkAdapter.sendWallPlace(wall);
        }
    }

    public static int playerToIndex(Player player) {
        for (int i = 0; i < playerColorMap.length; i++) {
            if (player.color.equals(playerColorMap[i])) {
                return i;
            }
        }
        return -1;
    }
}
