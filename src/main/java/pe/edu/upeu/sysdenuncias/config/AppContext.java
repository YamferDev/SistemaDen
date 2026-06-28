package pe.edu.upeu.sysdenuncias.config;

import pe.edu.upeu.sysdenuncias.controller.*;
import pe.edu.upeu.sysdenuncias.repository.*;
import pe.edu.upeu.sysdenuncias.service.*;
import pe.edu.upeu.sysdenuncias.service.impl.*;

import java.util.HashMap;
import java.util.Map;


public class AppContext {

    private static AppContext instance;
    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    private void registrarRepositorios() {
        registrar(CiudadanoRepository.class, new CiudadanoRepository());
        registrar(FuncionarioRepository.class, new FuncionarioRepository());
        registrar(TipoDenunciaRepository.class, new TipoDenunciaRepository());
        registrar(DenunciaRepository.class, new DenunciaRepository());
        registrar(EvidenciaRepository.class, new EvidenciaRepositoryImp());    }

    private void registrarServicios() {
        registrar(INotificacionService.class, new NotificacionServiceImp());
        
        registrar(ICiudadanoService.class, new CiudadanoServiceImp(getBean(CiudadanoRepository.class)));
        registrar(IFuncionarioService.class, new FuncionarioServiceImp(getBean(FuncionarioRepository.class)));
        registrar(ITipoDenunciaService.class, new TipoDenunciaServiceImp(getBean(TipoDenunciaRepository.class)));
        registrar(IEvidenciaService.class, new EvidenciaServiceImp(getBean(EvidenciaRepository.class)));
        registrar(IDenunciaService.class, new DenunciaServiceImp(
                getBean(DenunciaRepository.class), 
                getBean(INotificacionService.class)
        ));
    }

    private void registrarControladores() {
        registrar(LoginController.class, new LoginController(getBean(IFuncionarioService.class)));
        registrar(MainGuiController.class, new MainGuiController());
        registrar(CiudadanoController.class, new CiudadanoController(getBean(ICiudadanoService.class)));
        registrar(FuncionarioController.class, new FuncionarioController(getBean(IFuncionarioService.class)));
        registrar(TipoDenunciaController.class, new TipoDenunciaController(getBean(ITipoDenunciaService.class)));
        registrar(DenunciaController.class, new DenunciaController(
                getBean(IDenunciaService.class),
                getBean(ICiudadanoService.class),
                getBean(ITipoDenunciaService.class),
                getBean(IFuncionarioService.class),
                getBean(IEvidenciaService.class)
        ));
        registrar(DashboardController.class, new DashboardController(getBean(IDenunciaService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Bean no encontrado: " + tipo.getName()));
        }
        return (T) bean;
    }
}