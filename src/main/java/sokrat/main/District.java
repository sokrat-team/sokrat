package sokrat.main;

public class District {

    int nbRows;
    int nbColumns;
    int districtsSize;

    public District(int nbRows, int nbColumns, int districtsSize) {
        this.nbRows = nbRows;
        this.nbColumns = nbColumns;
        this.districtsSize = districtsSize;
    }

    public int getNbRows() {
        return nbRows;
    }

    public void setNbRows(int nbRows) {
        this.nbRows = nbRows;
    }

    public int getNbColumns() {
        return nbColumns;
    }

    public void setNbColumns(int nbColumns) {
        this.nbColumns = nbColumns;
    }

    public int districtNumber(Position p){
        int xIndex = 1 + p.getX() / districtsSize;
        int nbRows = p.getX() / districtsSize;

        int yIndex = 1 + (p.getY() / districtsSize);
        return xIndex*yIndex;
    }
}
