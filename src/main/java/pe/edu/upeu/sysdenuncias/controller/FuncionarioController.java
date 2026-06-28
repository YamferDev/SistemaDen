package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import pe.edu.upeu.sysdenuncias.service.IFuncionarioService;

import java.util.LinkedHashMap;

public class FuncionarioController {

    private final IFuncionarioService funcionarioService;
    private ObservableList<Funcionario> listarFuncionarios;
    @FXML private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<Cargo> cbxCargo;
    @FXML
    private PasswordField txtCredenciales;
    @FXML
    private TableView<Funcionario> tableView;
    @FXML
    private Button btnGuardar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblContadorAdmin;
    @FXML
    private Label lblContadorSupervisor;
    @FXML
    private Label lblContadorInspector;
    @FXML
    private TextField txtCredencialesVisible;
    @FXML
    private Button btnVerPassword;
    @FXML
    private Button btnCopiarPassword;
    private boolean passwordVisible = false;

    private Long idFuncionarioEdit = 0L;

    public FuncionarioController(IFuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    private void listar() {
        if (listarFuncionarios == null) {
            listarFuncionarios = FXCollections.observableArrayList();
            tableView.setItems(listarFuncionarios);
        }
        listarFuncionarios.setAll(funcionarioService.findAll());
        actualizarContadores();
    }
    @FXML
    public void initialize() {
        cbxCargo.setItems(FXCollections.observableArrayList(Cargo.values()));

        btnVerPassword.setOnAction(e -> toggleVerPassword());
        btnCopiarPassword.setOnAction(e -> copiarPassword());
        TableViewHelper<Funcionario> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 50.0));
        columns.put("DNI", new ColumnInfo("dni", 100.0));
        columns.put("Nombre", new ColumnInfo("nombre", 150.0));
        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, this::editFuncionario, this::deleteFuncionario);
        TableColumn<Funcionario, Cargo> colCargo = new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cargo"));
        colCargo.setPrefWidth(150);
        colCargo.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(Cargo cargo, boolean empty) {
                super.updateItem(cargo, empty);
                if (empty || cargo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(cargo.name());
                    switch (cargo) {
                        case ADMINISTRADOR -> setStyle("-fx-text-fill: #1c7ed6; -fx-background-color: #e7f5ff; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4; -fx-padding: 3 10 3 10; -fx-max-width: 130;");
                        case SUPERVISOR    -> setStyle("-fx-text-fill: #495057; -fx-background-color: #f1f3f5; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4; -fx-padding: 3 10 3 10; -fx-max-width: 130;");
                        case INSPECTOR     -> setStyle("-fx-text-fill: #5f3dc4; -fx-background-color: #f3f0ff; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4; -fx-padding: 3 10 3 10; -fx-max-width: 130;");
                    }
                }
            }

        });
        tableView.getColumns().add(colCargo);

        txtCredenciales.textProperty().bindBidirectional(txtCredencialesVisible.textProperty());

        txtCredenciales.textProperty().addListener((obs, old, val) -> {
            boolean vacio = val == null || val.isBlank();
            btnVerPassword.setDisable(vacio);
            btnCopiarPassword.setDisable(vacio);
            btnVerPassword.setGraphic(new de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView(
                    de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.EYE));
            btnCopiarPassword.setGraphic(new de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView(
                    de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.COPY));
        });

        txtCredenciales.setDisable(true);
        txtCredencialesVisible.setDisable(true);
        txtDni.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d*")) {
                txtDni.setText(val.replaceAll("[^\\d]", ""));
            }
            if (val.length() > 8) {
                txtDni.setText(val.substring(0, 8));
            }
        });
        txtNombre.textProperty().addListener((obs, old, newVal) -> validarCamposParaCredenciales());
        cbxCargo.valueProperty().addListener((obs, old, newVal) -> validarCamposParaCredenciales());

        txtCredenciales.setOnMouseClicked(event -> {
            if (txtCredenciales.getText().isBlank() && !txtCredenciales.isDisable()) {
                String generada = generarPassword();
                if (!generada.isBlank()) txtCredenciales.setText(generada);
            }
        });


        listar();
        Funcionario logueado = SessionManager.getInstance().getFuncionarioLogueado();
        if (logueado != null && logueado.getCargo() != Cargo.ADMINISTRADOR) {
            btnGuardar.setDisable(true);
        }
        txtBuscar.textProperty().addListener((obs, old, val) -> {
            String filtro = val.toLowerCase();
            tableView.setItems(listarFuncionarios.filtered(f ->
                    f.getNombre().toLowerCase().contains(filtro) ||
                            f.getDni().toLowerCase().contains(filtro) ||
                            f.getCargo().name().toLowerCase().contains(filtro)
            ));
            if (val.isBlank()) tableView.setItems(listarFuncionarios);
        });
    }

        private void validarCamposParaCredenciales() {
            boolean camposLlenos = !txtNombre.getText().isBlank() && cbxCargo.getValue() != null;
            txtCredenciales.setDisable(!camposLlenos);
            txtCredencialesVisible.setDisable(!camposLlenos);
        }

    private void actualizarContadores() {
        long admins = listarFuncionarios.stream().filter(f -> f.getCargo() == Cargo.ADMINISTRADOR).count();
        long supervisores = listarFuncionarios.stream().filter(f -> f.getCargo() == Cargo.SUPERVISOR).count();
        long inspectores = listarFuncionarios.stream().filter(f -> f.getCargo() == Cargo.INSPECTOR).count();

        lblContadorAdmin.setText("Administradores: " + admins);
        lblContadorSupervisor.setText("Supervisores: " + supervisores);
        lblContadorInspector.setText("Inspectores: " + inspectores);
    }


    @FXML
    public void guardar() {
        if (txtDni.getText().isBlank() || !txtDni.getText().matches("\\d{8}")) {
            Toast.showToast(null, "DNI debe tener 8 dígitos", 2000, 500, 300);
            return;
        }
        if (funcionarioService.existeConDni(txtDni.getText(), idFuncionarioEdit)) {
            Toast.showToast(null, "Ya existe un funcionario con ese DNI", 2000, 500, 300);
            return;
        }
        if (txtNombre.getText().isBlank()) {
            Toast.showToast(null, "El nombre es obligatorio", 2000, 500, 300);
            return;
        }
        if (cbxCargo.getValue() == null) {
            Toast.showToast(null, "Seleccione un cargo", 2000, 500, 300);
            return;
        }
        if (txtCredenciales.getText().isBlank()) {
            Toast.showToast(null, "Las credenciales son obligatorias", 2000, 500, 300);
            return;
        }

        try {
            Funcionario funcionario = Funcionario.builder()
                    .dni(txtDni.getText())
                    .nombre(txtNombre.getText())
                    .cargo(cbxCargo.getValue())
                    .credenciales(txtCredenciales.getText())
                    .build();

            if (idFuncionarioEdit != 0L) {
                funcionario.setId(idFuncionarioEdit);
                funcionarioService.update(idFuncionarioEdit, funcionario);
                Toast.showToast(null, "Actualizado correctamente", 2000, 500, 300);
            } else {
                funcionarioService.save(funcionario);
                Toast.showToast(null, "Guardado correctamente", 2000, 500, 300);
            }
            limpiar();
            listar();
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private void editFuncionario(Funcionario f) {
        idFuncionarioEdit = f.getId();
        txtDni.setText(f.getDni());
        txtNombre.setText(f.getNombre());
        cbxCargo.setValue(f.getCargo());
        txtCredenciales.setText(f.getCredenciales());
        txtCredenciales.setDisable(false);
        txtCredencialesVisible.setDisable(false);
        btnGuardar.setText("Actualizar");
    }

    private void deleteFuncionario(Funcionario f) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al funcionario " + f.getNombre() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                funcionarioService.delete(f.getId());
                listar();
                Toast.showToast(null, "Eliminado correctamente", 2000, 500, 300);
            }
        });
    }
    @FXML
    public void limpiar() {
        txtDni.clear();
        txtNombre.clear();
        cbxCargo.setValue(null);
        txtCredenciales.clear();
        txtCredencialesVisible.clear();
        txtBuscar.clear();
        txtCredenciales.setDisable(true);
        txtCredencialesVisible.setDisable(true);
        txtCredenciales.setVisible(true);
        txtCredenciales.setManaged(true);
        txtCredencialesVisible.setVisible(false);
        txtCredencialesVisible.setManaged(false);
        passwordVisible = false;
        btnVerPassword.setText("ver");

        idFuncionarioEdit = 0L;
        btnGuardar.setText("Guardar");
    }

    private String generarPassword() {
        String nombre = txtNombre.getText().trim();
        Cargo cargo = cbxCargo.getValue();

        String[] partes = nombre.split(" ");
        StringBuilder base = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isBlank()) base.append(parte.charAt(0));
        }

        String cargoStr = cargo.name().substring(0, Math.min(3, cargo.name().length()));
        int numero = new java.util.Random().nextInt(900) + 100;
        String especial = "@#$%&*!";
        char esp = especial.charAt(new java.util.Random().nextInt(especial.length()));

        return base.toString().toUpperCase() + cargoStr.toLowerCase() + numero + esp;
    }
    @FXML
    public void toggleVerPassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            txtCredencialesVisible.setText(txtCredenciales.getText());
            txtCredenciales.setVisible(false);
            txtCredenciales.setManaged(false);
            txtCredencialesVisible.setVisible(true);
            txtCredencialesVisible.setManaged(true);
            btnVerPassword.setText("Ocultar"); // cambio aquí
        } else {
            txtCredenciales.setText(txtCredencialesVisible.getText());
            txtCredencialesVisible.setVisible(false);
            txtCredencialesVisible.setManaged(false);
            txtCredenciales.setVisible(true);
            txtCredenciales.setManaged(true);
            btnVerPassword.setText("Ver"); // cambio aquí
        }
    }
    @FXML
    public void copiarPassword() {
        String pass = passwordVisible ? txtCredencialesVisible.getText() : txtCredenciales.getText();
        if (pass.isBlank()) {
            Toast.showToast(null, "No hay contraseña para copiar", 2000, 500, 300);
            return;
        }
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(pass);
        clipboard.setContent(content);
        Toast.showToast(null, "Contraseña copiada", 2000, 500, 300);
    }
}