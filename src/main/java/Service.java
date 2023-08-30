import java.io.*;
import java.net.Socket;

public class Service implements Runnable {

    Socket socket;

    public Service(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            sandMessage("welcome from server");
            while (true) {
                String message = receiveMessage();
                System.out.println("Received:" + message);
                if (message.equals("exit")) {
                    socket.shutdownOutput();
                    break;
                } else {
                    sandMessage(message.toUpperCase());
                }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sandMessage(String msg) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private String receiveMessage() throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.readLine();
    }
}
