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

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket)
        {
            try
            {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
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
                    tellEveryone(message);
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
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
