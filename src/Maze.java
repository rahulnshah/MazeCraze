
public class Maze {
    private char [][] grid = {
            {'0', '0', '^', '0', '0'},
            {'0', '0', '1', '1', '0'},
            {'0', '1', '0', '1', '0'},
            {'1', '0', '1', '0', '1'},
            {'0', '0', '*', '0', '0'}
    };

    private final int n = grid.length, m = grid[0].length;
    public synchronized boolean moveLeft(int [] position, char token)
    {
        int nrow = position[0];
        int ncol = position[1] - 1;
        // TODO: need to add another condition that there is not a wall at grid[r][c]
        if(ncol >= 0 && ncol < m && grid[nrow][ncol] == '0')
        {
            // swap
            swap(position[0], position[1], nrow, ncol);
            // Update col
            position[1] = ncol;
        }
        // check if any player hasWon
        if(token == '*')
        {
            return position[0] == 0;
        }
        else
        {
            return position[0] == m - 1;
        }
    }

    public synchronized boolean moveRight(int [] position, char token)
    {
        int nrow = position[0];
        int ncol = position[1] + 1;
        if(ncol >= 0 && ncol < m && grid[nrow][ncol] == '0')
        {
            // swap
            swap(position[0], position[1], nrow, ncol);
            position[1] = ncol;

        }

        if(token == '*')
        {
            return position[0] == 0;
        }
        else
        {
            return position[0] == m - 1;
        }
    }

    public synchronized boolean moveDown(int [] position, char token)
    {
        int nrow = position[0] + 1;
        int ncol = position[1];
        if(nrow >= 0 && nrow < n && grid[nrow][ncol] == '0')
        {
            // swap
            swap(position[0], position[1], nrow, ncol);
            // Update row
            position[0] = nrow;
        }
        // check if any player hasWon
        if(token == '*')
        {
            return position[0] == 0;
        }
        else
        {
            return position[0] == m - 1;
        }
    }

    public synchronized boolean moveUp(int [] position, char token)
    {
        int nrow = position[0] - 1;
        int ncol = position[1];
        if(nrow >= 0 && nrow < n && grid[nrow][ncol] == '0')
        {
            // swap
            swap(position[0], position[1], nrow, ncol);
            position[0] = nrow;
        }
        if(token == '*')
        {
            return position[0] == 0;
        }
        else
        {
            return position[0] == m - 1;
        }
    }

    public void swap(int row1, int column1, int row2, int column2)
    {
        char hold = grid[row1][column1];
        grid[row1][column1] = grid[row2][column2];
        grid[row2][column2] = hold;
    }
    public String show()
    {
        StringBuilder res = new StringBuilder();
        for (char [] chars : grid) {
            StringBuilder empty = new StringBuilder();
            for (char c : chars) {
                empty.append(c).append(" ");
            }
            res.append(empty).append("\n");
        }
        return res.toString();
    }
}