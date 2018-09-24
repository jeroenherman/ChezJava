package be.leerstad.chezjava;


import be.leerstad.chezjava.model.*;
import be.leerstad.chezjava.view.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private Cafe cafe = new Cafe();
    private ObservableList<Beverage> beverageList = FXCollections.observableArrayList(cafe.getBeverages());
    private ObservableList<Order> userSales = FXCollections.observableArrayList();

    private ObservableList<Revenue> revenueData = FXCollections.observableArrayList();
    private LocalDate from = LocalDate.now();
    private LocalDate to = LocalDate.now();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Cafe Desktop");
        initRootLayout();
        showLogon();
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });
    }

    private void closeProgram() {
        // Nothing Selected
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(getPrimaryStage());
        alert.setTitle("Close Cafe?");
        alert.setHeaderText("Are you sure you want to close the café?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            getCafe().logOff();
            primaryStage.close();
        }
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
// Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/RootLayout.fxml"));
            rootLayout = loader.load();
// Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            // Give the controller access to the main app.
            RootController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the logon Screen
     */
    public void showLogon() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/LogOn.fxml"));
            StackPane logOn = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Log On");
            loginStage.initModality(Modality.WINDOW_MODAL);
            loginStage.initOwner(primaryStage);
            loginStage.setOnCloseRequest(e -> {
                e.consume();
                closeProgram();
            });
            Scene scene = new Scene(logOn);
            loginStage.setScene(scene);

            // Give the controller access to the main app.
            LoginController controller = loader.getController();
            controller.loggedInProperty().addListener((obs, wasLoggedIn, isNowLoggedIn) -> {
                if (isNowLoggedIn) {
                    loginStage.hide();
                }
            });
            controller.setMainApp(this);
            loginStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the CafeDesktop view
     */
    public void showCafeDesktop() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/CafeDesktop.fxml"));
            BorderPane cafeDesktop = loader.load();
            // Set Cafe Desktop into the center of the root layout.
            rootLayout.setCenter(cafeDesktop);

            // Give the controller access to the main app.
            CafeDesktopController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the Reports view
     */
    public boolean showReports() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/Reports.fxml"));
            AnchorPane reports = loader.load();
            // Set Cafe Desktop into the center of the root layout.
            rootLayout.setCenter(reports);

            // Give the controller access to the main app.
            ReportController controller = loader.getController();
            controller.setMainApp(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get Access to the Cafe
     *
     * @return
     */
    public Cafe getCafe() {
        return cafe;
    }

    /**
     * /**
     * Get observable List of beverages
     *
     * @return
     */
    public ObservableList<Beverage> getBeverageList() {
        return beverageList;
    }

    /**
     * Get observable List of sales ( Orders grouped by beverage)
     *
     * @return
     */
    public ObservableList<Order> getSales() {
        return userSales;
    }

    /**
     * Set observable list of Orders filter by user and from to date
     *
     * @param user
     * @param from
     * @param to
     */
    public void setSales(User user, LocalDate from, LocalDate to) {
        userSales.clear();
        userSales.addAll(cafe.getAllSales(user, from, to));

    }

    public void setSales(LocalDate from, LocalDate to) {
        userSales.clear();
        userSales.addAll(cafe.getAllSales(from, to));
    }

    public ObservableList<Revenue> getRevenueData() {
        return revenueData;
    }

    public void setRevenueData() {
        this.revenueData.clear();
        for (Waiter w : getCafe().getWaiters()
                ) {
            revenueData.add(new Revenue(w, getCafe().getTotalRevenue(w)));
        }
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showEmailReports(LocalDate from, LocalDate to) {
        this.setFrom(from);
        this.setTo(to);
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/EmailReports.fxml"));
            StackPane emailReports = loader.load();
            Stage emailStage = new Stage();
            emailStage.setTitle("Email reports");
            emailStage.initModality(Modality.WINDOW_MODAL);
            emailStage.initOwner(primaryStage);
            Scene scene = new Scene(emailReports);
            emailStage.setScene(scene);

            // Give the controller access to the main app.
            EmailReportController controller = loader.getController();
            controller.setMainApp(this);
            controller.setEmailStage(emailStage);
            emailStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}


