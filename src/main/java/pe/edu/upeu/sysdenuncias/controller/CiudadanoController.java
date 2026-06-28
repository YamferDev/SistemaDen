package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.enums.Genero;
import pe.edu.upeu.sysdenuncias.model.Ciudadano;
import pe.edu.upeu.sysdenuncias.service.ICiudadanoService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import java.util.LinkedHashMap;

public class CiudadanoController {

    private final ICiudadanoService ciudadanoService;
    private ObservableList<Ciudadano> listarCiudadanos;
    private FilteredList<Ciudadano> filteredData;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<Genero> cbxGenero;
    
    @FXML private TableView<Ciudadano> tableView;
    @FXML private Button btnGuardar;

    private Long idCiudadanoEdit = 0L;

    public CiudadanoController(ICiudadanoService ciudadanoService) {
        this.ciudadanoService = ciudadanoService;
    }

    @FXML
    public void initialize() {
        cbxGenero.setItems(FXCollections.observableArrayList(Genero.values()));

        listarCiudadanos = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(listarCiudadanos, p -> true);

        SortedList<Ciudadano> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        TableViewHelper<Ciudadano> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 50.0));
        columns.put("Nombre", new ColumnInfo("nombre", 150.0));
        columns.put("DNI", new ColumnInfo("dni", 80.0));
        columns.put("Teléfono", new ColumnInfo("telefono", 100.0));
        columns.put("Correo", new ColumnInfo("correo", 180.0));
        columns.put("Dirección", new ColumnInfo("direccion", 150.0));
        columns.put("Género", new ColumnInfo("genero", 80.0));
        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, this::editCiudadano, this::deleteCiudadano);

        setupNumericField(txtDni, 8);
        setupNumericField(txtTelefono, 9);

        listar();
        txtBuscar.textProperty().addListener((obs, old, val) -> {
            String filtro = val.toLowerCase().trim();
            tableView.setItems(listarCiudadanos.filtered(c ->
                    c.getNombre().toLowerCase().contains(filtro) ||
                            c.getDni().contains(filtro) ||
                            (c.getCorreo() != null && c.getCorreo().toLowerCase().contains(filtro))
            ));
            if (val.isBlank()) tableView.setItems(listarCiudadanos);
        });
    }

    private void setupNumericField(TextField textField, int maxLength) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                textField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (textField.getText().length() > maxLength) {
                textField.setText(textField.getText().substring(0, maxLength));
            }
        });
    }
    private void listar() {
        if (listarCiudadanos == null) {
            listarCiudadanos = FXCollections.observableArrayList();
        }
        listarCiudadanos.setAll(ciudadanoService.findAll());
        tableView.setItems(listarCiudadanos);
    }

    @FXML
    public void guardar() {
        if (ciudadanoService.existeConDni(txtDni.getText(), idCiudadanoEdit)) {
            Toast.showToast(null, "Error: Ya existe un ciudadano con ese DNI", 3000, 500, 300);
            return;
        }

        try {
            Ciudadano ciudadano = Ciudadano.builder()
                    .nombre(txtNombre.getText())
                    .dni(txtDni.getText())
                    .telefono(txtTelefono.getText())
                    .correo(txtCorreo.getText())
                    .direccion(txtDireccion.getText())
                    .genero(cbxGenero.getValue())
                    .build();

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Ciudadano>> violations = validator.validate(ciudadano);

            if (!violations.isEmpty()) {
                String msg = violations.iterator().next().getMessage();
                Toast.showToast(null, "Error: " + msg, 3000, 500, 300);
                return;
            }

            if (idCiudadanoEdit != 0L) {
                ciudadano.setId(idCiudadanoEdit);
                ciudadanoService.update(idCiudadanoEdit, ciudadano);
                Toast.showToast(null, "Actualizado correctamente", 2000, 500, 300);
            } else {
                ciudadanoService.save(ciudadano);
                Toast.showToast(null, "Guardado correctamente", 2000, 500, 300);
            }
            limpiar();
            listar();
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
            Toast.showToast(null, "Error inesperado al guardar", 2000, 500, 300);
        }
    }
    private void editCiudadano(Ciudadano c) {
        idCiudadanoEdit = c.getId();
        txtNombre.setText(c.getNombre());
        txtDni.setText(c.getDni());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo());
        txtDireccion.setText(c.getDireccion());
        cbxGenero.setValue(c.getGenero());
        btnGuardar.setText("Actualizar");
    }

    private void deleteCiudadano(Ciudadano c) {
        ciudadanoService.delete(c.getId());
        listar();
        Toast.showToast(null, "Eliminado correctamente", 2000, 500, 300);
    }

    @FXML
    public void limpiar() {
        txtNombre.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();
        txtBuscar.clear();
        cbxGenero.setValue(null);
        idCiudadanoEdit = 0L;
        btnGuardar.setText("Guardar");
    }
}