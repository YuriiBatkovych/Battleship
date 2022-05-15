import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Session {
    private final Socket socket;
    private final Mode mode;
    private final BufferedWriter out;
    private final BufferedReader in;
    private final char[][] myMap;
    private final char[][] opponentMap;
    private List<Ship> myShips;
    private final List<Ship> opponentShips;

    private String lastSentMessage;
    private int errorCounter;
    private boolean started;

    private final Scanner scanner = new Scanner(System.in);

    public Session(Socket socket, Mode mode, String map) throws IOException {
        this.socket = socket;
        this.mode = mode;
        this.myMap = mapToMatrix(map);
        this.opponentMap = generateOpponentMap();
        this.opponentShips = new LinkedList<>();
        out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        findMyShips();

        this.lastSentMessage = "";
        this.errorCounter = 0;
        this.started = false;
    }

    private Ship pointIsInShips(Point p, List<Ship> Ships)
    {
        for (Ship ship: Ships) {
            if(ship.Contains(p))
                return ship;
        }
        return null;
    }

    private Ship connectShips(Ship a, Ship b)
    {
        Ship newShip = new Ship();

        newShip.ship.addAll(a.ship);
        newShip.ship.addAll(b.ship);

        return newShip;
    }

    private void findMyShips()
    {
        myShips = new LinkedList<>();

        for(int i=0; i<10; i++)
        {
            for (int j=0; j<10; j++)
            {
                if(myMap[i][j] == '#')
                {
                    Ship shipOver = pointIsInShips(new Point(i-1, j), myShips);
                    Ship shipBefore = pointIsInShips(new Point(i, j-1), myShips);

                    if(shipBefore!=null && shipOver!=null) {
                        var newShip = connectShips(shipBefore, shipOver);
                        newShip.Add(new Point(i,j));
                        myShips.remove(shipBefore);
                        myShips.remove(shipOver);
                        myShips.add(newShip);
                    }
                    else if(shipBefore!=null) shipBefore.Add(new Point(i,j));
                    else if(shipOver!=null) shipOver.Add(new Point(i,j));
                    else {
                        Ship newShip = new Ship();
                        newShip.Add(new Point(i, j));
                        myShips.add(newShip);
                    }
                }
            }
        }
    }

    private boolean allFlooded()
    {
        for (Ship ship: myShips) {
            if (!ship.isFlooded)
                return false;
        }

        return true;
    }

    private char[][] generateOpponentMap()
    {
        char[][] opponentMap = new char[10][10];

        for(int i=0; i<10; i++)
            for(int j=0; j<10; j++)
                opponentMap[i][j] = '?';

        return opponentMap;
    }

    private String getNextCoordinates()
    {
        System.out.println("Enter next coordinates to attack :");
        return scanner.next();
    }

    private String checkMyMap(String coordinates) {
        Point point = new Point(coordinates);

        if (myMap[point.Row()][point.Column()] == '.') {
            myMap[point.Row()][point.Column()] = '~';
            return "pudło;";
        }
        else if (myMap[point.Row()][point.Column()] == '~') {
            return "pudło;";
        }
        else if(myMap[point.Row()][point.Column()] == '#')
        {
            myMap[point.Row()][point.Column()] = '@';
            Ship ship = pointIsInShips(point, myShips);
            ship.shot(point);

            if(allFlooded())
                return "ostatni zatopiony;";

            if(ship.isFlooded)
                return "trafiony zatopiony;";

            return  "trafiony;";
        }
        else
        {
            Ship ship = pointIsInShips(point, myShips);

            if(ship.isFlooded)
                return "trafiony zatopiony;";

            return  "trafiony;";
        }
    }

    private Ship getAlienShip(Point p) //if null to ten strzał już był wykonany
    {
        if(pointIsInShips(p, opponentShips) != null ) return null;

        Ship shipOver = pointIsInShips(new Point(p.Row()-1, p.Column()), opponentShips);
        Ship shipAfter = pointIsInShips(new Point(p.Row(), p.Column()+1), opponentShips);
        Ship shipUnder = pointIsInShips(new Point(p.Row()+1, p.Column()), opponentShips);
        Ship shipBefore = pointIsInShips(new Point(p.Row(), p.Column()-1), opponentShips);

        if(shipOver != null) {
            shipOver.Add(p);
            return  shipOver;
        }
        else if(shipAfter != null){
            shipAfter.Add(p);
            return  shipAfter;
        }
        else if(shipUnder != null){
            shipUnder.Add(p);
            return shipUnder;
        }
        else if(shipBefore != null){
            shipBefore.Add(p);
            return  shipBefore;
        }

        Ship newShip = new Ship();
        newShip.Add(p);
        opponentShips.add(newShip);
        return newShip;

    }

    private void freeAlienSurround(Ship ship)
    {
        for (Point p: ship.ship) {
            if(p.Row()!=0)
            {
                if(opponentMap[p.Row()-1][p.Column()] == '?')
                    opponentMap[p.Row()-1][p.Column()] = '.';

                if(p.Column()!=0 && opponentMap[p.Row()-1][p.Column()-1] == '?')
                    opponentMap[p.Row()-1][p.Column()-1] = '.';
                if(p.Column()!=9 && opponentMap[p.Row()-1][p.Column()+1] == '?')
                    opponentMap[p.Row()-1][p.Column()+1] = '.';
            }

            if(p.Column()!=0 && opponentMap[p.Row()][p.Column()-1] == '?')
                opponentMap[p.Row()][p.Column()-1] = '.';
            if(p.Column()!=9 && opponentMap[p.Row()][p.Column()+1] == '?')
                opponentMap[p.Row()][p.Column()+1] = '.';

            if(p.Row()!=9)
            {
                if(opponentMap[p.Row()+1][p.Column()] == '?')
                    opponentMap[p.Row()+1][p.Column()] = '.';

                if(p.Column()!=0 && opponentMap[p.Row()+1][p.Column()-1] == '?')
                    opponentMap[p.Row()+1][p.Column()-1] = '.';
                if(p.Column()!=9 && opponentMap[p.Row()+1][p.Column()+1] == '?')
                    opponentMap[p.Row()+1][p.Column()+1] = '.';
            }
        }
    }

    private void updateOpponentMap(String coordinates, String command)
    {
        Point point = new Point(coordinates);

        switch (command) {
            case "pudło" -> opponentMap[point.Row()][point.Column()] = '.';
            case "trafiony" -> {
                opponentMap[point.Row()][point.Column()] = '#';
                getAlienShip(point);
            }
            case "trafiony zatopiony" -> {
                opponentMap[point.Row()][point.Column()] = '#';
                Ship ship = getAlienShip(point);
                if (ship != null) freeAlienSurround(ship);
            }
            case "ostatni zatopiony" -> {
                opponentMap[point.Row()][point.Column()] = '#';
                for (int i = 0; i < 10; i++)
                    for (int j = 0; j < 10; j++)
                        if (opponentMap[i][j] == '?')
                            opponentMap[i][j] = '.';
            }
        }
    }

    private String getCommand(String inputLine) { return inputLine.substring(0, inputLine.indexOf(';')); }

    private String getCoordinates(String inputLine) {
        return inputLine.substring(inputLine.indexOf(';')+1);
    }

    public void run() {
        try {
            System.out.println("My map : ");
            showMatrix(myMap);

            String lastCoordinates = "";

            if (mode == Mode.CLIENT) {
                lastCoordinates = getNextCoordinates();
                send("start;", lastCoordinates);
                started = true;
            }

            while (true) {

                if (errorCounter >= 3) {
                    System.out.println("Błąd komunikacji");
                    stopConnection();
                    break;
                }

                String inputLine = "";

// realizacja sprawdznia timeoutu w 1 sekunde została zamianiona na 60 sekund, ponieważ to jest nierealistyczne,
// żeby cłowiek ciągle wpisywał dane za 3 sekundy do konsoli przez całą gre

                var start = System.currentTimeMillis();

                if (!started) inputLine = in.readLine();

                while (started) {
                    if (in.ready()) {
                        inputLine = in.readLine();
                        break;
                    }

                    var end = System.currentTimeMillis();
                    if (end - start > 60000) {
                        send(getCommand(lastSentMessage) + ";", getCoordinates(lastSentMessage));
                        errorCounter++;
                        break;
                    }
                }

                if (inputLine != null && inputLine.length() > 0) {
                    System.out.println("Received : " + inputLine);
                    String command = getCommand(inputLine);
                    String coordinates = getCoordinates(inputLine);

                    String returnCommand = "";

                    if ("start".equals(command)) {
                        returnCommand = checkMyMap(coordinates);
                        errorCounter = 0;
                        started = true;
                    } else if ("pudło".equals(command)) {
                        updateOpponentMap(lastCoordinates, "pudło");
                        returnCommand = checkMyMap(coordinates);
                        errorCounter = 0;
                    } else if ("trafiony".equals(command)) {
                        updateOpponentMap(lastCoordinates, "trafiony");
                        returnCommand = checkMyMap(coordinates);
                        errorCounter = 0;
                    } else if ("trafiony zatopiony".equals(command)) {
                        updateOpponentMap(lastCoordinates, "trafiony zatopiony");
                        returnCommand = checkMyMap(coordinates);
                        errorCounter = 0;
                    } else if ("ostatni zatopiony".equals(command)) {
                        updateOpponentMap(lastCoordinates, "ostatni zatopiony");
                        System.out.println("Wygrana");

                        showMatrix(opponentMap);
                        System.out.println();
                        showMatrix(myMap);
                        stopConnection();
                        errorCounter = 0;
                        break;
                    } else {
                        System.out.println("Unrecognised command");
                        send(getCommand(lastSentMessage) + ";", getCoordinates(lastSentMessage));
                        errorCounter++;
                        continue;
                    }

                    if (returnCommand.equals("ostatni zatopiony;")) {
                        System.out.println("Przegrana");

                        showMatrix(opponentMap);
                        System.out.println();
                        showMatrix(myMap);

                        send(returnCommand, "");
                        stopConnection();
                        break;
                    } else {
                        lastCoordinates = getNextCoordinates();
                        send(returnCommand, lastCoordinates);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String command, String coordinates) throws IOException {
        String toSend = command + coordinates + "\n";
        lastSentMessage = command + coordinates;
        System.out.print("Sending "+toSend);
        out.write(toSend);
        out.newLine();
        out.flush();
    }

    private char[][] mapToMatrix(String map)
    {
        char[][] mapMatrix = new char[10][10];

        for(int i=0; i<10; i++)
            for (int j=0; j<10; j++) {
                mapMatrix[i][j] = map.charAt(i*10 + j);
            }

        return mapMatrix;
    }

    private static void showMatrix(char[][] matrix)
    {
        for(int i=0; i<10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
    }

    private void stopConnection()
    {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
