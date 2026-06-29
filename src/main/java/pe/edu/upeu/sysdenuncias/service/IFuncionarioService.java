package pe.edu.upeu.sysdenuncias.service;

import pe.edu.upeu.sysdenuncias.enums.Especialidad;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import java.util.Optional;

public interface IFuncionarioService extends ICrudGenericoService<Funcionario, Long> {

    Optional<Funcionario> loginFuncionario(
            String nombre,
            String credenciales);

    boolean existeConDni(
            String dni,
            Long excludeId);

    Optional<Funcionario> findInspectorByEspecialidad(
            Especialidad especialidad
    );
}