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
        int nbDistrictPerRows = nbColumns/districtsSize;
        return (p.getX()/districtsSize)  + ((p.getY() / districtsSize) * nbDistrictPerRows) + 1;
    }

    public  boolean sameDistrict(Position p1, Position p2){
            return districtNumber(p1) == districtNumber(p2);
    }
}
