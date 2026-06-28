package pe.edu.upeu.sysdenuncias.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;
import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.service.IDenunciaService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    private final IDenunciaService denunciaService;

    @FXML private Label lblNuevas;
    @FXML private Label lblProceso;
    @FXML private TableView<Denuncia> tblRecientes;
    @FXML private TableColumn<Denuncia, Long> colCodigo;
    @FXML private TableColumn<Denuncia, String> colCiudadano;
    @FXML private TableColumn<Denuncia, String> colMotivo;
    @FXML private TableColumn<Denuncia, String> colFecha;
    @FXML private TableColumn<Denuncia, String> colEstado;

    public DashboardController(IDenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colCodigo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colCiudadano.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCiudadano() != null ? cellData.getValue().getCiudadano().getNombre() : "N/A"
        ));
        colMotivo.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoDenuncia() != null ? cellData.getValue().getTipoDenuncia().getNombre() : "N/A"
        ));
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFecha() != null 
                        ? cellData.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                        : "N/A"
        ));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getEstado() != null ? cellData.getValue().getEstado().name() : "PENDIENTE"
        ));

        cargarDatos();
    }

    @FXML
    public void cargarDatos() {
        List<Denuncia> denuncias = denunciaService.findAll();

        // Calcular métricas
        long nuevas = denuncias.stream()
                .filter(d -> d.getEstado() == EstadoDenuncia.PENDIENTE)
                .count();
        long proceso = denuncias.stream()
                .filter(d -> d.getEstado() == EstadoDenuncia.EN_PROCESO)
                .count();

        lblNuevas.setText(String.valueOf(nuevas));
        lblProceso.setText(String.valueOf(proceso));

        // Cargar tabla (todas ordenadas de más recientes a antiguas si aplica, o la lista completa)
        ObservableList<Denuncia> obsList = FXCollections.observableArrayList(denuncias);
        tblRecientes.setItems(obsList);
    }
}
