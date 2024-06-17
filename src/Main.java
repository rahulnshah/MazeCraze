import java.io.BufferedReader;
import java.io.InputStreamReader;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        try {
            String message;
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            while ((message = consoleInput.readLine()) != null) {
                System.out.println(message);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}