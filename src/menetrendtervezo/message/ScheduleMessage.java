/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.message;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author tlehe
 */
public class ScheduleMessage {
    public void scheduleAlreadyExists() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Menetrend már létezik!");
        alert.setHeaderText(null);
        alert.setContentText("A létrehozni kívánt menetrend neve már foglalt! Válasszon másikat");
        alert.show();
    }
    
    public void startEqualsToEndError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Egyező érték");
        alert.setHeaderText(null);
        alert.setContentText("A kezdő és befjező időpont megegyezik! Válasszon másik értékekekt!");
        alert.show();
    }

    public void noDateGiven() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nincs dátum megadva!");
        alert.setHeaderText(null);
        alert.setContentText("Válasszon egy dátumot ahova be szeretén illeszteni az adatokat!");
        alert.show();
    }

    public void noRouteSelected() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nincs útvonal kiválasztva!");
        alert.setHeaderText(null);
        alert.setContentText("Válasszon ki útvonalat az időponthoz!");
        alert.show();
    }

    public void emptyScheduleName() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Névtelen menetrend");
        alert.setHeaderText(null);
        alert.setContentText("A menetrend neve nem lehet üres!");
        alert.show();
    }

    public void noAppointmentInserted() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nincs időpont a menetrendben!");
        alert.setHeaderText(null);
        alert.setContentText("A létrehozni kívánt menetrendhez nem tartonzak időpontok!");
        alert.show();
    }
    public boolean scheduleIsBound() {
        String str = "Az Menetrendhez tartoznak beosztás adatok amik a menetrend törlésével eltűnnek! Biztosan ezt akarja?";  
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,str,ButtonType.OK,ButtonType.CANCEL);
        alert.setTitle("Menetrendhez tartoznak adatok!");
        alert.setHeaderText(null);
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.OK)){
            return true;
        }
        return false;
    }
}
