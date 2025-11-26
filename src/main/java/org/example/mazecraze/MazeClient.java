package org.example.mazecraze;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MazeClient {

    private final Socket socket;
    private final Scanner in;
    private final PrintWriter out;

    public MazeClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, 58901);
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
                    var loc = Integer.parseInt(response.substring(15));
                    System.out.println("Opponent moved to: " + loc);

                } else if (response.startsWith("MESSAGE")) {
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
                    System.out.print("Enter move (0-8): ");
                    int move = console.nextInt();
                    out.println("MOVE " + move);
                }
            }

            out.println("QUIT");
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
