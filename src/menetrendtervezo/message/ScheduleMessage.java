/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.message;

import javafx.scene.control.Alert;

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
}
