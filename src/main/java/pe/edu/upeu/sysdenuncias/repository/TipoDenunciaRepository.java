package pe.edu.upeu.sysdenuncias.repository;

import pe.edu.upeu.sysdenuncias.enums.Especialidad;
import pe.edu.upeu.sysdenuncias.model.TipoDenuncia;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoDenunciaRepository extends AbstractJdbcRepository<TipoDenuncia, Long> {

    @Override
    protected String getTableName() {
        return "tipo_denuncia";
    }

    @Override
    protected String getPkColumn() {
        return "id";
    }

    @Override
    protected TipoDenuncia insert(Connection connection, TipoDenuncia entity) throws SQLException {

        long id = executeInsertGetKey(
                connection,
                "INSERT INTO tipo_denuncia(nombre, area_encargada) VALUES(?,?)",
                entity.getNombre(),
                entity.getAreaEncargada() != null
                        ? entity.getAreaEncargada().name()
                        : null
        );
        entity.setId(id);
        return entity;
    }

    @Override
    protected TipoDenuncia updateRow(Connection connection, TipoDenuncia entity) throws SQLException {

        executeUpdate(
                connection,
                "UPDATE tipo_denuncia SET nombre=?, area_encargada=? WHERE id=?",
                entity.getNombre(),
                entity.getAreaEncargada() != null
                        ? entity.getAreaEncargada().name()
                        : null,
                entity.getId()
        );

        return entity;
    }

    @Override
    protected TipoDenuncia mapRow(ResultSet rs) throws SQLException {
        return TipoDenuncia.builder()
                .id(rs.getLong("id"))
                .nombre(rs.getString("nombre"))
                .areaEncargada(
                        rs.getString("area_encargada") != null
                                ? Especialidad.valueOf(
                                rs.getString("area_encargada")
                        )
                                : null
                )                .build();
    }
}