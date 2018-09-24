package be.leerstad.chezjava.view;

import be.leerstad.chezjava.MainApp;
import javafx.fxml.FXML;

public class RootController {
    MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleResetTables() {
        mainApp.getCafe().resetTables();
        mainApp.showCafeDesktop();
    }

}
