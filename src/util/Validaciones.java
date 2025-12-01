// util/Validaciones.java  (crea esta clase si no existe)
package util;

import javax.swing.JOptionPane;

public class Validaciones {
    
    public static boolean esPasswordFuerte(String password) {
        if (password == null || password.length() < 8) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener mínimo 8 caracteres");
            return false;
        }
        boolean tieneMayus = false;
        boolean tieneNumero = false;
        boolean tieneEspecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) tieneMayus = true;
            if (Character.isDigit(c)) tieneNumero = true;
            if ("@#$%^&+=!.".indexOf(c) != -1) tieneEspecial = true;
        }
        
        if (!tieneMayus) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 1 MAYÚSCULA");
            return false;
        }
        if (!tieneNumero) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 1 NÚMERO");
            return false;
        }
        if (!tieneEspecial) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 1 carácter especial (@#$%^&+=!.)");
            return false;
        }
        return true;
    }
}