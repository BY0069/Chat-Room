import java.io.*;
import java.net.Socket;

public class Service implements Runnable {

    Socket socket;

    public Service(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                Reader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                BufferedWriter bufferedWriter = new BufferedWriter(writer)
        ) {

            String message = bufferedReader.readLine();
            System.out.println(message);


            bufferedWriter.write("hello from server");
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
