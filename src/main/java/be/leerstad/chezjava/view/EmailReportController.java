package be.leerstad.chezjava.view;

import be.leerstad.chezjava.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controls the email report screen
 */
public class EmailReportController {
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final String workingDir = System.getProperty("user.dir") + "/src/main/resources/";
    private final String propertiesName = "mail.properties";
    private Logger logger = Logger.getLogger(EmailReportController.class.getName());
    private Stage emailStage;
    private Properties props = new Properties();
    @FXML
    private TextField emailAddress;
    @FXML
    private CheckBox dayReport;
    @FXML
    private CheckBox topWaiters;
    private MainApp mainApp;

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public void initialize() {
        props.clear();
        loadEmailAddress();
    }

    /**
     * Handle send button
     */
    @FXML
    private void handleSend() {

        if (validate(emailAddress.getText())) {
            saveEmailAddress();
            sendEmail();
        } else
            emailAddress.setText("Fill in valid email address");
    }

    private void showEmailInformation(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle(text);
        alert.setHeaderText("send successfully");
        alert.showAndWait();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    private void loadEmailAddress() {
        InputStream input;

        try {
            input = new FileInputStream(workingDir + propertiesName);
            props.load(input);
            emailAddress.setText(props.getProperty("Receiver"));
            input.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void saveEmailAddress() {
        FileOutputStream output;

        try {
            output = new FileOutputStream(workingDir + propertiesName);
            // set the properties value
            props.setProperty("Receiver", emailAddress.getText());
            props.store(output, "Receiver Changed");
            output.close();
        } catch (IOException ioe) {
            logger.error("An IO exception occurred: " + ioe.getMessage());
        }
    }

    public void setEmailStage(Stage emailStage) {
        this.emailStage = emailStage;
    }

    private void sendEmail() {
        boolean resultWaiter = false;
        boolean resultPeriod = false;
        if (topWaiters.isSelected())
            resultWaiter = mainApp.getCafe().waiterReport();
        if (dayReport.isSelected())
            resultPeriod = mainApp.getCafe().revenueReport(mainApp.getFrom(), mainApp.getTo());
        if (topWaiters.isSelected() && resultWaiter)
            showEmailInformation("TopWaiter report");
        if (dayReport.isSelected() && resultPeriod)
            showEmailInformation("Revenue report");
        emailStage.close();
    }

}
