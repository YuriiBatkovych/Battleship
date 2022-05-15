import java.io.*;

public class Main {

    static String mode;
    static int port;
    static String serverIp;
    static String mapFile;

    public static void main(String[] args)  {

          if(args.length > 0)
          {
              mode = findParametr("-mode", args);
              port = Integer.parseInt(findParametr("-port", args));
              mapFile = findParametr("-map", args);

              if(mode.equals("client")) serverIp = findParametr("-address", args);

              if(!mapFile.equals("undefined"))
              {
                  String map = readMap(mapFile);

                  if(mode.equals("server"))
                  {
                      GreetServer server = new GreetServer(map);
                      server.start(port);
                  }
                  else
                  {
                      GreetClient client = new GreetClient();
                      client.startConnection(serverIp, port, map);
                  }
              }
              else System.out.println("Please enter correct map file path");
          }
          else System.out.println("Enter arguments of the application");
    }

    private static String findParametr(String parametr, String[] args)
    {
        for(int i=0; i<args.length; i++)
        {
            if(args[i].equals(parametr) && i+1 < args.length)
                return  args[i+1];
        }

        return "undefined";
    }

    private static String readMap(String mapFilePath)
    {
        StringBuilder mapBuilder = new StringBuilder("");

        try (var r = new BufferedReader(new FileReader(mapFilePath))){
            String line="";
            while ((line = r.readLine()) != null)
            {
                mapBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapBuilder.toString();
    }
}
