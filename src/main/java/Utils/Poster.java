package Utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class Poster {

    private Poster() {}
    public static void sandMessage(@NotNull Socket socket, String msg) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public static String receiveMessage(@NotNull Socket socket) throws IOException{
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.readLine();
    }
}
