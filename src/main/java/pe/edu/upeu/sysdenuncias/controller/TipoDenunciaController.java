package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.model.TipoDenuncia;
import pe.edu.upeu.sysdenuncias.service.ITipoDenunciaService;

import java.util.LinkedHashMap;

public class TipoDenunciaController {

    private final ITipoDenunciaService tipoDenunciaService;
    private ObservableList<TipoDenuncia> listarTipos;

    @FXML private TextField txtNombre;
    @FXML private TableView<TipoDenuncia> tableView;
    @FXML private Button btnGuardar;

    private Long idTipoEdit = 0L;

    public TipoDenunciaController(ITipoDenunciaService tipoDenunciaService) {
        this.tipoDenunciaService = tipoDenunciaService;
    }

    @FXML
    public void initialize() {
        TableViewHelper<TipoDenuncia> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 50.0));
        columns.put("Nombre del Tipo", new ColumnInfo("nombre", 250.0));

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, this::editTipo, this::deleteTipo);
        listar();
    }

    private void listar() {
        listarTipos = FXCollections.observableArrayList(tipoDenunciaService.findAll());
        tableView.setItems(listarTipos);
    }

    @FXML
    public void guardar() {
        if (txtNombre.getText().isBlank()) {
            Toast.showToast(null, "El nombre del tipo de denuncia es obligatorio", 2000, 500, 300);
            return;
        }
        try {
            TipoDenuncia tipo = TipoDenuncia.builder()
                    .nombre(txtNombre.getText())
                    .build();

            if (idTipoEdit != 0L) {
                tipo.setId(idTipoEdit);
                tipoDenunciaService.update(idTipoEdit, tipo);
                Toast.showToast(null, "Actualizado correctamente", 2000, 500, 300);
            } else {
                tipoDenunciaService.save(tipo);
                Toast.showToast(null, "Guardado correctamente", 2000, 500, 300);
            }
            limpiar();
            listar();
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private void editTipo(TipoDenuncia t) {
        idTipoEdit = t.getId();
        txtNombre.setText(t.getNombre());
        btnGuardar.setText("Actualizar");
    }

    private void deleteTipo(TipoDenuncia t) {
        tipoDenunciaService.delete(t.getId());
        listar();
        Toast.showToast(null, "Eliminado correctamente", 2000, 500, 300);
    }

    @FXML
    public void limpiar() {
        txtNombre.clear();
        idTipoEdit = 0L;
        btnGuardar.setText("Guardar");
    }
}