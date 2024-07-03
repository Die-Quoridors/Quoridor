package me.diequoridors.renderer;

import me.diequoridors.world.World;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Renderer {

    private static final int gridSize = 9;

    private final World world;

    private final Frame frame = new Frame();
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

        canvas.setBounds(0, frame.getInsets().top, windowSize, windowSize);
        canvas.setSize(new Dimension(windowSize, windowSize));

        for (int i = 1; i < gridSize; i++) {
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.drawLine(i * cellSize, 0, i * cellSize, frame.getHeight());
            graphics.drawLine(0, i * cellSize, frame.getWidth(), i * cellSize);
        }

        canvas.getBufferStrategy().show();
        graphics.dispose();
    }
}
