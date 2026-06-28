package pe.edu.upeu.sysdenuncias.service.impl;

import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.model.Evidencia;
import pe.edu.upeu.sysdenuncias.repository.EvidenciaRepository;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.service.IEvidenciaService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

public class EvidenciaServiceImp extends CrudGenericoServiceImp<Evidencia, Long> implements IEvidenciaService {

    private final EvidenciaRepository repo;

    public EvidenciaServiceImp(EvidenciaRepository repo) {
        super(repo);
        this.repo = repo;
    }
    @Override
    protected ICrudGenericoRepository<Evidencia, Long> getRepo() {
        return repo;
    }

    @Override
    public Evidencia guardarConArchivo(Evidencia evidencia, File archivoSeleccionado) {
        Denuncia denuncia = evidencia.getDenuncia();
        if (denuncia == null) {
            throw new RuntimeException("La denuncia asociada no puede ser nula");
        }

        List<Evidencia> existentes = repo.findByDenuncia(denuncia.getId());
        if (existentes != null && existentes.size() >= 3) {
            throw new RuntimeException("Límite de 3 evidencias alcanzado");
        }

        try {
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File destino = new File(uploadDir, archivoSeleccionado.getName());
            Files.copy(archivoSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            evidencia.setNombreArchivo(archivoSeleccionado.getName());
            evidencia.setRutaArchivo(destino.getPath().replace("\\", "/"));

            String tipoArchivo = Files.probeContentType(archivoSeleccionado.toPath());
            if (tipoArchivo == null) {
                String name = archivoSeleccionado.getName();
                int lastDot = name.lastIndexOf('.');
                if (lastDot > 0) {
                    tipoArchivo = name.substring(lastDot + 1);
                } else {
                    tipoArchivo = "desconocido";
                }
            }
            evidencia.setTipoArchivo(tipoArchivo);
            evidencia.setFechaSubida(LocalDateTime.now());

            Evidencia guardada = repo.save(evidencia);
            if (denuncia.getEvidencias() != null) {
                denuncia.getEvidencias().add(guardada);
            }
            return guardada;

        } catch (IOException e) {
            throw new RuntimeException("Error al copiar el archivo de evidencia: " + e.getMessage(), e);
        }
    }
}
