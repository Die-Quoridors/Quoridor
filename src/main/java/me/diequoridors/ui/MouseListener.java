package me.diequoridors.ui;

import me.diequoridors.Game;
import me.diequoridors.world.Player;
import me.diequoridors.world.WallRotation;
import me.diequoridors.world.World;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class MouseListener extends MouseAdapter {

    private final Game game;

    private Player movingPlayer;
    private Player wallPlayer;

    public Player phantomPlayer;
    public PhantomWall phantomWall;

    public MouseListener(Game game) {
        this.game = game;
        game.renderer.canvas.addMouseListener(this);
        game.renderer.canvas.addMouseMotionListener(this);
    }

    public void removeListener() {
        game.renderer.canvas.removeMouseListener(this);
        game.renderer.canvas.removeMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Optional<Player> clickedPlayer = game.world.players.stream().filter(player ->
                e.getX() > game.renderer.coordinatesToScreen(player.x)
                && e.getX() < game.renderer.coordinatesToScreen(player.x + 1)
                && e.getY() > game.renderer.coordinatesToScreen(player.y)
                && e.getY() < game.renderer.coordinatesToScreen(player.y + 1)
        ).findFirst();
        clickedPlayer.ifPresent(player -> {
            if (game.playerStrictMode && player != game.world.ownPlayer) {
                return;
            }
            movingPlayer = player;
            phantomPlayer = new Player(player);
        });

        wallPlayer = game.renderer.screenToWallPlayer(e.getX(), e.getY());
        if (game.playerStrictMode && wallPlayer != game.world.ownPlayer) {
            wallPlayer = null;
        }
        if (wallPlayer != null) {
            int x = game.renderer.screenToCoordinates(e.getX(), true);
            int y = game.renderer.screenToCoordinates(e.getY(), true);
            phantomWall = new PhantomWall(x, y, WallRotation.Vertical, wallPlayer);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (movingPlayer != null) {
            int x = game.renderer.screenToCoordinates(e.getX(), false);
            int y = game.renderer.screenToCoordinates(e.getY(), false);
            phantomPlayer.x = movingPlayer.x;
            phantomPlayer.y = movingPlayer.y;
            phantomPlayer.move(x, y);
        } else if (wallPlayer != null) {
            int x = game.renderer.screenToCoordinates(e.getX(), true);
            int y = game.renderer.screenToCoordinates(e.getY(), true);
            if (x >= 0 && y >= 0 && x < (World.worldSize - 1) && y < (World.worldSize - 1)) {
                phantomWall.x = x;
                phantomWall.y = y;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (movingPlayer != null) {
            int x = game.renderer.screenToCoordinates(e.getX(), false);
            int y = game.renderer.screenToCoordinates(e.getY(), false);
            movingPlayer.move(x, y);
            movingPlayer = null;
            phantomPlayer = null;
        } else if (wallPlayer != null) {
            int mouseX = game.renderer.screenToCoordinates(e.getX(), true);
            int mouseY = game.renderer.screenToCoordinates(e.getY(), true);
            boolean inField = mouseX >= 0 && mouseY >= 0 && mouseX < (World.worldSize - 1) && mouseY < (World.worldSize - 1);
            if (!inField) {
                phantomWall = null;
                return;
            }
            wallPlayer.placeWall(mouseX, mouseY, phantomWall.rotation);
            phantomWall = null;
        }
    }
}
