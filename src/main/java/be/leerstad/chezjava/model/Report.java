package be.leerstad.chezjava.model;

import java.time.LocalDate;

public interface Report {
    boolean revenueReport(LocalDate localDate);

    boolean waiterReport();

}
