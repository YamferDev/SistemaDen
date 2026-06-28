package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;
import pe.edu.upeu.sysdenuncias.model.*;
import pe.edu.upeu.sysdenuncias.service.*;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.time.LocalDate;
import java.util.LinkedHashMap;

public class DenunciaController {

    private final IDenunciaService denunciaService;
    private final ICiudadanoService ciudadanoService;
    private final ITipoDenunciaService tipoDenunciaService;
    private final IEvidenciaService evidenciaService;

    private ObservableList<Denuncia> listarDenuncias;

    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtUbicacion;
    @FXML private ComboBox<Ciudadano> cbxCiudadano;
    @FXML private ComboBox<TipoDenuncia> cbxTipoDenuncia;
    @FXML private ComboBox<EstadoDenuncia> cbxEstado;
    @FXML private TextArea txtObservacion;
    @FXML private TableView<Denuncia> tableView;
    @FXML private Button btnGuardar;
    @FXML private TextField txtBuscar;

    // FIX: usar null en vez de 0L para evitar problemas con autoboxing
    private Long idDenunciaEdit = null;

    public DenunciaController(IDenunciaService denunciaService,
                              ICiudadanoService ciudadanoService,
                              ITipoDenunciaService tipoDenunciaService,
                              IFuncionarioService funcionarioService,
                              IEvidenciaService evidenciaService) {
        this.denunciaService = denunciaService;
        this.ciudadanoService = ciudadanoService;
        this.tipoDenunciaService = tipoDenunciaService;
        this.evidenciaService = evidenciaService;
    }

    private void listar() {
        listarDenuncias = FXCollections.observableArrayList(denunciaService.findAll());
        tableView.setItems(listarDenuncias);
    }

    @FXML
    public void initialize() {
        cbxEstado.setItems(FXCollections.observableArrayList(EstadoDenuncia.values()));
        cbxCiudadano.setItems(FXCollections.observableArrayList(ciudadanoService.findAll()));
        cbxTipoDenuncia.setItems(FXCollections.observableArrayList(tipoDenunciaService.findAll()));

        cbxCiudadano.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Ciudadano item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? "" : item.getNombre() + " (" + item.getDni() + ")");
            }
        });
        cbxCiudadano.setButtonCell(cbxCiudadano.getCellFactory().call(null));

        cbxTipoDenuncia.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TipoDenuncia item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? "" : item.getNombre());
            }
        });
        cbxTipoDenuncia.setButtonCell(cbxTipoDenuncia.getCellFactory().call(null));

        TableViewHelper<Denuncia> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 40.0));
        columns.put("Fecha", new ColumnInfo("fecha", 120.0));
        columns.put("Ciudadano", new ColumnInfo("ciudadano.nombre", 120.0));
        columns.put("Tipo", new ColumnInfo("tipoDenuncia.nombre", 100.0));
        columns.put("Estado", new ColumnInfo("estado", 80.0));
        columns.put("Funcionario", new ColumnInfo("funcionario.nombre", 120.0));

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, this::editDenuncia, this::deleteDenuncia);
        addReportColumn();
        listar();

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal.toLowerCase();
            tableView.setItems(listarDenuncias.filtered(d ->
                    (d.getDescripcion() != null && d.getDescripcion().toLowerCase().contains(filtro)) ||
                            (d.getUbicacion() != null && d.getUbicacion().toLowerCase().contains(filtro)) ||
                            (d.getCiudadano() != null && d.getCiudadano().getNombre().toLowerCase().contains(filtro))
            ));
        });
    }

    private void addReportColumn() {
        TableColumn<Denuncia, Void> actionColumn = new TableColumn<>("PDF");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final MenuButton btnConstancia = new MenuButton("Constancia");

            private final MenuItem itemImprimir = new MenuItem("🖨 Imprimir");
            private final MenuItem itemCorreo = new MenuItem("📧 Correo");
            private final MenuItem itemWhatsapp = new MenuItem("💬 WhatsApp");
            {
                btnConstancia.setStyle(
                        "-fx-background-color: #1565C0; " +
                                "-fx-text-fill: white;"
                );

                btnConstancia.getItems().addAll(
                        itemImprimir,
                        itemCorreo,
                        itemWhatsapp
                );

                itemImprimir.setOnAction(event -> {
                    Denuncia data = getTableView().getItems().get(getIndex());

                    try {
                        denunciaService.generarConstanciaPdf(data.getId());
                    } catch (Exception e) {
                        Toast.showToast(
                                null,
                                "Error al generar constancia: " + e.getMessage(),
                                3000,
                                500,
                                300
                        );
                    }
                });

                itemCorreo.setOnAction(event -> {
                    Denuncia data = getTableView().getItems().get(getIndex());

                    try {
                        denunciaService.enviarConstanciaCorreo(data.getId());

                        Toast.showToast(
                                null,
                                "Correo enviado",
                                3000,
                                500,
                                300
                        );

                    } catch (Exception e) {
                        Toast.showToast(
                                null,
                                "Error: " + e.getMessage(),
                                3000,
                                500,
                                300
                        );
                    }
                });

                itemWhatsapp.setOnAction(event -> {
                    Denuncia data = getTableView().getItems().get(getIndex());

                    Toast.showToast(
                            null,
                            "Envío por WhatsApp en implementación",
                            3000,
                            500,
                            300
                    );
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btnConstancia));            }
        });
        actionColumn.setPrefWidth(140);
        tableView.getColumns().add(actionColumn);
    }

    @FXML
    public void guardar() {
        try {
            // FIX: Validar ANTES de construir el objeto
            if (cbxCiudadano.getValue() == null) {
                Toast.showToast(null, "Seleccione un ciudadano", 2000, 500, 300);
                return;
            }
            if (cbxTipoDenuncia.getValue() == null) {
                Toast.showToast(null, "Seleccione el tipo de denuncia", 2000, 500, 300);
                return;
            }
            if (txtUbicacion.getText().isBlank()) {
                Toast.showToast(null, "La ubicación es obligatoria", 2000, 500, 300);
                return;
            }
            if (txtDescripcion.getText().isBlank()) {
                Toast.showToast(null, "La descripción es obligatoria", 2000, 500, 300);
                return;
            }

            Funcionario fLogueado = SessionManager.getInstance().getFuncionarioLogueado();

            Denuncia denuncia = Denuncia.builder()
                    .descripcion(txtDescripcion.getText())
                    .ubicacion(txtUbicacion.getText())
                    .observacion(txtObservacion.getText())
                    .estado(cbxEstado.getValue() != null ? cbxEstado.getValue() : EstadoDenuncia.PENDIENTE)
                    .ciudadano(cbxCiudadano.getValue())
                    .tipoDenuncia(cbxTipoDenuncia.getValue())
                    .funcionario(fLogueado)
                    .fecha(LocalDate.now())
                    .build();

            if (idDenunciaEdit != null) {
                denuncia.setId(idDenunciaEdit);
                denunciaService.update(idDenunciaEdit, denuncia);
                Toast.showToast(null, "Actualizado correctamente", 2000, 500, 300);
            } else {
                denunciaService.save(denuncia);
                Toast.showToast(null, "Guardado correctamente", 2000, 500, 300);
            }

            limpiar();
            listar();

        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
            Toast.showToast(null, "Error al guardar: " + e.getMessage(), 3000, 500, 300);
        }
    }

    private void editDenuncia(Denuncia d) {
        idDenunciaEdit = d.getId();
        txtDescripcion.setText(d.getDescripcion());
        txtUbicacion.setText(d.getUbicacion());
        txtObservacion.setText(d.getObservacion() != null ? d.getObservacion() : "");
        cbxEstado.setValue(d.getEstado());
        cbxCiudadano.getItems().stream()
                .filter(c -> c.getId().equals(d.getCiudadano().getId()))
                .findFirst()
                .ifPresent(cbxCiudadano::setValue);
        cbxTipoDenuncia.getItems().stream()
                .filter(t -> t.getId().equals(d.getTipoDenuncia().getId()))
                .findFirst()
                .ifPresent(cbxTipoDenuncia::setValue);
        btnGuardar.setText("Actualizar");
    }

    private void deleteDenuncia(Denuncia d) {
        denunciaService.delete(d.getId());
        listar();
        Toast.showToast(null, "Eliminado correctamente", 2000, 500, 300);
    }

    @FXML
    public void limpiar() {
        txtDescripcion.clear();
        txtUbicacion.clear();
        txtObservacion.clear();
        cbxCiudadano.setValue(null);
        cbxTipoDenuncia.setValue(null);
        cbxEstado.setValue(EstadoDenuncia.PENDIENTE);
        idDenunciaEdit = null; // FIX: null en vez de 0L
        btnGuardar.setText("Guardar");
    }

    @FXML
    public void subirEvidencia() {
        if (idDenunciaEdit == null) {
            Toast.showToast(null, "Primero selecciona una denuncia para editar", 2000, 500, 300);
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Evidencia");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes/PDF", "*.png", "*.jpg", "*.jpeg", "*.pdf")
        );
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            Evidencia ev = Evidencia.builder()
                    .denuncia(Denuncia.builder().id(idDenunciaEdit).build())
                    .build();
            try {
                evidenciaService.guardarConArchivo(ev, archivo);
                Toast.showToast(null, "Evidencia subida correctamente", 2000, 500, 300);
            } catch (RuntimeException e) {
                Toast.showToast(null, e.getMessage(), 2000, 500, 300);
            }
        }
    }
}

