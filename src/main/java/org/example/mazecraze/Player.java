package org.example.mazecraze;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private int row;
    private int col;
    private char token;
    private Maze maze;

    public Player(Maze maze)
    {
        this.maze = maze;
    }

    public void moveLeft()
    {
        int newRow = row;
        int newCol = col - 1;
        if(canMoveHorizontally(newCol) && maze.hasTile(newRow, newCol))
        {
            // swap
            maze.swap(row, col, newRow, newCol);
            // Update col
            col = newCol;
        }
    }

    public void moveRight()
    {
        int newRow = row;
        int newCol = col + 1;
        if(canMoveHorizontally(newCol) && maze.hasTile(newRow, newCol))
        {
            // swap
            maze.swap(row, col, newRow, newCol);
            col = newCol;
        }
    }

    public void moveDown()
    {
        int newRow = row + 1;
        int newCol = col;
        if(canMoveVertically(newRow) && maze.hasTile(newRow, newCol))
        {
            // swap
            maze.swap(row, col, newRow, newCol);
            // Update row
            row = newRow;
        }
    }

    public void moveUp()
    {
        int newRow = row - 1;
        int newCol = col;
        if(canMoveVertically(newRow) && maze.hasTile(newRow, newCol))
        {
            // swap
            maze.swap(row, col, newRow, newCol);
            row = newRow;
        }
    }

    public boolean isAtFinishLine() {
        // check if any player hasWon
        if(token == '*')
        {
            return row == 0;
        }
        return row == maze.getColumns() - 1;
    }

    private boolean canMoveHorizontally(int newCol) {
        return newCol >= 0 && newCol < maze.getColumns();
    }

    private boolean canMoveVertically(int newRow) {
        return newRow >= 0 && newRow < maze.getRows();
    }

}
