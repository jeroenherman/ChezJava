package be.leerstad.chezjava.view;

import be.leerstad.chezjava.MainApp;
import be.leerstad.chezjava.model.Order;
import be.leerstad.chezjava.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class ReportController {
    DecimalFormat df = new DecimalFormat("#.00€");
    @FXML
    DatePicker toDate;
    private MainApp mainApp;
    private Double totalRevenueUser;
    private Double totalRevenue;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label currentWaiterLabel;
    @FXML
    private Label totalRevenueWaiterLabel;
    // Daily Revenue
    @FXML
    private ChoiceBox<User> waiterChoiceBox;
    @FXML
    private CheckBox totalCheckBox;
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> orderQuantityColumb;
    @FXML
    private TableColumn<Order, String> orderBeverageNameColumb;
    @FXML
    private TableColumn<Order, String> orderBeveragePriceColumb;
    @FXML
    private TableColumn<Order, String> orderSubTotalColumb;
    @FXML
    private DatePicker fromDate;
    @FXML
    private Label totalRevenuePeriodLabel;

    // Top Waiters

    @FXML
    private TableView<Revenue> topWaiterTable;
    @FXML
    private TableColumn<Revenue, String> firstNameColumb;
    @FXML
    private TableColumn<Revenue, String> lastNameColumb;
    @FXML
    private TableColumn<Revenue, Double> revenueColumb;
    @FXML
    // Pie chart
    private ObservableList<PieChart.Data> piechartData;
    @FXML
    private PieChart pieChart;

    public ReportController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Setup Revenue Tabel
        PropertyValueFactory<Order, Integer> orderQuantityProperty = new PropertyValueFactory<>("quantity");
        orderQuantityColumb.setCellValueFactory(orderQuantityProperty);
        orderBeverageNameColumb.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getItemName()));
        orderBeveragePriceColumb.setCellValueFactory(e -> new SimpleStringProperty(df.format(e.getValue().getItemPrice())));
        orderSubTotalColumb.setCellValueFactory(e -> new SimpleStringProperty(df.format(e.getValue().getOrderPrice())));
        totalRevenuePeriodLabel.setText("");
        toDate.setValue(LocalDate.now());
        fromDate.setValue(LocalDate.now());
        // total Check box Actions
        totalCheckBox.setOnAction(e -> {
            if (totalCheckBox.isSelected()) {
                waiterChoiceBox.getSelectionModel().clearSelection();
                waiterChoiceBox.disableProperty().setValue(true);
                currentWaiterLabel.setText("");
                totalRevenueWaiterLabel.setText("");
            } else {
                waiterChoiceBox.disableProperty().setValue(false);
                waiterChoiceBox.getSelectionModel().select(mainApp.getCafe().getUser());
            }
        });
        // Waiter Check Box actions
        waiterChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (!(totalCheckBox.isSelected())) {
                                showRevenueDetails(newValue, fromDate.getValue(), toDate.getValue());
                            } else {
                                showRevenue(fromDate.getValue(), toDate.getValue());
                            }
                        });
        // From Date actions
        fromDate.setOnAction(e -> {
            if (!(totalCheckBox.isSelected())) {
                toDate.setValue(fromDate.getValue());
                showRevenueDetails(waiterChoiceBox.getValue(), fromDate.getValue(), toDate.getValue());
            } else {
                toDate.setValue(fromDate.getValue());
                showRevenue(fromDate.getValue(), toDate.getValue());
            }
        });
        // to Date Actions
        toDate.setOnAction(e -> {

            if (!(totalCheckBox.isSelected())) {
                showRevenueDetails(waiterChoiceBox.getValue(), fromDate.getValue(), toDate.getValue());
            } else {
                showRevenue(fromDate.getValue(), toDate.getValue());
            }
        });
        // Setup Top Waiter Table
        firstNameColumb.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getWaiter().getFirstName()));
        lastNameColumb.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getWaiter().getLastName()));
        PropertyValueFactory<Revenue, Double> revenueDoublePropertyValueFactory = new PropertyValueFactory<>("total");
        revenueColumb.setCellValueFactory(revenueDoublePropertyValueFactory);

    }

    /**
     * fills order table sets current user field based on choicebox, from , to date picker
     */
    private void showRevenueDetails(User user, LocalDate from, LocalDate to) {
        currentWaiterLabel.setText(user.toString());
        totalRevenueUser = mainApp.getCafe().getTotalRevenue(user).doubleValue();
        totalRevenueWaiterLabel.setText(df.format(totalRevenueUser));
        mainApp.setSales(user, from, to);
        orderTable.setItems(mainApp.getSales());
        totalRevenuePeriodLabel.setText(df.format(mainApp.getSales().stream().mapToDouble(Order::getOrderPrice).sum()));
    }

    /**
     * fills order table for all waiters, from , to date picker
     */
    private void showRevenue(LocalDate from, LocalDate to) {
        waiterChoiceBox.getSelectionModel().clearSelection();
        mainApp.setSales(from, to);
        orderTable.setItems(mainApp.getSales());
        totalRevenuePeriodLabel.setText(df.format(mainApp.getSales().stream().mapToDouble(Order::getOrderPrice).sum()));
    }

    private void showPiechart() {

        piechartData = FXCollections.observableArrayList();
        for (User waiter : mainApp.getCafe().getTopWaiters().keySet()) {
            piechartData.add(new PieChart.Data(waiter.toString(), mainApp.getCafe().getTopWaiters().get(waiter)));
        }
        pieChart.setData(piechartData);
        pieChart.setTitle("Top Waiters");
    }

    @FXML
    private void handleCafe() {
        mainApp.showCafeDesktop();
    }

    @FXML
    private void handleEmail() {
        mainApp.showEmailReports(fromDate.getValue(), toDate.getValue());
    }

    /**
     * Set Controller variables gives access to main app
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        // load sales list
        mainApp.setSales(mainApp.getCafe().getUser(), LocalDate.now(), LocalDate.now());
        // set top Labels
        totalRevenue = mainApp.getCafe().getTotalRevenues().values().stream().mapToDouble(Double::doubleValue).sum();
        totalRevenueUser = mainApp.getCafe().getTotalRevenue(mainApp.getCafe().getUser()).doubleValue();
        totalRevenueLabel.setText(df.format(totalRevenue));
        currentWaiterLabel.setText(mainApp.getCafe().getUser().toString());
        totalRevenueWaiterLabel.setText(df.format(totalRevenueUser));
        // Set Choice Box
        waiterChoiceBox.getItems().addAll(mainApp.getCafe().getWaiters());
        waiterChoiceBox.getSelectionModel().select(mainApp.getCafe().getUser());
        // set Top waiters
        mainApp.setRevenueData();
        topWaiterTable.setItems(mainApp.getRevenueData());
        showPiechart();
    }
}
