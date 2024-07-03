import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DailyAdviceServer {
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

        char token;

        public ClientHandler(Socket clientSocket, Maze maze)
        {
            try
            {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
                this.maze = maze;
                // add it to the List
                clients.add(this);
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
            // read messages from the server and print them to the console
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    if(message.equals("n"))
                    {
                        hasWon = maze.moveUp(position, token);
                    }
                    else if(message.equals("d"))
                    {
                        hasWon = maze.moveDown(position, token);
                    }
                    else if(message.equals("w"))
                    {
                        hasWon = maze.moveLeft(position, token);
                    }
                    else if(message.equals("e"))
                    {
                        hasWon = maze.moveRight(position, token);
                    }
                    else
                    {
                        // TODO: see if it is possible to get the write reference from the List using sock.getOutputStream()
                        PrintWriter writer = new PrintWriter(sock.getOutputStream());
                        writer.println("Invalid command");
                    }
                    tellEveryone(maze.show());
                    if(hasWon)
                    {
                        tellEveryone(token + " HAS WON! QUIT OR CONTINUE PLAYING");
                        // reset grid
                        maze.initialize();
                        // reset player position
                        resetEveryone();
                        // set hasWon to false
                        hasWon = false;
                    }
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

    public static void main(String [] args)
    {
        DailyAdviceServer server = new DailyAdviceServer();
        server.go();
    }

    private void go() {
        clientOutputStreams = new ArrayList<>();

        // Instantiate a single shared Maze object in the server class.
        // This instance will be accessed and modified by all client threads.
        maze = new Maze();
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
