package sokrat.main;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Position {

    private int x;
    private int y;

    public static final Position INITIAL_POSITION=new Position(0,0);

    public Position(int x, int y) {
        this.x =x;
        this.y = y;
    }

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

    public Position moveTo(Position that){
        if (this.x != that.x){


        }
        return null;
    }
}
