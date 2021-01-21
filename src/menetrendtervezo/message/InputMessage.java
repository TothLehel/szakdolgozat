/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.message;

import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author tlehe
 */
public class InputMessage {
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
    
    public void succesfulDriverTableInput(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Adatbeviteli Sikeres!");
        alert.setHeaderText(null);
        alert.setContentText("A Sofőr lista sikeresen felvéve az adatbázisba!");
        alert.show();
    }
    public void succesfulStopTableInput(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Adatbeviteli Sikeres!");
        alert.setHeaderText(null);
        alert.setContentText("A Megállók lista sikeresen felvéve az adatbázisba!");
        alert.show();
    }
    public void succesfulVehicleTableInput(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Adatbeviteli Sikeres!");
        alert.setHeaderText(null);
        alert.setContentText("A Járművek lista sikeresen felvéve az adatbázisba!");
        alert.show();
    }

    public void licencePlateExists(Row row) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatok felvitele sikertelen!");
        alert.setHeaderText(null);
        alert.setContentText("A(z) " + row.getRowNum() + ". sorban található rendszám már létezik az adatbázisban!");
        alert.show();
    }

    public void driverAlreadyExists(Row row) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatok felvitele sikertelen!");
        alert.setHeaderText(null);
        alert.setContentText("A(z) " + row.getRowNum() + ". sorban található sofőr kétszer található a táblázatban!");
        alert.show();
    }

    public void dateAlreadyExists(Row row) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Adatok felvitele sikertelen!");
        alert.setHeaderText(null);
        alert.setContentText("A(z) " + row.getRowNum() + ". sorban található sofőrhöz már létezik a(z) "+row.getSheet().getActiveCell().toString() +" dátumhoz érték az adatbázisban!");
        alert.show();
    }

    public boolean databaseContainsDriverValues() {
        //BooleanProperty confirm = BooleanProperty.booleanProperty(false);
        String str = "Az adatbázis már tartalmaz sofőr adatokat amelyek új táblázat beillesztésével törlődnek! Ez a létrehozott beosztás adatainak törlésével járhat! Biztosan ezt akarja?";  
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,str,ButtonType.OK,ButtonType.CANCEL);
        alert.setTitle("Adatbázis tartalmaz adatokat!");
        alert.setHeaderText(null);
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.OK)){
            return true;
        }
        return false;
    }
    public boolean databaseContainsVehiclesValues() {
        String str = "Az adatbázis már tartalmaz jármű adatokat amelyek új táblázat beillesztésével törlődnek! Ez a létrehozott beosztás adatainak törlésével járhat! Biztosan ezt akarja?";  
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,str,ButtonType.OK,ButtonType.CANCEL);
        alert.setTitle("Adatbázis tartalmaz adatokat!");
        alert.setHeaderText(null);
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.OK)){
            return true;
        }
        return false;
    }
}
