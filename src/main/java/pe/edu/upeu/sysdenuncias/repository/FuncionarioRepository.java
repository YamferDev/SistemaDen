package pe.edu.upeu.sysdenuncias.repository;

import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.enums.Especialidad;
import pe.edu.upeu.sysdenuncias.model.Funcionario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class FuncionarioRepository extends AbstractJdbcRepository<Funcionario, Long> {

    @Override
    protected String getTableName() {
        return "funcionario";
    }

    @Override
    protected String getPkColumn() {
        return "id";
    }

    public Optional<Funcionario> findByCredenciales(String nombre, String credenciales) {
        return executeQueryOne("SELECT * FROM funcionario WHERE nombre = ? AND credenciales = ?", nombre, credenciales);
    }

    @Override
    protected Funcionario insert(Connection connection, Funcionario entity) throws SQLException {

        long id = executeInsertGetKey(
                connection,
                "INSERT INTO funcionario(dni, nombre, cargo, especialidad, credenciales) VALUES(?,?,?,?,?)",
                entity.getDni(),
                entity.getNombre(),
                entity.getCargo().name(),
                entity.getEspecialidad() != null
                        ? entity.getEspecialidad().name()
                        : null,
                entity.getCredenciales()
        );

        entity.setId(id);
        return entity;
    }

    @Override
    protected Funcionario updateRow(Connection connection, Funcionario entity) throws SQLException {

        executeUpdate(
                connection,
                "UPDATE funcionario SET dni=?, nombre=?, cargo=?, especialidad=?, credenciales=? WHERE id=?",
                entity.getDni(),
                entity.getNombre(),
                entity.getCargo().name(),
                entity.getEspecialidad() != null
                        ? entity.getEspecialidad().name()
                        : null,
                entity.getCredenciales(),
                entity.getId()
        );

        return entity;
    }

    @Override
    protected Funcionario mapRow(ResultSet rs) throws SQLException {
        Cargo cargo = null;
        String cargoStr = rs.getString("cargo");
        if (cargoStr != null) {
            try {
                cargo = Cargo.valueOf(cargoStr);
            } catch (IllegalArgumentException e) {
                if (cargoStr.equalsIgnoreCase("GERENTE")) {
                    cargo = Cargo.SUPERVISOR;
                } else {
                    cargo = Cargo.INSPECTOR;
                }
            }
        }
        return Funcionario.builder()
                .id(rs.getLong("id"))
                .dni(rs.getString("dni"))
                .nombre(rs.getString("nombre"))
                .cargo(cargo)
                .especialidad(
                        rs.getString("especialidad") != null
                                ? Especialidad.valueOf(
                                rs.getString("especialidad")
                        )
                                : null
                ).credenciales(rs.getString("credenciales"))
                .build();
    }

    public boolean existeConDni(String dni, Long excludeId) {
        String sql = excludeId != null
                ? "SELECT COUNT(*) FROM funcionario WHERE dni = ? AND id != ?"
                : "SELECT COUNT(*) FROM funcionario WHERE dni = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dni);

            if (excludeId != null) {
                ps.setLong(2, excludeId);
            }

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al validar DNI: " + e.getMessage(), e);
        }
    }

    public Optional<Funcionario> findInspectorByEspecialidad(
            Especialidad especialidad
    ) {

        return executeQueryOne(
                "SELECT * FROM funcionario WHERE cargo = ? AND especialidad = ?",
                Cargo.INSPECTOR.name(),
                especialidad.name()
        );
    }
}