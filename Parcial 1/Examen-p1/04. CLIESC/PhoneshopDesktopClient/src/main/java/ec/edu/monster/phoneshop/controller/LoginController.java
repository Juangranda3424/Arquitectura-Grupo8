package ec.edu.monster.phoneshop.controller;

import ec.edu.monster.phoneshop.App;
import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.dto.AuthCredentialsDto;
import ec.edu.monster.phoneshop.dto.AuthResponseDto;
import ec.edu.monster.phoneshop.dto.ResponseStatus;
import ec.edu.monster.phoneshop.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 *
 * @author Andres
 */
public class LoginController {
    private final AuthService authService = new AuthService();
    private final ApplicationContext applicationContext = ApplicationContext.getInstance();

    private enum MessageStatus {
        ERROR,
        SUCCESS
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField serverIpField;

    @FXML
    private Text loginMessageLabel;

    @FXML
    private BorderPane loginMessageWrapper;

    public LoginController() {}

    @FXML
    public void submit() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String serverIp = serverIpField.getText();
        String port;

        if (serverIp.contains(":")) {
            String[] parts = serverIp.split(":");
            serverIp = parts[0];
            port = parts[1];
        } else {
            port = "9001";
        }

        loginMessageWrapper.setVisible(false);
        usernameField.getStyleClass().remove("error");
        passwordField.getStyleClass().remove("error");

        if (username.isEmpty()) {
            showAuthMessage(MessageStatus.ERROR, "El nombre de usuario es requerido");
            usernameField.getStyleClass().add("error");
            return;
        }

        if (password.isEmpty()) {
            showAuthMessage(MessageStatus.ERROR, "El password es requerido");
            passwordField.getStyleClass().add("error");
            return;
        }

        applicationContext.setServerIp(serverIp);
        applicationContext.setServerPort(port);

        try {
            AuthResponseDto response = authService.login(AuthCredentialsDto.builder()
                    .username(username)
                    .password(password)
                    .build());

            if (response.getStatus() == ResponseStatus.SUCCESS) {
                System.out.println("Token: " + response.getToken());
                System.out.println("Identification Number: " + response.getUser().getIdentificationNumber());

                applicationContext.setToken(response.getToken());
                applicationContext.setIdentificationNumber(response.getUser().getIdentificationNumber());
                showAuthMessage(MessageStatus.SUCCESS, "Se ha ingresado correctamente!");
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        Platform.runLater(() -> App.setRoot("main"));
                    } catch (Exception ex) {}
                }).start();
            } else {
                showAuthMessage(MessageStatus.ERROR, response.getMessage());
            }
        } catch (Exception e) {
            showAuthMessage(MessageStatus.ERROR, e.getMessage());
        }
    }

    private void showAuthMessage(MessageStatus status, String message) {
        loginMessageWrapper.setVisible(true);
        loginMessageWrapper.getStyleClass().remove(status == MessageStatus.ERROR ? "success" : "error");
        loginMessageWrapper.getStyleClass().add(status == MessageStatus.ERROR ? "error" : "success");
        loginMessageLabel.setText(message);
    }
}
