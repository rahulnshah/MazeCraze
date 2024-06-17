import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DailyAdviceServer {
    private final String [] adviceList = {"Take Smaller Bites", "Go for walks", "Play sports",
    "Drink water"};
    public static void main(String [] args)
    {
        DailyAdviceServer server = new DailyAdviceServer();
        server.go();
    }

    private void go() {
        try {
            ServerSocket serverSocket = new ServerSocket(4242);

            while(true)
            {
                Socket sock = serverSocket.accept();

                PrintWriter writer = new PrintWriter(sock.getOutputStream());
                String advice = getAdvice();
                writer.println(advice);
                writer.close();
                System.out.println("Advice from server: " + advice);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getAdvice() {
        return adviceList[(int) (Math.random() * adviceList.length)];
    }
}
