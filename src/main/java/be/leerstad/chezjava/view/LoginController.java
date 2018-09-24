package be.leerstad.chezjava.view;

import be.leerstad.chezjava.MainApp;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

/**
 * Controls the login screen
 */
public class LoginController {
    private static Logger logger = Logger.getLogger(LoginController.class.getName());
    @FXML
    private TextField user;
    @FXML
    private PasswordField password;
    @FXML
    private Label loginMessage;
    private BooleanProperty loggedIn = new SimpleBooleanProperty();
    private MainApp mainApp;

    public BooleanProperty loggedInProperty() {
        return loggedIn;
    }

    private void setLoggedIn(boolean loggedIn) {
        loggedInProperty().set(loggedIn);
    }

    public void initialize() {
        loginMessage.setText("");
    }

    /**
     * Handle Logon button
     */
    @FXML
    private void handleLogOn() {
        // clear error
        loginMessage.setText("");
        if (authorize()) {
            logger.debug("Login: " + user.getText());
            setLoggedIn(true);
            mainApp.showCafeDesktop();

        } else
            loginMessage.setText("login failed");
        logger.debug("login failed: " + user.getText());
    }

    /**
     * Check authorization credentials.
     * <p>
     * If accepted, return true sets current user to login
     */
    private boolean authorize() {
        boolean result = false;
        // Split user field in firstname and password
        String[] userSplit = user.getText().trim().split("\\s+");
        // check length array to 2
        if (userSplit.length == 2)
            return mainApp.getCafe().logOn(userSplit[0], userSplit[1], password.getText());
        loginMessage.setText("Type firstname lastname with a space");
        return result;
    }

    public void setMainApp(MainApp mainApp) {

        this.mainApp = mainApp;

    }
}


