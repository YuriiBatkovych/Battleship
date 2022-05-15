import java.util.LinkedList;
import java.util.List;

public class Ship {
    List<Point> ship;
    boolean isFlooded;

    Ship()
    {
        ship = new LinkedList<>();
        isFlooded = false;
    }

    public boolean Contains(Point p) { return  ship.contains(p); }

    public void Add(Point p){
        ship.add(p);
    }

    public void show()
    {
        for (Point p: ship) {
            p.show();
            System.out.print(",");
        }

        System.out.println();
    }

    public void shot(Point p)
    {
        for (Point point: ship) {
            if(point.equals(p))
                point.shot();
        }

        for (Point point: ship) {
            if(!point.isFlooded())
                return;
        }

        isFlooded = true;
    }
}
