import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GreetServer {

    private String map; // 10X10
    private  ServerSocket serverSocket ;

    GreetServer(String map)
    {
        this.map = map;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();

            Session session = new Session(clientSocket, Mode.SERVER, map);
            session.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try{
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}