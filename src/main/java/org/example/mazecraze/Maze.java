package org.example.mazecraze;

import java.util.LinkedList;
import java.util.Queue;

public class Maze {
    private static Maze uniqueInstance = new Maze();
    private char [][] grid;
    private int [][] gridOfGold;
    private int n, m;

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

    public synchronized boolean hasTile(int newRow, int newCol) {
        return grid[newRow][newCol] == '0';
    }

    public synchronized int getColumns() {
        return m;
    }

    public synchronized int getRows(){
        return n;
    }

    public synchronized void swap(int row1, int column1, int row2, int column2)
    {
        char hold = grid[row1][column1];
        grid[row1][column1] = grid[row2][column2];
        grid[row2][column2] = hold;
    }

    public int shortestPathBinaryMatrix(int startRow, int startColumn, char token)
    {
        int targetRow = (token == '^') ? n - 1 : 0;
        char opponentToken = (token == '^') ? '*' : '^';
        int n = grid.length, m = grid[0].length;
        boolean [][] vis = new boolean[n][m];
        Queue<int []> queue = new LinkedList<>();
        // Start from the initial point
        queue.add(new int [] {startRow,startColumn,1});
        vis[startRow][startColumn] = true;

        // to represent the 4 directions I could traverse in a 2D matrix, like a grid
        int [] deltaR = {0, 1, 0, -1};
        int [] deltaC = {-1, 0, 1, 0};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currRow = current[0];
            int currCol = current[1];
            int w = current[2];
            // Stop if we reach the end point
            if (currRow == targetRow) {
                return w;
            }

            // Check each possible move
            for (int i = 0; i < deltaR.length; i++) {
                int newRow = currRow + deltaR[i];
                int newCol = currCol + deltaC[i];

                // Check if new cell is valid
                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < m && grid[newRow][newCol] != '1' && grid[newRow][newCol] != opponentToken && !vis[newRow][newCol]) {
                    vis[newRow][newCol] = true;
                    queue.add(new int[]{newRow, newCol, w + 1});
                }
            }
        }
        // If end point is unreachable, return -1
        return -1;
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

    public int callFindMaxGold(int currRow, int currCol)
    {
        return findMaxGold(currRow, currCol, n, m);
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