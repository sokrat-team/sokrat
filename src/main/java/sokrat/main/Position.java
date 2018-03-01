package sokrat.main;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Position {

    private int x;
    private int y;



    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int distanceTo(Position that){
        return Math.abs(this.x - that.x ) + Math.abs(this.y - that.y );
    }
}
