package me.diequoridors.world;

public class Wall {
    
    public int x;
    public int y;
    public WallRotation rotation;
    public Player placer;

    public Wall(int x, int y, WallRotation rotation, Player placer) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.placer = placer;
    }

    public static boolean placementValid(World world, int x, int y, WallRotation rotation) {
        boolean simpleMatch = world.walls.stream().noneMatch(wall -> {
            if (wall.rotation == rotation) {
                int startX = wall.rotation == WallRotation.Horizontal ? wall.x - 1 : wall.x;
                int endX = wall.rotation == WallRotation.Horizontal ? wall.x + 1 : wall.x;
                int startY = wall.rotation == WallRotation.Vertical ? wall.y - 1 : wall.y;
                int endY = wall.rotation == WallRotation.Vertical ? wall.y + 1 : wall.y;

                return x >= startX && x <= endX && y >= startY && y <= endY;
            } else {
                return wall.x == x && wall.y == y;
            }
        });
        if (!simpleMatch) {
            return false;
        }
        System.out.println("Vaild: " + world.wallSolver.solveWalls(world.players.get(0)));
        return true;
    }
    

}
