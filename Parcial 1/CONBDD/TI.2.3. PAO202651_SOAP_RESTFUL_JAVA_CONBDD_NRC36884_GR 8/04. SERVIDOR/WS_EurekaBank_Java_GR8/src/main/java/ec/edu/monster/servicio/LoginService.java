/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.servicio;

/**
 *
 * @author crist
 */
public class LoginService {
        public boolean login(String username, String password) {
        if ((username.equals("MONSTER") && password.equals("MONSTER9"))
                || (username.equals("admin") && password.equals("admin"))) {
            return true;
        }
        return false;
    }
}
