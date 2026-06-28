package pe.edu.upeu.sysdenuncias.repository;

import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.model.Evidencia;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class EvidenciaRepositoryImp extends AbstractJdbcRepository<Evidencia, Long> implements EvidenciaRepository {

    @Override
    protected String getTableName() {
        return "evidencia";
    }

    @Override
    protected String getPkColumn() {
        return "id";
    }

    @Override
    protected Evidencia insert(Connection connection, Evidencia entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO evidencia(denuncia_id, nombre_archivo, ruta_archivo, tipo_archivo, fecha_subida) VALUES(?,?,?,?,?)",
                entity.getDenuncia().getId(),
                entity.getNombreArchivo(),
                entity.getRutaArchivo(),
                entity.getTipoArchivo(),
                entity.getFechaSubida() != null ? Timestamp.valueOf(entity.getFechaSubida()) : null
        );
        entity.setId(id);
        return entity;
    }

    @Override
    protected Evidencia updateRow(Connection connection, Evidencia entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE evidencia SET denuncia_id=?, nombre_archivo=?, ruta_archivo=?, tipo_archivo=?, fecha_subida=? WHERE id=?",
                entity.getDenuncia().getId(),
                entity.getNombreArchivo(),
                entity.getRutaArchivo(),
                entity.getTipoArchivo(),
                entity.getFechaSubida() != null ? Timestamp.valueOf(entity.getFechaSubida()) : null,
                entity.getId()
        );
        return entity;
    }

    @Override
    protected Evidencia mapRow(ResultSet rs) throws SQLException {
        Denuncia denuncia = Denuncia.builder()
                .id(rs.getLong("denuncia_id"))
                .build();

        return Evidencia.builder()
                .id(rs.getLong("id"))
                .denuncia(denuncia)
                .nombreArchivo(rs.getString("nombre_archivo"))
                .rutaArchivo(rs.getString("ruta_archivo"))
                .tipoArchivo(rs.getString("tipo_archivo"))
                .fechaSubida(rs.getTimestamp("fecha_subida") != null ? rs.getTimestamp("fecha_subida").toLocalDateTime() : null)
                .build();
    }

    @Override
    public List<Evidencia> findByDenuncia(Long denunciaId) {
        String sql = "SELECT * FROM evidencia WHERE denuncia_id = ?";
        return executeQuery(sql, denunciaId);
    }
}
