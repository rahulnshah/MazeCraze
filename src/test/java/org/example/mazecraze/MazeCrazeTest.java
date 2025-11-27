package org.example.mazecraze;

import org.example.mazecraze.model.Maze;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class MazeCrazeTest {
    private Maze maze;
    private Maze.Player player;

    @BeforeEach
    void setup() throws IOException {
        // Any global setup can be done here
        // initialize maze and player instances
        maze = new Maze();
        maze.initialize();
        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(mock(java.io.InputStream.class));
        when(mockSocket.getOutputStream()).thenReturn(mock(java.io.OutputStream.class));
        player = maze.new Player(mockSocket, '^');
        player.setRow(4);
        player.setCol(2);
    }


    @Test
    void testPlayerConfiguration() {
        assertThat(player, allOf(hasProperty("row", is(4)),
                                 hasProperty("col", is(2)),
                                 hasProperty("mark", is('^'))
        ));
    }

    @Test
    void testMazeConfiguration() {
        assertThat(maze, allOf(hasProperty("n", is(5)),
                                 hasProperty("m", is(5))
        ));
    }

    @Test
    void testPlayerMoveUp() {
        player.moveUp();
        assertThat(player, allOf(hasProperty("row", is(3)),
                                 hasProperty("col", is(2))
        ));
    }

    @Test
    void testPlayerMoveDown() {
        player.moveDown();
        assertThat(player, allOf(hasProperty("row", is(4)),
                                 hasProperty("col", is(2))
        ));
    }

    @Test
    void testPlayerMoveLeft() {
        player.moveLeft();
        assertThat(player, allOf(hasProperty("row", is(4)),
                                 hasProperty("col", is(1))
        ));
    }

    @Test
    void testPlayerMoveRight() {
        assertThat(player, allOf(hasProperty("row", is(4)),
                                 hasProperty("col", is(2))
        ));
    }

    @Test
    void testPlayerInvalidMove() {
        player.setRow(3);
        player.setCol(2);
        player.moveRight();
        assertThat(player, allOf(hasProperty("row", is(3)),
                                 hasProperty("col", is(2))
        ));
    }

    @AfterEach
    void teardown() {
        // Any global teardown can be done here
        maze.setGrid(null);
        maze.setGoldAmounts(null);
        maze = null;
        player = null;
    }
}