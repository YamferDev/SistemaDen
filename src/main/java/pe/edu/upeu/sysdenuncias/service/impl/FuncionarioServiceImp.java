package pe.edu.upeu.sysdenuncias.service.impl;

import pe.edu.upeu.sysdenuncias.enums.Especialidad;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import pe.edu.upeu.sysdenuncias.repository.FuncionarioRepository;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.service.IFuncionarioService;

import java.util.Optional;

public class FuncionarioServiceImp extends CrudGenericoServiceImp<Funcionario, Long> implements IFuncionarioService {

    private final FuncionarioRepository repo;

    public FuncionarioServiceImp(FuncionarioRepository repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Funcionario, Long> getRepo() {
        return repo;
    }

    @Override
    public Optional<Funcionario> loginFuncionario(String nombre, String credenciales) {
        return repo.findByCredenciales(nombre, credenciales);
    }

    @Override
    public boolean existeConDni(String dni, Long excludeId) {
        return repo.existeConDni(dni, excludeId);

    }

    @Override
    public Optional<Funcionario> findInspectorByEspecialidad(
            Especialidad especialidad) {

        return repo.findInspectorByEspecialidad(especialidad);
    }

}