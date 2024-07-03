package me.diequoridors;

import me.diequoridors.renderer.Renderer;
import me.diequoridors.world.World;

public class Main {
    public static void main(String[] args) {
        World world = new World();
        Renderer renderer = new Renderer(world);

    }
}