/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.error;

import javafx.scene.control.Alert;

/**
 *
 * @author tlehe
 */
public class InputError {
    public void noTableGiven(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Nem adott meg táblákat!");
        alert.show();
    }
    public void driverTableDontExists(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A Sofőr listát tartalmazó tábla mezője üres!");
        alert.show();
    }
    public void stopTableDontExists(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A megállókat tartalmazó tábla mezője üres!");
        alert.show();
    }
    public void vehicleTableDontExists(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A Járműveket tartalmazó tábla mezője üres!");
        alert.show();
    }
    public void stopTableFormatError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A megállókat tartalmazó tábla sémája nem megfelelő!");
        alert.show();
    }
    public void vehicleTableFormatError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A járműveket tartalmazó tábla sémája nem megfelelő!");
        alert.show();
    }
    public void driverTableFormatError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A sofőröket tartalmazó tábla sémája nem megfelelő!");
        alert.show();
    }
    /*public void wrongInputType(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatbeviteli Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("");
        alert.show();
    }*/
}
