import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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
            writer.println("WELCOME TO MazeCraze!");
            writer.println(maze.show());
            writer.println("PRESS N, S, W, E TO NAVIGATE THROUGH THE MAZE. YOU ARE " + token + ".");
            writer.flush();
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    boolean moveMade = false;
                    if (message.equalsIgnoreCase("n")) {
                        hasWon = maze.moveUp(position, token);
                        moveMade = true;
                    } else if (message.equalsIgnoreCase("s")) {
                        hasWon = maze.moveDown(position, token);
                        moveMade = true;
                    } else if (message.equalsIgnoreCase("w")) {
                        hasWon = maze.moveLeft(position, token);
                        moveMade = true;
                    } else if (message.equalsIgnoreCase("e")) {
                        hasWon = maze.moveRight(position, token);
                        moveMade = true;
                    } else if (message.startsWith("see gold")) {
                        int maxGoldAmtCanCollect = maze.callFindMaxGold(position);
                        writer.println("YOU CAN COLLECT " + maxGoldAmtCanCollect + " UNITS OF GOLD AT CURRENT POSITION");
                    }
                    else if(message.startsWith("how far am i?"))
                    {
                        int distance = maze.shortestPathBinaryMatrix(position, token);
                        writer.println("YOU ARE AT LEAST " + distance + " UNITS AWAY FROM DESTINATION AT CURRENT POSITION");
                    }
                    else {
                        writer.println("YOU: INVALID COMMAND!");
                    }

                    if (moveMade) {
                        writer.println("YOU:");
                        tellEveryoneBut(token + ":", writer);
                        tellEveryone(maze.show());

                        if (hasWon) {
                            writer.println("YOU HAVE WON!");
                            writer.flush();
                            tellEveryoneBut(token + " HAS WON!", writer);
                            tellEveryone("QUIT OR CONTINUE PLAYING...");
                            maze.initialize();
                            resetEveryone();
                            hasWon = false;
                            for (ClientHandler client : clients) {
                                PrintWriter writer = client.writer;
                                writer.println("WELCOME TO MazeCraze!");
                                writer.println(client.maze.show());
                                writer.println("PRESS N, S, W, E TO NAVIGATE THROUGH THE MAZE. YOU ARE " + client.token + ".");
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

        // Instantiate a single shared Maze object in the server class.
        // This instance will be accessed and modified by all client threads.
        maze = Maze.getInstance();
        // initialize the maze
        maze.initialize();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);

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
                System.out.println("got a connection");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
