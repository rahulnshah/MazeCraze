package org.example.mazecraze;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MazeCrazeTest {
    private Maze maze;
    private Player player;

    @BeforeEach
    void setup() {
        // Any global setup can be done here
        // initialize maze and player instances
        maze = new Maze();
        maze.initialize();
        player = new Player(maze);
        player.setRow(4);
        player.setCol(2);
        player.setToken('*');
    }


    @Test
    void testPlayerConfiguration() {
        assertThat(player, allOf(hasProperty("row", is(4)),
                                 hasProperty("col", is(2)),
                                 hasProperty("token", is('*'))
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