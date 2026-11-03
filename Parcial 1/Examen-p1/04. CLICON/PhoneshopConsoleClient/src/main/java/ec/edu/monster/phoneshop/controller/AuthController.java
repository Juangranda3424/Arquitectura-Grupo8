package ec.edu.monster.phoneshop.controller;

import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.common.CommandLine;
import ec.edu.monster.phoneshop.dto.AuthCredentialsDto;
import ec.edu.monster.phoneshop.dto.AuthResponseDto;
import ec.edu.monster.phoneshop.dto.ResponseStatus;
import ec.edu.monster.phoneshop.service.AuthService;

public class AuthController {
    private final ApplicationContext applicationContext = ApplicationContext.getInstance();
    private final CommandLine commandLine = CommandLine.getInstance();
    private final AuthService authService = new AuthService();

    
    private void mostrarBanner() {
        String banner =
            " ____   _                                 ____   _                \n" +
            "|  _ \\ | |__    ___   _ __    ___ / ___| | |__    ___   _ __  \n" +
            "| |_) || '_ \\  / _ \\ | '_ \\  / _ \\\\___ \\ | '_ \\  / _ \\ | '_ \\ \n" +
            "|  __/ | | | || (_) || | | ||  __/ ___) || | | || (_) || |_) |\n" +
            "|_|    |_| |_| \\___/ |_| |_| \\___||____/ |_| |_| \\___/ | .__/ \n" +
            "                                                       |_|    \n" +
            "                                                        v1.0.0";

        System.out.println(banner);
        System.out.println(":: PhoneShop System ::                      (v1.0)");
        System.out.println("-----------------------------------------------------------\n");
    }
    
    public void login() {
        
        mostrarBanner();
        
        commandLine.print("Iniciar Sesion \n");
        
        String username = commandLine.prompt("Usuario: ");
        String password = commandLine.password("Contraseña: ");
        String serverIp = commandLine.prompt("IP del servidor: ");

        applicationContext.setServerIp(serverIp);

        try {
            AuthResponseDto response = authService.login(AuthCredentialsDto.builder()
                    .username(username)
                    .password(password)
                    .build());

            if (response.getStatus() == ResponseStatus.SUCCESS) {
                applicationContext.setToken(response.getToken());
                applicationContext.setIdentificationNumber(response.getUser().getIdentificationNumber());
                commandLine.print("Autentificación exitosa");
            } else {
                commandLine.print(response.getMessage());
                login();
            }
        } catch (Exception e) {
            commandLine.print(e.getMessage());
            login();
        }
    }
}
