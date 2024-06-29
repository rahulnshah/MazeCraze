
public class Maze {
    private char [][] grid = {
            {'0', '0', '^', '0', '0'},
            {'0', '0', '1', '1', '0'},
            {'0', '1', '0', '1', '0'},
            {'1', '0', '1', '0', '1'},
            {'0', '0', '*', '0', '0'}
    };

    private int n = grid.length, m = grid[0].length;
    public synchronized void moveLeft(int [] position, char token)
    {
        int nrow = position[0];
        int ncol = position[1] - 1;
        // TODO: need to add another condition that there is not a wall at grid[r][c]
        if(ncol >= 0 && ncol < m && grid[nrow][ncol] != token && grid[nrow][ncol] != '1')
        {
           // Update col
            position[1] = ncol;
            // mark the row and col as visited in grid
            grid[nrow][ncol] = token;
        }
    }

    public synchronized void moveRight(int [] position, char token)
    {
        int nrow = position[0];
        int ncol = position[1] + 1;
        if(ncol >= 0 && ncol < m && grid[nrow][ncol] != token && grid[nrow][ncol] != '1')
        {
            position[1] = ncol;
            // mark the row and col as visited in grid
            grid[nrow][ncol] = token;
        }
    }

    public synchronized void moveDown(int [] position, char token)
    {
        int nrow = position[0] + 1;
        int ncol = position[1];
        if(nrow >= 0 && nrow < n && grid[nrow][ncol] != token && grid[nrow][ncol] != '1')
        {
            // Update row
            position[0] = nrow;
            // mark the row and col as visited in grid
            grid[nrow][ncol] = token;
        }
    }

    public synchronized void moveUp(int [] position, char token)
    {
        int nrow = position[0] - 1;
        int ncol = position[1];
        if(nrow >= 0 && nrow < n && grid[nrow][ncol] != token && grid[nrow][ncol] != '1')
        {
            position[0] = nrow;
            // mark the row and col as visited in grid
            grid[nrow][ncol] = token;
        }
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