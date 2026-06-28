package pe.edu.upeu.sysdenuncias.service.impl;

import pe.edu.upeu.sysdenuncias.exception.ModelNotFoundException;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.service.ICrudGenericoService;

import java.util.List;
import java.util.Optional;

public abstract class CrudGenericoServiceImp<T, ID> implements ICrudGenericoService<T, ID> {

    public CrudGenericoServiceImp(ICrudGenericoRepository<T, ID> repo) {
    }

    protected abstract ICrudGenericoRepository<T, ID> getRepo();

    @Override
    public T save(T entity) {
        return getRepo().save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        if (!getRepo().existsById(id)) {
            throw new ModelNotFoundException("El ID no existe: " + id);
        }
        return getRepo().update(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public void delete(ID id) {
        if (!getRepo().existsById(id)) {
            throw new ModelNotFoundException("El ID no existe: " + id);
        }
        getRepo().deleteById(id);
    }
}