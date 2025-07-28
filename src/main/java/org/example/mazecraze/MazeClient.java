package org.example.mazecraze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MazeClient {
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    BufferedReader consoleInput;
    
    public static void main(String [] args)
    {
        MazeClient client = new MazeClient();
        client.go();
    }

    private void go() {
        try {
            // Set up networking
            setUpNetworking();

            // take user input as a message and send it to server
            consoleInput = new BufferedReader(new InputStreamReader(System.in));

            // start a new thread to read any messages coming from server
            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();

            // Read messages from the console and send them to the server
            String message;
            while ((message = consoleInput.readLine()) != null) {
                writer.println(message);
                // clear the output stream of any characters that may be or maybe not inside the stream
                writer.flush();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void setUpNetworking() {
        try{
            sock = new Socket("127.0.0.1", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            // Set up input and output streams
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            // read messages from the server and print them to the console
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
