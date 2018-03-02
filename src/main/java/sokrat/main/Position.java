package sokrat.main;

import com.google.common.collect.ComparisonChain;

public class Position implements Comparable<Position> {

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

    public Position moveTowards(Position that){
        if (this.x != that.x){
            return  moveX(that);
        }else {
            return moveY(that);
        }
    }

    private Position moveX(Position that) {
        if (this.x == that.x){
            return this;
        }
        if (this.x > that.x) return new Position(this.x-1, this.y);
        else return new Position(this.x+1, this.y);
    }

    private Position moveY(Position that) {
        if (this.y == that.y){
            return this;
        }
        if (this.y > that.y) return new Position(this.x, this.y-1);
        else return new Position(this.x, this.y+1);
    }

    @Override
    public int compareTo(Position that) {
        return ComparisonChain.start()
                .compare(this.x, that.x)
                .compare(this.y, that.y)
                .result();
    }
}
