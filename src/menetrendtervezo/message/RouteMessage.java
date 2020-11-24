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
public class RouteMessage {

    public void emptyRouteNameError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kitöltetlen mező!");
        alert.setHeaderText(null);
        alert.setContentText("Az Útvonal neve nincs kitöltve!");
        alert.show();
    }

    public void emptyStopTableListError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Üres Lista!");
        alert.setHeaderText(null);
        alert.setContentText("Nincsenek megállók hozzárendelve az Útvonalhoz!");
        alert.show();
    }
    public void emptyRouteDistanceError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kitöltetlen mező!");
        alert.setHeaderText(null);
        alert.setContentText("A távolság mezője üres!");
        alert.show();
    }
    public void routeDistanceFormatError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Típus hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A távolság mezőjében nem megfelelő érték szerepel!");
        alert.show();
     }

    public void routeDistanceLessThanZero() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nem megfelelő távolság érték!");
        alert.setHeaderText(null);
        alert.setContentText("A távolság mezőjében nem szerepelhet negatív szám!");
        alert.show();
    }

    public void startIsNotSetError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nincs kiválasztott megálló!");
        alert.setHeaderText(null);
        alert.setContentText("A kezdő megálló nincs beállítva!");
        alert.show();
    }

    public void stopIsNotSetError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nincs kiválasztott megálló!");
        alert.setHeaderText(null);
        alert.setContentText("A végpont megálló nincs beállítva!");
        alert.show();
    }

    public void notConnectedStopDistancesError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nem összefüggő útvonal!");
        alert.setHeaderText(null);
        alert.setContentText("Az újonnan beilleszteni kívánt útszakasz kezdő megállójának egyenlőnek kell lennie az előző útszakasz végső megállójával, "
                + "vagy az új szakasznak a kezdő és végpontjainak kell egyeznie az előző útszakasz kezdő és végpontjával!  ");
        alert.show();
    }

    public void startEqualsStopError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nem összefüggő útvonal!");
        alert.setHeaderText(null);
        alert.setContentText("A kezdő megálló ugyanaz mint a befejező megálló!");
        alert.show();
    }
    
}
