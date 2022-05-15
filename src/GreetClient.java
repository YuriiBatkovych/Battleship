import java.io.IOException;
import java.net.Socket;

public class GreetClient {
    GreetClient(){};

    public void startConnection(String ip, int port, String map) {
        try{
            Socket clientSocket = new Socket(ip, port);

            Session session = new Session(clientSocket, Mode.CLIENT, map);
            session.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}