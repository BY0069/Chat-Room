import java.io.*;
import java.net.Socket;

public class Client {
    static String host = "127.0.0.1";
    static int port = 10899;

    public void start() {
        try (Socket client = new Socket(host, port)) {
            client.setKeepAlive(true);
            OutputStream outputStream = client.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            InputStream inputStream = client.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            bufferedWriter.write("hello from client");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String message = bufferedReader.readLine();
            System.out.println(message);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
