
package main;

import view.LoginForm;
import util.TemaCrisol;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Aplica el tema Crisol desde el primer milisegundo
            TemaCrisol.aplicarTemaGlobal();
            
            // Abre directamente el Login
            new LoginForm();
        });
    }
}