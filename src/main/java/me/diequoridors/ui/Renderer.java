package me.diequoridors.ui;

import me.diequoridors.world.Player;
import me.diequoridors.world.World;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Renderer {

    private static final int gridSize = 9;
    private static final float cellPadding = 0.1f;

    private final World world;

    public final Frame frame = new Frame();
    private final Canvas canvas = new Canvas();

    private final Timer frameTimer = new Timer();

    public Renderer(World world) {
        this.world = world;

        frame.setVisible(true);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.setTitle("Quoridor");
        frame.setSize(new Dimension(720, 720 + frame.getInsets().top));

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

    private void renderFrame() {
        Graphics graphics = canvas.getBufferStrategy().getDrawGraphics();
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int windowSize = Math.min(frame.getWidth(), frame.getHeight() - frame.getInsets().top);
        int cellSize = windowSize / gridSize;
        int usabelCellSize = (int) (cellSize * (1 - 2 * cellPadding));
        int cellOffset = (int) (cellSize * cellPadding);

        canvas.setBounds(0, frame.getInsets().top, windowSize, windowSize);
        canvas.setSize(new Dimension(windowSize, windowSize));

        // === render Grid ===
        for (int i = 1; i < gridSize; i++) {
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.drawLine(i * cellSize, 0, i * cellSize, frame.getHeight());
            graphics.drawLine(0, i * cellSize, frame.getWidth(), i * cellSize);
        }

        // === render Players ===
        for (Player player : world.players) {
            int x = player.x * cellSize + cellOffset;
            int y = player.y * cellSize + cellOffset;

            graphics.setColor(player.color);
            graphics.fillOval(x, y, usabelCellSize, usabelCellSize);
            graphics.setColor(Color.BLACK);
            graphics.drawOval(x, y, usabelCellSize, usabelCellSize);
        }

        canvas.getBufferStrategy().show();
        graphics.dispose();
    }
}
