package org.example.mazecraze.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maze {
    private char [][] grid;
    private int n, m;
    private Map<List<Integer>, Integer> goldAmounts;

    public synchronized void initialize()
    {
        // make a new grid
        grid = new char[][]{
            {'1', '0', '^', '0', '0'},
            {'0', '1', '0', '0', '0'},
            {'0', '0', '0', '1', '0'},
            {'1', '1', '0', '1', '1'},
            {'0', '0', '*', '0', '0'}
        };


        goldAmounts = new HashMap<>();
        goldAmounts.put(Arrays.asList(0,0), 6);
        goldAmounts.put(Arrays.asList(0,1), 2);
        goldAmounts.put(Arrays.asList(0,2), 4);
        goldAmounts.put(Arrays.asList(0,3), 0);
        goldAmounts.put(Arrays.asList(0,4), 0);
        goldAmounts.put(Arrays.asList(1,0), 0);
        goldAmounts.put(Arrays.asList(1,2), 3);
        goldAmounts.put(Arrays.asList(1,3), 0);
        goldAmounts.put(Arrays.asList(1,4), 9);
        goldAmounts.put(Arrays.asList(2,0), 10);
        goldAmounts.put(Arrays.asList(2,1), 0);
        goldAmounts.put(Arrays.asList(2,2), 0);
        goldAmounts.put(Arrays.asList(2,4), 1);
        goldAmounts.put(Arrays.asList(3,2), 0);
        goldAmounts.put(Arrays.asList(4,0), 7);
        goldAmounts.put(Arrays.asList(4,1), 0);
        goldAmounts.put(Arrays.asList(4,2), 4);
        goldAmounts.put(Arrays.asList(4,3), 4);
        goldAmounts.put(Arrays.asList(4,4), 8);

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

    public synchronized int shortestPathBinaryMatrix(int startRow, int startColumn, char token)
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

    private int findMaxGold(int r, int c, int n, int m, boolean [][] vis, char opponentToken)
    {
        /*
        Edge cases to handle:
        1) out of bounds - return 0 gold
        2) vis at pos(r,c) == true, return 0
        3) wall at (r,c) return 0
        4) opponent token at (r,c) return 0
        5) no gold at (r,c) return 0
        */
        if(r < 0 || c < 0 || r >= n || c >= m || grid[r][c] == '1' || grid[r][c] == opponentToken || vis[r][c] || !goldAmounts.containsKey(Arrays.asList(r,c))){
            return 0;
        }
        int goldAmt = goldAmounts.get(Arrays.asList(r,c));
        vis[r][c] = true;
        int left = findMaxGold(r, c - 1, n, m, vis, opponentToken);
        int right = findMaxGold(r, c + 1, n, m, vis, opponentToken);
        int up = findMaxGold(r - 1, c, n, m, vis, opponentToken);
        int down = findMaxGold(r + 1, c, n, m, vis, opponentToken);
        vis[r][c] = false;
        // take the max
        return goldAmt + Math.max(left, Math.max(right, Math.max(up, down)));
    }

    public synchronized int getMaximumGold(int currRow, int currCol, char token)
    {
        char opponentToken = (token == '^') ? '*' : '^';
        boolean [][] vis = new boolean[n][m];
        return findMaxGold(currRow, currCol, n, m, vis, opponentToken);
    }
    public synchronized String show()
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