import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class DailyAdviceServer {
    ArrayList<PrintWriter> clientOutputStreams;

    Maze maze;

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
                // initialize the row and column position for this client
                if(clientOutputStreams.size() == 1)
                {
                    // give first player to connect the position of the turtle
                    this.position[0] = 0;
                    this.position[1] = 2;
                    this.token = '^';
                }
                else
                {
                    // give second player to connect the position of the hare
                    this.position[0] = 4;
                    this.position[1] = 2;
                    this.token = '*';
                }
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
            boolean hasWon = false;
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
                        // TODO: reset grid
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void tellEveryone(String message) {
        Iterator<PrintWriter> it = clientOutputStreams.iterator();
        while (it.hasNext())
        {
            try{
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
                Thread t = new Thread(new ClientHandler(clientSocket, maze));
                t.start();
                System.out.println("got a connection");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
