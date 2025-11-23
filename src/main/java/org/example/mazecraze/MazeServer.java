package org.example.mazecraze;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static org.example.mazecraze.MazeConstants.*;

public class MazeServer {
    ArrayList<PrintWriter> clientOutputStreams;

    ArrayList<ClientHandler> clients = new ArrayList<>();

    // Player positions and token are assigned on FCFS basis
    Map<Integer, List<Integer>> positions = Map.of(
            0,new ArrayList<>(Arrays.asList(0,2)),
            1,new ArrayList<>(Arrays.asList(4,2))
    );

    Map<Integer, Character> tokens = Map.of(
            0,'^',
            1,'*'
    );

    Maze maze;

    boolean hasWon = false;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        Player player;
        Maze maze;
        PrintWriter writer;

        public ClientHandler(Socket clientSocket, Maze maze, Player player)
        {
            try
            {
                // add this handler to the List
                clients.add(this);
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
                writer  = clientOutputStreams.get(clients.indexOf(this));;
                this.player = player;
                this.maze = maze;
                // set position
                this.player.setRow(positions.get(clients.indexOf(this)).get(0));
                this.player.setCol(positions.get(clients.indexOf(this)).get(1));
                // set token
                this.player.setToken(tokens.get(clients.indexOf(this)));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            writer.println(WELCOME_MESSAGE);
            writer.println(MOVEMENT_PROMPT + player.getToken() + ".");
            writer.println(maze.show());
            writer.flush();
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    boolean moveMade = false;
                    char token = player.getToken();
                    if (message.equalsIgnoreCase(NORTH)) {
                        player.moveUp();
                        hasWon = player.isAtFinishLine();
                        moveMade = true;
                    } else if (message.equalsIgnoreCase(SOUTH)) {
                        player.moveDown();
                        hasWon = player.isAtFinishLine();
                        moveMade = true;
                    } else if (message.equalsIgnoreCase(WEST)) {
                        player.moveLeft();
                        hasWon = player.isAtFinishLine();
                        moveMade = true;
                    } else if (message.equalsIgnoreCase(EAST)) {
                        player.moveRight();
                        hasWon = player.isAtFinishLine();
                        moveMade = true;
                    } else if (message.equalsIgnoreCase(SEE_GOLD_COMMAND)) {
                        int maxGoldAmtCanCollect = maze.getMaximumGold(player.getRow(), player.getCol(), player.getToken());
                        writer.println(GOLD_MESSAGE + maxGoldAmtCanCollect + GOLD_SUFFIX);
                    }
                    else if(message.equalsIgnoreCase(DISTANCE_COMMAND))
                    {
                        int distance = maze.shortestPathBinaryMatrix(player.getRow(), player.getCol(), player.getToken());
                        writer.println(DISTANCE_MESSAGE + distance + DISTANCE_SUFFIX);
                    }
                    else {
                        writer.println(INVALID_COMMAND);
                    }

                    if (moveMade) {
                        writer.println(YOU_PREFIX);
                        tellEveryoneBut(token + ":", writer);
                        tellEveryone(maze.show());

                        if (hasWon) {
                            writer.println(WIN_MESSAGE);
                            writer.flush();
                            tellEveryoneBut(token + " HAS WON!", writer);
                            tellEveryone(CONTINUE_PROMPT);
                            maze.initialize();
                            resetEveryone();
                            hasWon = false;
                            for (ClientHandler client : clients) {
                                PrintWriter writer = client.writer;
                                writer.println(WELCOME_MESSAGE);
                                writer.println(MOVEMENT_PROMPT + token + ".");
                                writer.println(client.maze.show());
                            }
                        }
                    }
                    writer.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void tellEveryoneBut(String message, PrintWriter writer) {
        Iterator<PrintWriter> it = clientOutputStreams.iterator();
        while (it.hasNext())
        {
            try
            {
                PrintWriter aWriter = (PrintWriter) it.next();
                if(aWriter != writer) {
                    aWriter.println((message));
                    // clear the output stream of any characters that may be or maybe not inside the stream
                    aWriter.flush();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void resetEveryone()
    {
        for(int i = 0; i < clients.size(); i++)
        {
            ClientHandler client = clients.get(i);
            client.player.setRow(positions.get(i).get(0));
            client.player.setCol(positions.get(i).get(1));
            client.player.setToken(tokens.get(i));
        }
    }
    private void tellEveryone(String message) {
        Iterator<PrintWriter> it = clientOutputStreams.iterator();
        while (it.hasNext())
        {
            try
            {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println((message));
                // clear the output stream of any characters that may be or maybe not inside the stream
                writer.flush();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void suggestClosestPath()
    {

    }

    public static void main(String [] args)
    {
        MazeServer server = new MazeServer();
        server.go();
    }

    private void go() {
        clientOutputStreams = new ArrayList<>();

        // Instantiate a single shared org.example.mazecraze.Maze object in the server class.
        // This instance will be accessed and modified by all client threads.
        maze = Maze.getInstance();
        // initialize the maze
        maze.initialize();

        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            while(true)
            {
                // accept connection to the newly connected client
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                // save output stream for that client
                clientOutputStreams.add(writer);
                // start a new thread that will read the messages sent by thi client and then send them to all connected clients
                ClientHandler clientHandler = new ClientHandler(clientSocket, maze, new Player());

                Thread t = new Thread(clientHandler);
                t.start();
                System.out.println(SERVER_PING_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
