package pe.edu.upeu.sysdenuncias.dto;

import lombok.Data;
import pe.edu.upeu.sysdenuncias.model.Funcionario;

@Data
public class SessionManager {
    private static SessionManager instance;
    private Funcionario funcionarioLogueado;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
}