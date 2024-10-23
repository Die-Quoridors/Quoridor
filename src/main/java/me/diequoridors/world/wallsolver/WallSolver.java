package me.diequoridors.world.wallsolver;

import me.diequoridors.Game;
import me.diequoridors.world.Player;
import me.diequoridors.world.Wall;
import me.diequoridors.world.WallRotation;
import me.diequoridors.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WallSolver {

    private final World world;

    public WallSolver(World world) {
        this.world = world;
    }

    public boolean isWallValid(Player player, int wx, int wy, WallRotation wrotation) {
        Game fakeGame = new Game(world);
        Player dummyPlayer = new Player(player.x, player.y , player.playerId, fakeGame);
        fakeGame.world.walls.add(new Wall(wx, wy, wrotation, dummyPlayer));

        ArrayList<Vec2D> visited = new ArrayList<>();
        ArrayList<Vec2D> underConsideration = new ArrayList<>();

        for (boolean fst = true; !underConsideration.isEmpty() || fst; fst = false) {
            if (dummyPlayer.isInWinArea()) {
                return true;
            }
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    int ax = dummyPlayer.x + x;
                    int ay = dummyPlayer.y + y;
                    if (
                            underConsideration.stream().anyMatch(p -> p.x == ax && p.y == ay)
                            || visited.stream().anyMatch(p -> p.x == ax && p.y == ay)
                    ) {
                        continue;
                    }
                    if (dummyPlayer.isValidMove(ax, ay)) {
                        underConsideration.add(new Vec2D(ax, ay));
                    }
                }
            }

            final Vec2D finalCurrentPos = new Vec2D(dummyPlayer.x, dummyPlayer.y);
            List<Vec2D> nearPlayerJumps = world.players.stream().filter(pl -> {
                int diffx = Math.abs(finalCurrentPos.x - pl.x);
                int diffy = Math.abs(finalCurrentPos.y - pl.y);
                return (diffx == 1 && diffy == 0) || (diffy == 1 && diffx == 0);
            }).map(pl -> {
                int diffx = pl.x - finalCurrentPos.x;
                int diffy = pl.y - finalCurrentPos.y;
                if (player.isValidMove(finalCurrentPos.x + diffx * 2, finalCurrentPos.y + diffy * 2)) {
                    return new Vec2D(finalCurrentPos.x + diffx * 2, finalCurrentPos.y + diffy * 2);
                }
                int tryx = diffx == 0 ? 1 : 0;
                int tryy = diffy == 0 ? 1 : 0;
                if (player.isValidMove(finalCurrentPos.x + diffx * 2 + tryx, finalCurrentPos.y + diffy * 2 + tryy)) {
                    return new Vec2D(finalCurrentPos.x + diffx * 2 + tryx, finalCurrentPos.y + diffy * 2 + tryy);
                }
                if (player.isValidMove(finalCurrentPos.x + diffx * 2 + tryx * -1, finalCurrentPos.y + diffy * 2 + tryy * -1)) {
                    return new Vec2D(finalCurrentPos.x + diffx * 2 + tryx * -1, finalCurrentPos.y + diffy * 2 + tryy * -1);
                }
                return null;
            }).filter(Objects::nonNull).filter(p ->
                    underConsideration.stream().noneMatch(c -> c.x == p.x && c.y == p.y)
                    && visited.stream().noneMatch(c -> c.x == p.x && c.y == p.y)
            ).toList();
            underConsideration.addAll(nearPlayerJumps);
            visited.add(finalCurrentPos);

            Vec2D nextPos = underConsideration.removeFirst();
            dummyPlayer.x = nextPos.x;
            dummyPlayer.y = nextPos.y;
        }
        return false;
    }

}

class Vec2D {

    int x;
    int y;

    public Vec2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D(Vec2D vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

}