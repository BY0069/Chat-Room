package Utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Poster {

    private Poster() {}
    public static void sendMessage(@NotNull Socket socket, String msg) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        bufferedWriter.write(msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public static String receiveMessage(@NotNull Socket socket) throws IOException{
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.readLine();
    }
}
