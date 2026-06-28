package pe.edu.upeu.sysdenuncias.service.impl;

import pe.edu.upeu.sysdenuncias.model.Ciudadano;
import pe.edu.upeu.sysdenuncias.repository.CiudadanoRepository;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.service.ICiudadanoService;
public class CiudadanoServiceImp extends CrudGenericoServiceImp<Ciudadano, Long> implements ICiudadanoService {

    private final CiudadanoRepository repo;

    public CiudadanoServiceImp(CiudadanoRepository repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Ciudadano, Long> getRepo() {
        return repo;
    }

    @Override
    public boolean existeConDni(String dni, Long excludeId) {
        return repo.existeConDni(dni, excludeId);
    }
}