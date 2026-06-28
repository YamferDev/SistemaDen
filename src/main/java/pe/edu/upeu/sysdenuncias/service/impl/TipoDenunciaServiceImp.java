package pe.edu.upeu.sysdenuncias.service.impl;

import pe.edu.upeu.sysdenuncias.model.TipoDenuncia;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.repository.TipoDenunciaRepository;
import pe.edu.upeu.sysdenuncias.service.ITipoDenunciaService;

public class TipoDenunciaServiceImp extends CrudGenericoServiceImp<TipoDenuncia, Long> implements ITipoDenunciaService {

    private final TipoDenunciaRepository repo;

    public TipoDenunciaServiceImp(TipoDenunciaRepository repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<TipoDenuncia, Long> getRepo() {
        return repo;
    }
}