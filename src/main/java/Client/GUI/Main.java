package Client.GUI;

import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("127.0.0.1", 10899);
        new Login(client);
    }
}
