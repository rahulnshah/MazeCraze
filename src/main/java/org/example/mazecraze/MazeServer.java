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

        int [] position = new int[2];
        Maze maze;
        
        PrintWriter writer;

        char token;

        public ClientHandler(Socket clientSocket, Maze maze)
        {
            try
            {
                // add this handler to the List
                clients.add(this);
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
                writer  = clientOutputStreams.get(clients.indexOf(this));;
                this.maze = maze;
                // set position
                this.position[0] = positions.get(clients.indexOf(this)).get(0);
                this.position[1] = positions.get(clients.indexOf(this)).get(1);
                // set token
                this.token = tokens.get(clients.indexOf(this));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            writer.println(WELCOME_MESSAGE);
            writer.println(MOVEMENT_PROMPT + token + ".");
            writer.println(maze.show());
            writer.flush();
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    boolean moveMade = false;
                    if (message.equalsIgnoreCase(NORTH)) {
                        maze.moveUp(position, token);
                        hasWon = maze.isAtFinishLine(position, token);
                        moveMade = true;
                    } else if (message.equalsIgnoreCase(SOUTH)) {
                        maze.moveDown(position, token);
                        hasWon = maze.isAtFinishLine(position, token);
                        moveMade = true;
                    } else if (message.equalsIgnoreCase(WEST)) {
                        maze.moveLeft(position, token);
                        hasWon = maze.isAtFinishLine(position, token);
                        moveMade = true;
                    } else if (message.equalsIgnoreCase(EAST)) {
                        maze.moveRight(position, token);
                        hasWon = maze.isAtFinishLine(position, token);
                        moveMade = true;
                    } else if (message.startsWith(SEE_GOLD_COMMAND)) {
                        int maxGoldAmtCanCollect = maze.callFindMaxGold(position);
                        writer.println(GOLD_MESSAGE + maxGoldAmtCanCollect + GOLD_SUFFIX);
                    }
                    else if(message.startsWith(DISTANCE_COMMAND))
                    {
                        int distance = maze.shortestPathBinaryMatrix(position, token);
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
                                writer.println(MOVEMENT_PROMPT + client.token + ".");
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
            client.position[0] = positions.get(i).get(0);
            client.position[1] = positions.get(i).get(1);
            client.token = tokens.get(i);
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
                ClientHandler clientHandler = new ClientHandler(clientSocket, maze);

                Thread t = new Thread(clientHandler);
                t.start();
                System.out.println(SERVER_PING_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
