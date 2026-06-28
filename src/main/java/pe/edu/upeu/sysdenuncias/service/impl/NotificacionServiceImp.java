package pe.edu.upeu.sysdenuncias.service.impl;

import pe.edu.upeu.sysdenuncias.model.Ciudadano;
import pe.edu.upeu.sysdenuncias.service.INotificacionService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class NotificacionServiceImp implements INotificacionService {
    private static final String PROPS_PATH = "src/main/resources/application.properties";

    @Override
    public void notificarCiudadano(Ciudadano ciudadano, String mensaje) {
        enviarWhatsApp(ciudadano, mensaje);
    }


    @Override
    public void enviarWhatsApp(Ciudadano ciudadano, String mensaje) {
        String telefono = ciudadano.getTelefono();
        if (telefono == null || telefono.isBlank()) {
            System.err.println("[WhatsApp] El ciudadano no tiene teléfono registrado: " + ciudadano.getNombre());
            return;
        }

        String telefonoLimpio = telefono.replaceAll("[^0-9]", "");
        if (telefonoLimpio.length() == 9) {
            telefonoLimpio = "51" + telefonoLimpio; // código Perú
        }

        try {
            String mensajeCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
            String url = "https://wa.me/" + telefonoLimpio + "?text=" + mensajeCodificado;

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("[WhatsApp] Abriendo navegador para: " + ciudadano.getNombre());
            } else {
                System.err.println("[WhatsApp] Desktop no soportado. URL generada: " + url);
            }
        } catch (Exception e) {
            System.err.println("[WhatsApp] Error al abrir navegador: " + e.getMessage());
        }
    }
    @Override
    public void enviarCorreo(Ciudadano ciudadano, String mensajeTexto) {
        String correo = ciudadano.getCorreo();
        if (correo == null || correo.isBlank()) {
            System.err.println("[Correo] El ciudadano no tiene correo registrado: " + ciudadano.getNombre());
            return;
        }

        try {
            Properties config = cargarConfig();
            String host     = config.getProperty("mail.smtp.host", "smtp.gmail.com");
            String port     = config.getProperty("mail.smtp.port", "587");
            String username = config.getProperty("mail.smtp.username");
            String password = config.getProperty("mail.smtp.password");
            String fromName = config.getProperty("mail.smtp.from.name", "Sistema de Denuncias");

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
            message.setSubject("Notificación de su Denuncia - " + fromName);

            // HTML email
            String htmlContent = construirHtmlCorreo(ciudadano, mensajeTexto);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[Correo] Enviado correctamente a: " + correo);

        } catch (Exception e) {
            System.err.println("[Correo] Error al enviar correo: " + e.getMessage());
        }
    }

    private String construirHtmlCorreo(Ciudadano ciudadano, String mensajeTexto) {
        String mensajeHtml = mensajeTexto.replace("\n", "<br>");
        return """
            <html>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;">
              <div style="max-width: 600px; margin: auto; background: white; border-radius: 8px;
                          border-top: 5px solid #1565C0; padding: 30px;">
                <h2 style="color: #1565C0; margin-top: 0;">Municipalidad - Sistema de Denuncias</h2>
                <hr style="border: none; border-top: 1px solid #ddd;"/>
                <p>Estimado/a <strong>%s</strong>,</p>
                <div style="background: #E3F2FD; border-left: 4px solid #1565C0;
                            padding: 15px; border-radius: 4px; margin: 20px 0;">
                  %s
                </div>
                <p style="color: #666; font-size: 12px;">
                  Este es un mensaje automático del Sistema de Denuncias Ciudadanas.<br>
                  Por favor no responda este correo.
                </p>
                <hr style="border: none; border-top: 1px solid #ddd;"/>
                <p style="color: #999; font-size: 11px; text-align: center;">
                  Municipalidad &copy; %d
                </p>
              </div>
            </body>
            </html>
            """.formatted(
                ciudadano.getNombre(),
                mensajeHtml,
                java.time.Year.now().getValue()
        );
    }

    private Properties cargarConfig() {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(PROPS_PATH)) {
            props.load(is);
        } catch (IOException e) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                if (is != null) props.load(is);
            } catch (IOException ex) {
                System.err.println("[Config] No se pudo cargar application.properties: " + ex.getMessage());
            }
        }
        return props;
    }
    @Override
    public void enviarCorreoConAdjunto(
            Ciudadano ciudadano,
            String mensajeTexto,
            File archivoPdf
    ) {

        try {

            Properties config = cargarConfig();

            String host = config.getProperty("mail.smtp.host");
            String port = config.getProperty("mail.smtp.port");
            String username = config.getProperty("mail.smtp.username");
            String password = config.getProperty("mail.smtp.password");
            String fromName = config.getProperty("mail.smtp.from.name");

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(
                    props,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    }
            );

            MimeMessage message = new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(username, fromName)
            );

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(ciudadano.getCorreo())
            );

            message.setSubject(
                    "Constancia de Denuncia N°"
            );

            MimeBodyPart cuerpo = new MimeBodyPart();
            cuerpo.setContent(
                    construirHtmlCorreo(ciudadano, mensajeTexto),
                    "text/html; charset=utf-8"
            );

            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.attachFile(archivoPdf);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpo);
            multipart.addBodyPart(adjunto);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println(
                    "[Correo] Constancia enviada con adjunto"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}