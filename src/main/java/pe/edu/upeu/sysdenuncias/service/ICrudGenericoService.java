package pe.edu.upeu.sysdenuncias.service;

import java.util.List;
import java.util.Optional;


public interface ICrudGenericoService<T, ID> {
    T save(T entity);
    T update(ID id, T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
}