package org.example.mazecraze;

import org.example.mazecraze.model.IO;
import org.example.mazecraze.model.Maze;

import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class MazeServer {
    public static void main(String [] args) throws Exception {
        MazeServer server = new MazeServer();
        server.go();
    }

    private void go() throws Exception {
        try (var listener = new ServerSocket(8080)) {
            IO.println("Maze Server is Running...");
            try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
                    // initialize a new maze for each pair of players
                    Maze maze = new Maze();
                    maze.initialize();
                    pool.execute(maze.new Player(listener.accept(), '^'));
                    pool.execute(maze.new Player(listener.accept(), '*'));
                }
            }
        }
    }
}
