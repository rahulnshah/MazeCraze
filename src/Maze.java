import java.util.LinkedList;
import java.util.Queue;

public class Maze {
    private static Maze uniqueInstance = new Maze();
    private char [][] grid;
    private int [][] gridOfGold;
    int n, m;

    private Maze(){}

    public static Maze getInstance(){
        return uniqueInstance;
    }
    public synchronized void initialize()
    {
        // make a new grid
        grid = new char[][]{
            {'0', '0', '^', '0', '0'},
            {'0', '1', '0', '0', '0'},
            {'0', '0', '0', '1', '0'},
            {'1', '1', '0', '1', '1'},
            {'0', '0', '*', '0', '0'}
        };

        gridOfGold = new int [][]{
                {'0', '0', '^', '0', '0'},
                {'0', '1', '0', '0', '0'},
                {'0', '0', '0', '1', '0'},
                {'1', '1', '0', '1', '1'},
                {'0', '0', '*', '0', '0'}
        };
        n = grid.length;
        m = grid[0].length;
    }
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

    public boolean canReachDestination(int[] position, char token)
    {
        int srow = position[0], scol = position[1];
        int targetRow = (token == '^') ? n - 1 : 0;
        int n = grid.length, m = grid[0].length;
        boolean [][] vis = new boolean[n][m];
        Queue<int []> q = new LinkedList<>();
        q.add(new int [] {srow, scol});

        // to represent the 4 directions I could traverse in a 2D matrix, like a grid
        int [] delRow = {0, 1, 0, -1};
        int [] delCol = {-1, 0, 1, 0};

        while(!q.isEmpty()) {
            // take out first cell
            int row = q.peek()[0];
            int col = q.peek()[1];
            q.poll();
            // if you found dest return true
            if (row == targetRow) {
                return true;
            }
            // explore it neighbors
            for (int i = 0; i < 4; i++) { // only four directions
                int r = row + delRow[i];
                int c = col + delCol[i];
                if (r >= 0 && r < n && c >= 0 && c < m && grid[r][c] == 0 && !vis[r][c]) {
                    q.add(new int [] {r, c});
                    vis[r][c] = true;
                }
            }
        }
        // dest not reached
        return false;
    }

    public int findMaxGold(int r, int c, int n, int m)
    {
        /*
        Edge cases to handle:
        1) out of bounds - return 0 gold
        2) vis at pos(r,c) == true, return 0
        3) pos(r,c) == 0, return 0
        */
        if(r < 0 || c < 0 || r >= n || c >= m || gridOfGold[r][c] == '0'){
            return 0;
        }
        int goldAmt = gridOfGold[r][c] - '0';
        gridOfGold[r][c] = '0';
        int left = findMaxGold(r, c - 1, n, m);
        int right = findMaxGold(r, c + 1, n, m);
        int up = findMaxGold(r - 1, c, n, m);
        int down = findMaxGold(r + 1, c, n, m);
        // mark current pos as vis
        gridOfGold[r][c] = String.valueOf(goldAmt).charAt(0);
        // take the max
        return (gridOfGold[r][c] - '0') + Math.max(left, Math.max(right, Math.max(up, down)));
    }

    public int callFindMaxGold(int [] position)
    {
        return findMaxGold(position[0], position[1], n, m);
    }
    public String show()
    {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < n; i++) {
            StringBuilder empty = new StringBuilder();
            for (int j = 0; j < m - 1; j++) {
                empty.append(grid[i][j]).append(" ");
            }
            empty.append(grid[i][n - 1]);
            // if you're in the last row i then do not append a newline to empty
            if(i != n - 1) {
                res.append(empty).append("\n");
            }
            else
            {
                res.append(empty);
            }
        }
        return res.toString();
    }
}