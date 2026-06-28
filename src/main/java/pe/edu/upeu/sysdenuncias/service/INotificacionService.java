package pe.edu.upeu.sysdenuncias.service;

import pe.edu.upeu.sysdenuncias.model.Ciudadano;

import java.io.File;

public interface INotificacionService {

    void notificarCiudadano(Ciudadano ciudadano, String mensaje);

    void enviarCorreo(Ciudadano ciudadano, String mensaje);

    void enviarWhatsApp(Ciudadano ciudadano, String mensaje);
    void enviarCorreoConAdjunto(
            Ciudadano ciudadano,
            String mensaje,
            File archivoPdf
    );
}