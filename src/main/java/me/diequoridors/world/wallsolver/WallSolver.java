package me.diequoridors.world.wallsolver;

import me.diequoridors.world.WallRotation;
import me.diequoridors.world.World;

import java.util.*;

public class WallSolver {

    private final World world;

    public WallSolver(World world) {
        this.world = world;
    }

    public void solveWalls() {
        List<Vec2D[]> wallIndex = generateWallIndex();
        List<Integer> stringEnds = findEnds(wallIndex);

        ArrayList<ArrayList<Integer>> strings = new ArrayList<>();
        for (int wallI : stringEnds) {
            if (strings.stream().anyMatch(s -> s.getFirst() == wallI || s.getLast() == wallI)) {
                continue;
            }

            List<ArrayList<Integer>> tmpStrings = growStrings(wallI, wallIndex);
            strings.addAll(tmpStrings);
        }
        System.out.println(strings);
    }

    private List<Vec2D[]> generateWallIndex() {
        return world.walls.stream().map(wall -> {
            int endAX = wall.rotation == WallRotation.Vertical ? wall.x : wall.x - 1;
            int endAY = wall.rotation == WallRotation.Vertical ? wall.y - 1 : wall.y;

            int endBX = wall.rotation == WallRotation.Vertical ? wall.x : wall.x + 1;
            int endBY = wall.rotation == WallRotation.Vertical ? wall.y + 1 : wall.y;

            return new Vec2D[]{
                    new Vec2D(endAX, endAY),
                    new Vec2D(endBX, endBY)
            };
        }).toList();
    }

    private List<Integer> findEnds(List<Vec2D[]> wallIndex) {
        return wallIndex.stream()
                .filter(wall ->
                        !Arrays.stream(wall).allMatch(end -> endBlocked(end, wall, wallIndex))
                )
                .map(wallIndex::indexOf)
                .toList();
    }

    // another wall shares end
    private static boolean endBlocked(Vec2D end, Vec2D[] wall, List<Vec2D[]> wallIndex) {
        return wallIndex.stream()
                .filter(w -> w != wall)
                .anyMatch(w ->
                        Arrays.stream(w).anyMatch(e -> e.x == end.x && e.y == end.y)
                );
    }

    private List<ArrayList<Integer>> growStrings(int wallI, List<Vec2D[]> wallIndex) {
        return growStrings(wallI, wallIndex, -1);
    }

    private List<ArrayList<Integer>> growStrings(int wallI, List<Vec2D[]> wallIndex, int fromEnd) {
        Vec2D[] wall = wallIndex.get(wallI);
        Vec2D end;
        if (fromEnd != -1) {
            Vec2D e = wall[fromEnd == 0 ? 1 : 0];
            if (!endBlocked(e, wall, wallIndex)) {
                return List.of(new ArrayList<>(List.of(wallI)));
            }
            end = e;
        } else {
            Optional<Vec2D> en = Arrays.stream(wall).filter(e -> endBlocked(e, wall, wallIndex)).findFirst();
            if (en.isEmpty()) {
                return List.of(new ArrayList<>(List.of(wallI)));
            }
            end = en.get();
        }

        List<Vec2D[]> tmp = wallIndex.stream()
                .filter(w -> w != wall)
                .filter(w -> Arrays.stream(w).anyMatch(e -> e.x == end.x && e.y == end.y))
                .toList();

        return wallIndex.stream()
                .filter(w -> w != wall)
                .filter(w -> Arrays.stream(w).anyMatch(e -> e.x == end.x && e.y == end.y))
                .map(w -> {
                    int i = wallIndex.indexOf(w);

                    // get end connected to prev wall
                    Vec2D ef = Arrays.stream(w).filter(e -> e.x == end.x && e.y == end.y).findFirst().get();
                    int ei = Arrays.stream(w).toList().indexOf(ef);

                    return growStrings(i, wallIndex, ei).stream().peek(str -> str.add(wallI)).toList();
                })
                .flatMap(Collection::stream)
                .toList();
    }

}

class Vec2D {

    int x;
    int y;

    public Vec2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

}