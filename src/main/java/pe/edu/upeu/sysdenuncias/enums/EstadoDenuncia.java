package pe.edu.upeu.sysdenuncias.enums;


public enum EstadoDenuncia {
    PENDIENTE,
    EN_PROCESO,
    RESUELTO,
    NOTIFICADO, EN_APELACION, RECHAZADO;

    
    public String getLabel() {
        return switch (this) {
            case PENDIENTE   -> "Pendiente";
            case EN_PROCESO  -> "En Proceso";
            case RESUELTO    -> "Resuelto";
            case NOTIFICADO  -> "Notificado";
            case RECHAZADO    -> "Rechazado";
            case EN_APELACION -> "En Apelación";
        };
    }
}