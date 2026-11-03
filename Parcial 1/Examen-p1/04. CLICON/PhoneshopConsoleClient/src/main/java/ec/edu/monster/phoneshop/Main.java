package ec.edu.monster.phoneshop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import ec.edu.monster.phoneshop.controller.AuthController;
import ec.edu.monster.phoneshop.controller.MainController;

public class Main {
    public static void main(String[] args) throws Exception {
        AuthController authController = new AuthController();
        authController.login();

        System.out.println("Bienvenido a BanQuito");

        MainController mainController = new MainController();
        mainController.init();
    }
}