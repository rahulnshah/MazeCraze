package org.example.mazecraze;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MazeClient {

    private final Socket socket;
    private final Scanner in;
    private final PrintWriter out;

    public MazeClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, 8080);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void play() throws Exception {
        try (socket) {
            var response = in.nextLine();
            char mark = response.charAt(8);

            System.out.println("You are player " + mark);

            Scanner console = new Scanner(System.in);

            while (in.hasNextLine()) {
                response = in.nextLine();

                if (response.startsWith("VALID_MOVE")) {
                    System.out.println("Move accepted. Waiting...");

                } else if (response.startsWith("OPPONENT_MOVED")) {
                    var loc = response.substring(15);
                    System.out.println("Opponent moved to: " + loc);

                }
                else if(response.startsWith("GRID")){
                    System.out.println("Current Maze State:");
                    String [] maze = response.substring(5).split(";");
                    for(String row : maze){
                        System.out.println(row);
                    }
                }
                else if (response.startsWith("INVALID_MOVE")) {
                    System.out.println("Invalid move. Try again.");

                }
                else if(response.startsWith("MAX_GOLD")){
                    System.out.println("You can collect a maximum of " + response.substring(9) + " gold in this maze.");
                }
                else if(response.startsWith("SHORTEST_PATH")){
                    System.out.println("Shortest path to target row is: " + response.substring(14) + " steps.");
                }
                else if (response.startsWith("MESSAGE")) {
                    System.out.println(response.substring(8));

                } else if (response.startsWith("VICTORY")) {
                    System.out.println("You win!");
                    break;

                } else if (response.startsWith("DEFEAT")) {
                    System.out.println("You lost.");
                    break;

                } else if (response.startsWith("TIE")) {
                    System.out.println("It's a tie.");
                    break;

                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    System.out.println("Opponent left.");
                    break;
                }

                // If it's your turn, ask for a move
                if (response.startsWith("MESSAGE Your move")) {
                    System.out.print("Enter move (N,S,W,E,sg,sd): ");
                    String move = console.next();
                    out.println(move);
                }
            }

            out.println("QUIT");
            out.flush();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java MazeClient <server-ip>");
            return;
        }

        MazeClient client = new MazeClient(args[0]);
        client.play();
    }
}
