import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static String host = "127.0.0.1";
    static int port = 10899;

    public void start() {
        try (Socket client = new Socket(host, port)) {
            String message;
            Scanner scanner = new Scanner(System.in);
            while ((message = receive(client)) != null) {
                if (message.equals("exit")) {
                    client.close();
                    break;
                } else {
                    String sendMsg;
                    System.out.println(message);
                    if (scanner.hasNextLine()) {
                        sendMsg = scanner.nextLine();
                        send(client, sendMsg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(@NotNull Socket socket, String msg) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private String receive(@NotNull Socket socket) throws IOException{
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.readLine();
    }
}
