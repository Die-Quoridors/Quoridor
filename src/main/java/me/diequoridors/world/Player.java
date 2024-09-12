package me.diequoridors.world;

import me.diequoridors.Game;
import me.diequoridors.ui.Renderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class Player {

    public static final int[][] playerStartPosMap = {{4, 0}, {4, 8}, {0, 4}, {8, 4}};
    public static final Color[] playerColorMap = {Color.WHITE, Color.BLACK, Color.CYAN, Color.PINK};
    public static final int[][] playerWinArea = {{0, 8, 8, 1}, {0, 0, 8, 1}, {8, 0, 1, 8}, {0, 0, 1, 8}};
    public static final int[][] playerWallDepotArea = {{0, 0}, {0, 10}, {0, 0}, {10, 0}};
    public static final WallRotation[] playerTargetDirection = {WallRotation.Vertical, WallRotation.Vertical, WallRotation.Horizontal, WallRotation.Horizontal};

    public int x;
    public int y;

    public final Color color;
    public final Game game;
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

        // check Walls
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

        int travelX = x - this.x;
        int travelY = y - this.y;

        // check target is free
        Optional<Player> collisionPlayer = game.world.players.stream().filter(player -> player.x == x && player.y == y && !player.isClone).findFirst();
        if (collisionPlayer.isPresent()) {
            return false;
        }

        // check travel no more than 2 or no move
        int travelDistance = Math.abs(this.x - x) + Math.abs(this.y - y);
        if (travelDistance == 0 || travelDistance > 2) {
            return false;
        }

        // finish normal moves
        if (travelDistance == 1) {
            return true;
        }

        // == check special moves ==
        int mX = this.x + (travelX / 2);
        int mY = this.y + (travelY / 2);

        // check player skipping
        Optional<Player> skipPlayer = game.world.players.stream().filter(player -> player.x == mX && player.y == mY && !player.isClone).findFirst();
        if (Math.abs(travelX) == 2 || Math.abs(travelY) == 2) {
            return skipPlayer.isPresent();
        }

        // check diagonals
        if (Math.abs(travelX) == 1 && Math.abs(travelY) == 1) {
            Optional<Player> diaPlayer1 = game.world.players.stream().filter(player -> player.x == (this.x + travelX) && player.y == this.y && !player.isClone).findFirst();
            Optional<Player> diaPlayer2 = game.world.players.stream().filter(player -> player.x == this.x && player.y == (this.y + travelY) && !player.isClone).findFirst();

            ArrayList<Player> p = new ArrayList<>();
            diaPlayer1.ifPresent(p::add);
            diaPlayer2.ifPresent(p::add);

            for (Player diaPlayer : p) {
                int pDiffX = diaPlayer.x - this.x;
                int pDiffY = diaPlayer.y - this.y;
                System.out.println("Dia Player X/Y:" + diaPlayer.x + "/" + diaPlayer.y + " | Diff X/Y:" + pDiffX + "/" + pDiffY);
                WallRotation reqWallDir = pDiffX == 0 ? WallRotation.Horizontal : WallRotation.Vertical;
                System.out.println("ReqWallDir: " + reqWallDir);
                boolean wallMatch = false;
                for (Wall wall : game.world.walls) {
                    if (wall.rotation != reqWallDir) {
                        continue;
                    }

                    if (pDiffX == 0) {
                        int offY = pDiffY > 0 ? pDiffY - 1 : pDiffY;
                        if ((wall.x + 1) == diaPlayer.x && wall.y == (diaPlayer.y + offY)) {
                            wallMatch = true;
                            break;
                        }
                        if (wall.x == diaPlayer.x && wall.y == (diaPlayer.y + offY)) {
                            wallMatch = true;
                            break;
                        }
                    } else if (pDiffY == 0) {
                        int offX = pDiffX > 0 ? pDiffX - 1 : pDiffX;
                        if (wall.x == (diaPlayer.x + offX) && wall.y == diaPlayer.y) {
                            wallMatch = true;
                            break;
                        }
                        if (wall.x == (diaPlayer.x + offX) && (wall.y + 1) == diaPlayer.y) {
                            wallMatch = true;
                            break;
                        }
                    }
                }

                if (wallMatch) {
                    return true;
                }
            }
        }

        return false;
    }

    public void move(int x, int y) {
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
        boolean placementValid = Wall.placementValid(game.world, x, y, rotation);
        if (!placementValid) {
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
