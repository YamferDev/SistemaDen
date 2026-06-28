package pe.edu.upeu.sysdenuncias.enums;


public enum Genero {
    MASCULINO,
    FEMENINO;

    public String getLabel() {
        return switch (this) {
            case MASCULINO -> "Masculino";
            case FEMENINO  -> "Femenino";
        };
    }
}