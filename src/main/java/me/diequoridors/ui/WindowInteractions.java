package me.diequoridors.ui;

import me.diequoridors.Game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowInteractions extends WindowAdapter {

    private final Renderer renderer;
    private final Game game;

    public WindowInteractions(Renderer renderer, Game game) {
        this.renderer = renderer;
        this.game = game;
        renderer.frame.addWindowListener(this);
    }

    public void removeListener() {
        renderer.frame.removeWindowListener(this);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        game.exit();
    }
}
