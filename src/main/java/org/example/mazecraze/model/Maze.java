package org.example.mazecraze.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static org.example.mazecraze.constants.MazeConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maze {
    public char [][] grid;
    private int n, m;
    private Map<List<Integer>, Integer> goldAmounts;

    // Whose turn it is now
    Player currentPlayer;

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

    public synchronized int shortestPathBinaryMatrix(Player player)
    {
        int targetRow = player.getTargetRow();
        char opponentToken = player.getOpponent().getMark();
        int n = grid.length, m = grid[0].length, startRow = player.getRow(), startColumn = player.getCol();
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

    public synchronized int getMaximumGold(Player player)
    {
        char opponentToken = player.getOpponent().getMark();
        boolean [][] vis = new boolean[n][m];
        int currRow = player.getRow(), currCol = player.getCol();
        return findMaxGold(currRow, currCol, n, m, vis, opponentToken);
    }
    public synchronized String showGrid()
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
                res.append(empty).append(";");
            }
            else
            {
                res.append(empty);
            }
        }
        return res.toString();
    }

    public synchronized void isMovePossible(Player player) {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.opponent == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        }
    }

    public synchronized void move (String command, Player player) {
        // do the move here
        switch (command.toLowerCase()) {
            case NORTH -> player.moveUp();
            case SOUTH -> player.moveDown();
            case WEST -> player.moveLeft();
            case EAST -> player.moveRight();
            case SEE_GOLD_COMMAND -> {
                int maxGold = getMaximumGold(player);
                player.output.println("MAX_GOLD " + maxGold);
            }
            case DISTANCE_COMMAND -> {
                int shortestPath = shortestPathBinaryMatrix(player);
                player.output.println("SHORTEST_PATH " + shortestPath);
            }
        }
        currentPlayer = currentPlayer.opponent;
    }
    @Data
    @ToString(exclude = {"socket", "input", "output", "opponent"})
    public class Player implements Runnable {
        int row;
        int col;
        char mark;
        int targetRow;
        Player opponent;
        final Socket socket;
        Scanner input;
        PrintWriter output;

        public Player(Socket socket, char mark) throws IOException {
            this.socket = socket;
            this.mark = mark;
            this.targetRow = (mark == '^') ? getRows() - 1 : 0;
            this.row = (mark == '^') ? 0 : getRows() - 1;
            this.col = getColumns() / 2;
            this.input = new Scanner(socket.getInputStream());
            this.output = new PrintWriter(socket.getOutputStream(), true);
        }

        public void moveLeft()
        {
            int newRow = row;
            int newCol = col - 1;
            if(canMoveHorizontally(newCol) && hasTile(newRow, newCol))
            {
                // swap
                swap(row, col, newRow, newCol);
                // Update col
                this.setCol(newCol);
            }
        }

        public void moveRight()
        {
            int newRow = row;
            int newCol = col + 1;
            if(canMoveHorizontally(newCol) && hasTile(newRow, newCol))
            {
                // swap
                swap(row, col, newRow, newCol);
                this.setCol(newCol);
            }
        }

        public void moveDown()
        {
            int newRow = row + 1;
            int newCol = col;
            if(canMoveVertically(newRow) && hasTile(newRow, newCol))
            {
                // swap
                swap(row, col, newRow, newCol);
                // Update row
                this.setRow(newRow);
            }
        }

        public void moveUp()
        {
            int newRow = row - 1;
            int newCol = col;
            if(canMoveVertically(newRow) && hasTile(newRow, newCol))
            {
                // swap
                swap(row, col, newRow, newCol);
                this.setRow(newRow);
            }
        }

        public boolean isAtFinishLine() {
            // check if any player hasWon
            return row == targetRow;
        }

        public boolean canMoveHorizontally(int newCol) {
            return newCol >= 0 && newCol < getColumns();
        }

        public boolean canMoveVertically(int newRow) {
            return newRow >= 0 && newRow < getRows();
        }

        private void setup() {
            IO.println("Player " + this + " connected");
            IO.println("Sending welcome message to " + this);
            output.println("WELCOME " + mark);
            if (mark == '^') {
                currentPlayer = this;
                output.println("MESSAGE Waiting for opponent to connect");
            } else {
                opponent = currentPlayer;
                targetRow = getRows() - 1;
                opponent.opponent = this;
                opponent.output.println("MESSAGE Your move");
            }
        }

        private void processMoveCommand(String command) {
            try {
                isMovePossible(this);
                output.println("VALID_MOVE");
                move(command, this);
                opponent.output.println("OPPONENT_MOVED " + "(" + row + "," + col + ")");
                output.println("GRID " + showGrid());
                opponent.output.println("GRID " + showGrid());
            } catch (IllegalStateException e) {
                IO.println("Rejected move from " + this + ": " + e.getMessage());
                output.println("MESSAGE " + e.getMessage());
            }
        }

        private void processCommands() {
            while (input.hasNextLine()) {
                var command = input.nextLine();
                IO.println("Received command from " + this + ": " + command);
                if (command.startsWith("QUIT") || isAtFinishLine()) {
                    if(isAtFinishLine())
                    {
                        output.println("VICTORY");
                        opponent.output.println("DEFEAT");
                    }
                    // No more to read from this player
                    return;
                } else if (command.equalsIgnoreCase(NORTH) || command.equalsIgnoreCase(SOUTH) || command.equalsIgnoreCase(WEST) || command.equalsIgnoreCase(EAST) || command.equalsIgnoreCase(SEE_GOLD_COMMAND) || command.equalsIgnoreCase(DISTANCE_COMMAND)) {
                    processMoveCommand(command);
                }
                else {
                    output.println(INVALID_COMMAND);
                }
            }
        }

        @Override
        public void run() {
            try (socket) {
                setup(); // first player that joins makes up - kickoff the game
                processCommands(); // process commands from the player - current player
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                IO.println("Player " + this + " disconnected");
            }
        }
    }

}