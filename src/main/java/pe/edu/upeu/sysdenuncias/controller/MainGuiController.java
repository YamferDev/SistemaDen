package pe.edu.upeu.sysdenuncias.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.config.AppContext;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.model.Funcionario;

import java.io.IOException;

public class MainGuiController {

    @FXML private TabPane tabPaneFx;

    @FXML private Label lblSidebarUsuario;
    @FXML private Label lblSidebarCargo;
    @FXML private TitledPane paneMantenimiento;
    @FXML private TitledPane paneOperaciones;
    
    @FXML private Button btnSidebarInicio;
    @FXML private Button btnSidebarCiudadanos;
    @FXML private Button btnSidebarFuncionarios;
    @FXML private Button btnSidebarTiposDenuncia;
    @FXML private Button btnSidebarDenuncias;
    @FXML private Button btnSidebarSalir;

    @FXML
    public void sidebarAction(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();

        switch (id) {
            case "btnSidebarInicio" -> abrirTabConFXML("/view/dashboard.fxml", "Dashboard Interactivo");
            case "btnSidebarCiudadanos" -> abrirTabConFXML("/view/ciudadano.fxml", "Gestión de Ciudadanos");
            case "btnSidebarFuncionarios" -> {
                Funcionario f = SessionManager.getInstance().getFuncionarioLogueado();
                if (f.getCargo() != Cargo.ADMINISTRADOR) {
                    Toast.showToast(null, "Acceso denegado: Se requieren permisos de Administrador", 2000, 500, 300);
                    return;
                }
                abrirTabConFXML("/view/funcionario.fxml", "Gestión de Funcionarios");
            }

            case "btnSidebarTiposDenuncia" -> abrirTabConFXML("/view/tipodenuncia.fxml", "Tipos de Denuncia");
            case "btnSidebarDenuncias" -> abrirTabConFXML("/view/denuncia.fxml", "Gestión de Denuncias");
            case "btnSidebarSalir" -> cerrarSesion(event);
        }
    }

    private void cerrarSesion(ActionEvent event) {
        try {
            SessionManager.getInstance().setFuncionarioLogueado(null);
            
            AppContext ctx = AppContext.getInstance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            loader.setControllerFactory(ctx::getBean);
            Parent loginRoot = loader.load();
            
            Scene loginScene = new Scene(loginRoot);
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            loginScene.getStylesheets().add(cssPath);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login - Sistema de Denuncias");
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.setWidth(360);
            stage.setHeight(460);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirTabConFXML(String fxmlPath, String tituloTab) {
        try {
            AppContext ctx = AppContext.getInstance();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ctx::getBean);

            Parent root = loader.load();

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            for (Tab tab : tabPaneFx.getTabs()) {
                if (tab.getText().equals(tituloTab)) {
                    tabPaneFx.getSelectionModel().select(tab);
                    return;
                }
            }

            Tab newTab = new Tab(tituloTab, scrollPane);
            tabPaneFx.getTabs().add(newTab);
            tabPaneFx.getSelectionModel().select(newTab);
        } catch (Exception e) {
            System.out.println("ERROR AL CARGAR:");
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        Funcionario func = SessionManager.getInstance().getFuncionarioLogueado();

        if (func != null) {
            lblSidebarUsuario.setText(func.getNombre());
            lblSidebarCargo.setText(func.getCargo().name());

            configurarPrivilegios(func.getCargo());
        }

        abrirTabConFXML("/view/dashboard.fxml", "Dashboard Interactivo");
    }

    private void configurarPrivilegios(Cargo cargo) {
        btnSidebarCiudadanos.setManaged(true);
        btnSidebarCiudadanos.setVisible(true);
        btnSidebarFuncionarios.setManaged(true);
        btnSidebarFuncionarios.setVisible(true);
        btnSidebarTiposDenuncia.setManaged(true);
        btnSidebarTiposDenuncia.setVisible(true);
        paneMantenimiento.setManaged(true);
        paneMantenimiento.setVisible(true);

        switch (cargo) {
            case ADMINISTRADOR -> {
            }
            case SUPERVISOR -> {
                btnSidebarFuncionarios.setManaged(false);
                btnSidebarFuncionarios.setVisible(false);
                btnSidebarTiposDenuncia.setManaged(false);
                btnSidebarTiposDenuncia.setVisible(false);
            }
            case INSPECTOR -> {
                paneMantenimiento.setManaged(false);
                paneMantenimiento.setVisible(false);
            }
        }
    }
}
