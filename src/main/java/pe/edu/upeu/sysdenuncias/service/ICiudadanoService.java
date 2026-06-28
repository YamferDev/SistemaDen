package pe.edu.upeu.sysdenuncias.service;

import pe.edu.upeu.sysdenuncias.model.Ciudadano;

public interface ICiudadanoService extends ICrudGenericoService<Ciudadano, Long> {
    boolean existeConDni(String dni, Long excludeId);
}