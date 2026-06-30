package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.enums.Especialidad;
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
    private Label lblEspecialidad;

    @FXML
    private ComboBox<Especialidad> cbxEspecialidad;
    @FXML
    private TextField txtCredencialesVisible;
    @FXML
    private Button btnVerPassword;
    @FXML
    private Button btnCopiarPassword;
    private boolean passwordVisible = false;

    private Long idFuncionarioEdit = null;

    public FuncionarioController(IFuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    private void listar() {
        if (listarFuncionarios == null) {
            listarFuncionarios = FXCollections.observableArrayList();
            tableView.setItems(listarFuncionarios);
        }
        listarFuncionarios.setAll(funcionarioService.findAll());
        listarFuncionarios.forEach(f -> System.out.println(" - " + f.getNombre() + " | " + f.getCargo()));
        actualizarContadores();
    }
    @FXML
    public void initialize() {

        cbxCargo.setItems(FXCollections.observableArrayList(Cargo.values()));
        cbxEspecialidad.setItems(
                FXCollections.observableArrayList(
                        Especialidad.values()
                )
        );
        txtDni.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.isEmpty()) return;
            if (!val.matches("\\d*")) {
                txtDni.setText(val.replaceAll("[^\\d]", ""));
            } else if (val.length() > 8) {
                txtDni.setText(val.substring(0, 8));
            }
        });
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
        TableColumn<Funcionario, Especialidad> colEspecialidad = new TableColumn<>("Especialidad");
        colEspecialidad.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("especialidad"));
        colEspecialidad.setPrefWidth(150);
        colEspecialidad.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Especialidad especialidad, boolean empty) {
                super.updateItem(especialidad, empty);
                if (empty || especialidad == null) {
                    setText("-");
                    setStyle("");
                } else {
                    setText(especialidad.name());
                    setStyle("-fx-text-fill: #2f9e44; -fx-background-color: #ebfbee; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4; -fx-padding: 3 10 3 10;");
                }
            }
        });
        tableView.getColumns().add(colEspecialidad);

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

        txtNombre.textProperty().addListener((obs, old, newVal) -> validarCamposParaCredenciales());
        cbxCargo.valueProperty().addListener((obs, old, newVal) -> validarCamposParaCredenciales());
        cbxCargo.valueProperty().addListener((obs, old, cargo) -> {

            boolean inspector =
                    cargo == Cargo.INSPECTOR;

            lblEspecialidad.setVisible(inspector);
            lblEspecialidad.setManaged(inspector);

            cbxEspecialidad.setVisible(inspector);
            cbxEspecialidad.setManaged(inspector);

            if (!inspector) {
                cbxEspecialidad.setValue(null);            }
        });

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
        FilteredList<Funcionario> filteredList = new FilteredList<>(listarFuncionarios, p -> true);
        tableView.setItems(filteredList);
        txtBuscar.textProperty().addListener((obs, old, val) -> {
            filteredList.setPredicate(f -> {
                if (val == null || val.isBlank()) return true;
                String filtro = val.toLowerCase();
                return f.getNombre().toLowerCase().contains(filtro) ||
                        f.getDni().toLowerCase().contains(filtro) ||
                        f.getCargo().name().toLowerCase().contains(filtro);
            });
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
        if (!txtDni.getText().matches("\\d{8}")) {
            Toast.showToast(null,
                    "DNI debe tener exactamente 8 dígitos",
                    2000, 500, 300);
            return;
        }
        if (funcionarioService.existeConDni(txtDni.getText(), idFuncionarioEdit)) {
            System.out.println("DNI duplicado detectado. DNI: " + txtDni.getText()
                    + " | idExcluir: " + idFuncionarioEdit); // <-- agrega esto
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
            Funcionario funcionario =
                    Funcionario.builder()
                            .dni(txtDni.getText())
                            .nombre(txtNombre.getText())
                            .cargo(cbxCargo.getValue())
                            .especialidad(
                                    cbxCargo.getValue() == Cargo.INSPECTOR
                                            ? cbxEspecialidad.getValue()
                                            : null
                            )
                            .credenciales(txtCredenciales.getText())
                            .build();

            if (idFuncionarioEdit != null) {
                funcionario.setId(idFuncionarioEdit);
                funcionarioService.update(idFuncionarioEdit, funcionario);
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
        cbxEspecialidad.setValue(null);
        lblEspecialidad.setVisible(false);
        lblEspecialidad.setManaged(false);

        cbxEspecialidad.setVisible(false);
        cbxEspecialidad.setManaged(false);
        idFuncionarioEdit = null;
        btnGuardar.setText("Guardar");
        txtDni.setDisable(false);
        txtDni.setEditable(true);
        txtNombre.setDisable(false);
        txtNombre.setEditable(true);
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
        cbxEspecialidad.setValue(
                f.getEspecialidad()
        );
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