package pe.edu.upeu.sysdenuncias.components;

public class ColumnInfo {
    private String field;
    private Double width;

    public ColumnInfo(String field) {
        this.field = field;
    }

    public ColumnInfo(String field, Double width) {
        this.field = field;
        this.width = width;
    }

    public String getField() {
        return field;
    }

    public Double getWidth() {
        return width;
    }
}