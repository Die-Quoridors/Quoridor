package me.diequoridors.ui;

import me.diequoridors.Game;
import me.diequoridors.world.Wall;
import me.diequoridors.world.WallRotation;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyListener extends KeyAdapter {

    private Game game;

    public KeyListener(Game game) {
        this.game = game;
        game.renderer.frame.addKeyListener(this);
    }

    public void removeListener() {
        game.renderer.frame.removeKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getExtendedKeyCode()) {
            case 82: { // r
                Wall phantomWall = game.mouseListener.phantomWall;
                if (phantomWall != null) {
                    phantomWall.rotation = phantomWall.rotation == WallRotation.Vertical ? WallRotation.Horizontal : WallRotation.Vertical;
                }
            } break;
            default: {
                System.out.println("Key not found: " + e.getExtendedKeyCode());
            } break;
        }
    }
}
