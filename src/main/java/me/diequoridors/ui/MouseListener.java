package me.diequoridors.ui;

import me.diequoridors.Game;
import me.diequoridors.world.Player;
import me.diequoridors.world.WallRotation;
import me.diequoridors.world.World;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class MouseListener extends MouseAdapter {

    private final Renderer renderer;
    private final World world;

    private Player movingPlayer;
    private Player wallPlayer;

    public Player phantomPlayer;

    public MouseListener(Game game) {
        this.renderer = game.renderer;
        this.world = game.world;
        renderer.canvas.addMouseListener(this);
        renderer.canvas.addMouseMotionListener(this);
    }

    public void removeListener() {
        renderer.canvas.removeMouseListener(this);
        renderer.canvas.removeMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Optional<Player> clickedPlayer = world.players.stream().filter(player ->
                e.getX() > renderer.coordinatesToScreen(player.x)
                && e.getX() < renderer.coordinatesToScreen(player.x + 1)
                && e.getY() > renderer.coordinatesToScreen(player.y)
                && e.getY() < renderer.coordinatesToScreen(player.y + 1)
        ).findFirst();
        clickedPlayer.ifPresent(player -> {
            movingPlayer = player;
            phantomPlayer = new Player(player);
        });

        wallPlayer = renderer.screenToWallPlayer(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (movingPlayer != null) {
            int x = renderer.screenToCoordinates(e.getX(), false);
            int y = renderer.screenToCoordinates(e.getY(), false);
            phantomPlayer.x = movingPlayer.x;;
            phantomPlayer.y = movingPlayer.y;
            phantomPlayer.move(x, y);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (movingPlayer != null) {
            int x = renderer.screenToCoordinates(e.getX(), false);
            int y = renderer.screenToCoordinates(e.getY(), false);
            movingPlayer.move(x, y);
            movingPlayer = null;
            phantomPlayer = null;
        } else if (wallPlayer != null) {
            int x = renderer.screenToCoordinates(e.getX(), true);
            int y = renderer.screenToCoordinates(e.getY(), true);
            wallPlayer.placeWall(x, y, WallRotation.Vertical);
        }
    }
}
