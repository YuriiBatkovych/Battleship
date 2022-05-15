class Point
{
    private final int row;
    private final int column;
    private boolean isFlooded;

    Point(int _row, int _column)
    {
        this.row = _row;
        this.column = _column;
        isFlooded = false;
    }

    Point(String coordinates)
    {
        this.row = Integer.parseInt(coordinates.substring(1)) -1 ;
        this.column = coordinates.charAt(0) - 65;
        isFlooded = false;
    }

    public int Row() { return row; }
    public int Column() { return column; }
    public boolean isFlooded() { return  isFlooded; };

    public void shot() {
        isFlooded = true;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean result = false;
        if(o instanceof Point)
        {
            Point p = (Point) o;
            result = (this.row == p.row && this.column == p.column);
        }

        return result;
    }

    public void show()
    {
        System.out.print("("+row+","+column+")");
    }
}

