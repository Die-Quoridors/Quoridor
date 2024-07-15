package me.diequoridors.ui;

import me.diequoridors.Game;
import me.diequoridors.world.Player;
import me.diequoridors.world.Wall;
import me.diequoridors.world.WallRotation;
import me.diequoridors.world.World;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Renderer {

    public static final int gridSize = 9;
    private static final float cellPadding = 0.1f;
    private static final int wallWidth = 10;

    private final World world;
    private final Game game;

    public final Frame frame = new Frame();
    public final Canvas canvas = new Canvas();

    private final Timer frameTimer = new Timer();

    public Renderer(Game game) {
        this.game = game;
        this.world = game.world;

        frame.setVisible(true);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.setTitle("Quoridor");
        frame.setSize(new Dimension(880, 880 + frame.getInsets().top));

        canvas.setVisible(true);
        canvas.setFocusable(false);
        canvas.setBackground(Color.WHITE);
        canvas.setBounds(0, frame.getInsets().top, frame.getWidth(), frame.getHeight());
        canvas.setSize(frame.getSize());

        frame.add(canvas);
        canvas.createBufferStrategy(2);

        frameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                renderFrame();
            }
        }, 1000 / 60, 1000 / 60);
    }

    public void exit() {
        frameTimer.cancel();
        frame.dispose();
    }

    private int getCellSize() {
        int windowSize = Math.min(frame.getWidth(), frame.getHeight() - frame.getInsets().top);
        return windowSize / (gridSize + 2);
    }

    public int coordinatesToScreen(int coord) {
        int cellSize = getCellSize();
        return coord * cellSize + getCellSize();
    }

    public int screenToCoordinates(int screen, boolean wall) {
        int cellSize = getCellSize();
        int offset = wall ? cellSize / 2 : 0;
        int coord = (screen - cellSize - offset) / cellSize;
        return Math.min(gridSize, Math.max(0, coord));
    }

    public Player screenToWallPlayer(int screenX, int screenY) {
        int cellSize = getCellSize();
        int x = screenX / cellSize;
        int y = screenY / cellSize;

        System.out.print(x);
        System.out.print("/");
        System.out.println(y);

        for (int i = 0; i < world.players.size(); i++) {
            int[] wallStart = Player.playerWallDepotArea[i];
            WallRotation targetDirection = Player.playerTargetDirection[i];

            if (
                    (targetDirection == WallRotation.Vertical && y == wallStart[1])
                    || (targetDirection == WallRotation.Horizontal && x == wallStart[0])
            ) {
                return world.players.get(i);
            }
        }

        return null;
    }

    private void renderFrame() {
        Graphics graphics = canvas.getBufferStrategy().getDrawGraphics();
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int windowSize = Math.min(frame.getWidth(), frame.getHeight() - frame.getInsets().top);
        int cellSize = getCellSize();
        int usableCellSize = (int) (cellSize * (1 - 2 * cellPadding));
        int cellOffset = (int) (cellSize * cellPadding);
        int frameHeight = frame.getHeight() - frame.getInsets().top;
        int frameWidth = frame.getWidth();

        canvas.setBounds(0, frame.getInsets().top, windowSize, windowSize);
        canvas.setSize(new Dimension(windowSize, windowSize));

        // === render Grid ===
        for (int i = 0; i <= gridSize; i++) {
            if (i == 0 || i == gridSize) {
                graphics.setColor(Color.BLACK);
            } else {
                graphics.setColor(Color.LIGHT_GRAY);
            }
            graphics.drawLine(i * cellSize + cellSize, cellSize, i * cellSize + cellSize, frameHeight - cellSize);
            graphics.drawLine(cellSize, i * cellSize + cellSize, frameWidth - cellSize, i * cellSize + cellSize);
        }

        // === render Players ===
        for (Player player : world.players) {
            int x = player.x * cellSize + cellOffset + cellSize;
            int y = player.y * cellSize + cellOffset + cellSize;

            graphics.setColor(player.color);
            graphics.fillOval(x, y, usableCellSize, usableCellSize);
            graphics.setColor(Color.BLACK);
            graphics.drawOval(x, y, usableCellSize, usableCellSize);
        }

        // === render phantom Player ===
        Player phantomPlayer = game.mouseListener.phantomPlayer;
        if (phantomPlayer != null) {
            int x = phantomPlayer.x * cellSize + cellOffset + cellSize;
            int y = phantomPlayer.y * cellSize + cellOffset + cellSize;

            graphics.setColor(phantomPlayer.color);
            graphics.fillOval(x, y, usableCellSize, usableCellSize);
            graphics.setColor(Color.BLACK);
            graphics.drawOval(x, y, usableCellSize, usableCellSize);
        }

        // === render Walls ===
        for (Wall wall : world.walls) {
            int x = wall.x * cellSize + (wall.rotation == WallRotation.Horizontal ? 0 : cellSize - (wallWidth / 2)) + cellSize;
            int y = wall.y * cellSize + (wall.rotation == WallRotation.Horizontal ? (cellSize - (wallWidth / 2)) : 0) + cellSize;

            int width = wall.rotation == WallRotation.Vertical ? wallWidth : (cellSize * 2);
            int height = wall.rotation == WallRotation.Horizontal ? wallWidth : (cellSize * 2);

            graphics.setColor(Color.BLACK);
            graphics.fillRect(x, y, width, height);
        }

        // === render phantom Wall

        Wall phantomWall = game.mouseListener.phantomWall;
        if (phantomWall != null) {
            int x = phantomWall.x * cellSize + (phantomWall.rotation == WallRotation.Horizontal ? 0 : cellSize - (wallWidth / 2)) + cellSize;
            int y = phantomWall.y * cellSize + (phantomWall.rotation == WallRotation.Horizontal ? (cellSize - (wallWidth / 2)) : 0) + cellSize;

            int width = phantomWall.rotation == WallRotation.Vertical ? wallWidth : (cellSize * 2);
            int height = phantomWall.rotation == WallRotation.Horizontal ? wallWidth : (cellSize * 2);

            graphics.setColor(Color.BLACK);
            graphics.fillRect(x, y, width, height);
        }

        // === render non placed Walls
        for (int i = 0; i < world.players.size(); i++) {
            Player player = world.players.get(i);
            long placedWallCount = world.walls.stream().filter(wall -> wall.placer == player).count();
            long placableWallsCount = world.wallLimit - placedWallCount;
            int[] wallDepotStart = Player.playerWallDepotArea[i];
            WallRotation playerRotation = Player.playerTargetDirection[i];
            int wallSpacing = placableWallsCount <= (gridSize + 1) ? cellSize : cellSize / (int) (Math.ceil(((double) (placableWallsCount - 1) / gridSize)));

            for (int w = 0; w < placableWallsCount; w++) {

                int wOffset = w * wallSpacing;
                int xOff = playerRotation == WallRotation.Vertical ? wOffset : 0;
                int yOff = playerRotation == WallRotation.Vertical ? 0 : wOffset;

                int x = wallDepotStart[0] * cellSize + xOff + cellSize;
                int y = wallDepotStart[1] * cellSize + yOff + cellSize;

                int width = playerRotation == WallRotation.Vertical ? wallWidth : cellSize;
                int height = playerRotation == WallRotation.Horizontal ? wallWidth : cellSize;

                int xMv = playerRotation == WallRotation.Horizontal ? 0 : wallWidth / 2;
                int yMv = playerRotation == WallRotation.Vertical ? 0 : wallWidth / 2;

                graphics.setColor(Color.GRAY);
                graphics.fillRect(x + xMv - width, y + yMv - height, width, height);
            }
        }

        canvas.getBufferStrategy().show();
        graphics.dispose();
    }
}
