package be.leerstad.chezjava.view;

import be.leerstad.chezjava.MainApp;
import be.leerstad.chezjava.model.Beverage;
import be.leerstad.chezjava.model.Order;
import be.leerstad.chezjava.model.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.apache.log4j.Logger;

import java.util.Optional;


public class CafeDesktopController {
    private static Logger logger = Logger.getLogger(CafeDesktopController.class.getName());
    // top
    @FXML
    private Label currentWaiter;
    @FXML
    private Label totalOccupiedTables;
    @FXML
    private Label tablesUnderControl;

    // Overview Tables
    @FXML
    private FlowPane tablePane;

    // Order
    @FXML
    private Label currentTable;
    @FXML
    private Label currentBeverage;
    @FXML
    private Label currentQuantity;
    @FXML
    private TableView<Beverage> beverageTable;
    @FXML
    private TableColumn<Beverage, String> beverageNameColumb;

    private Integer quantity = 1;
    private Beverage selectedBeverage;


    //Bill
    @FXML
    private Label bill;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public CafeDesktopController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        currentBeverage.setText("");
        currentQuantity.setText(quantity.toString());
        // Initializes the beverage Table
        beverageNameColumb.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getItemName()));
        // Listen for the selection changes
        beverageTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    currentBeverage.setText(newValue.getItemName());
                    setSelectedBeverage(newValue);
                    // clear quantity
                    quantity = 1;
                    currentQuantity.setText(quantity.toString());
                });

    }

    private void loadTables() {
        // count tables under control
        Integer notUnderControl = 0;
        Integer underControl = 0;
        Integer occupied = 0;
        // Initialize Tables
        tablePane.getChildren().clear();
        tablePane.setVgap(10);
        tablePane.setHgap(10);
        for (Table t : mainApp.getCafe().getTables()
                ) {
            Button btn = new Button(t.getName());
            TableButtonHandler handler = new TableButtonHandler();
            logger.debug("Table Button Created: " + t.getName());
            btn.setOnAction(handler);

            // button background
            if (t.getWaiter() == (null)) {

                btn.setStyle("-fx-background-color: green");
                logger.debug(t.getName() + " is free");

                // Table has a waiter
            } else {

                if (t.getWaiter().equals(mainApp.getCafe().getUser())) {
                    btn.setStyle("-fx-background-color: orange");
                    logger.debug(t.getName() + " has orders");

                    underControl++;
                }
                // table has different waiter then user
                else {
                    btn.setStyle("-fx-background-color: red");
                    logger.debug(t.getName() + " is occupied");

                    btn.setDisable(true);
                    notUnderControl++;
                }
            }

            btn.setPadding(new Insets(10, 50, 10, 50));
            tablePane.getChildren().add(btn);
            // set labels
            tablesUnderControl.setText(underControl.toString());

            occupied = underControl + notUnderControl;
            totalOccupiedTables.setText(occupied.toString());

        }
        printBill();
    }

    public void setMainApp(MainApp mainApp) {

        this.mainApp = mainApp;
        beverageTable.setItems(mainApp.getBeverageList());
        currentWaiter.setText(mainApp.getCafe().getUser().toString());
        // Set Initial Table to the first non occupied table
        for (Table t : mainApp.getCafe().getTables()
                ) {
            if ((t.getWaiter() == null) || (t.getWaiter()).equals(mainApp.getCafe().getUser())) {
                mainApp.getCafe().setCurrentTable(t);
                break;
            }
        }

        currentTable.setText((mainApp.getCafe().getCurrentTable().getName()));

        loadTables();
    }

    @FXML
    private void handleSwitchUser() {
        showLogOffConfirmation();
    }

    @FXML
    private void handlePlusButton() {
        quantity++;
        currentQuantity.setText(quantity.toString());
        logger.debug("quantity increased " + quantity);
    }

    @FXML
    private void handleMinusButton() {
        quantity--;
        currentQuantity.setText(quantity.toString());
        logger.debug("quantity decreased " + quantity);

    }

    @FXML
    private void handleResetButton() {
        quantity = 1;
        currentQuantity.setText("1");
        logger.debug("quantity reset " + quantity);

    }

    @FXML
    private void handleAddToTable() {
        if (!(selectedBeverage == null)) {
            Order order = new Order(selectedBeverage, quantity);
            mainApp.getCafe().Order(order);
            printBill();
            loadTables();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Select Beverage");
            alert.setHeaderText("Please select a beverage");
        }

    }

    @FXML
    private void handlePayOrders() {
        mainApp.getCafe().getCurrentTable().payOrders();
        bill.setText("");
        loadTables();
    }

    @FXML
    private void handleReports() {
        mainApp.showReports();
        logger.debug("reports screen opened");


    }

    private void showLogOffConfirmation() {
        // Nothing Selected
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Log Off");
        alert.setHeaderText("Are you sure you want to log off");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            mainApp.getCafe().logOff();
            mainApp.showLogon();
        }


    }

    private void printBill() {
        StringBuilder sb = new StringBuilder(mainApp.getCafe().getCurrentTable().toString());
        bill.setText(sb.toString());
        logger.info("Print Bill");
        logger.info(sb);
    }

    public void setSelectedBeverage(Beverage selectedBeverage) {
        this.selectedBeverage = selectedBeverage;
    }

    class TableButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            String text = ((Button) e.getSource()).getText();
            for (Table t : mainApp.getCafe().getTables()) {
                if (t.getName().equals(text)) {
                    mainApp.getCafe().setCurrentTable(t);
                    currentTable.setText(mainApp.getCafe().getCurrentTable().getName());
                    printBill();
                }
            }


        }
    }
}
