/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javax.annotation.PostConstruct;
import javax.rmi.CORBA.Util;
import menetrendtervezo.datacontroller.DataController;
import menetrendtervezo.file.FileManager;
import menetrendtervezo.error.InputError;
import menetrendtervezo.route.RoadType;
import menetrendtervezo.route.Stop;

/**
 *
 * @author tlehe
 */
public class FXMLDocumentController implements Initializable {
    final FileManager FILE_MANAGER = new FileManager();
    File driverTable, stopTable, vehicleTable, timetableTable;
    final InputError INPUT_ERROR = new InputError();
    final DataController DATA_CONTROLLER = new DataController();
           
    @FXML
    TextField driverTableDirectory, stopTableDirectory, 
            vehicleTableDirectory, timetableTableDirectory;
    
    @FXML
    Button driverTableBrowseButton, vehicleTableBrowseButton, 
            stopTableBrowseButton, timetableBrowseButton;
    
    @FXML 
    Pane dialogePane;
    
    @FXML
    SplitPane splitPane;
    
    @FXML
    Button closeDialogeButton;
    
    @FXML 
    TableView stopList, selectedStopList;
    
    @FXML
    Label selectedStartLabel, selectedStopLabel;
    
    @FXML
    TextField distanceTextField;
    
    @FXML
    ChoiceBox roadChoiceBox;
    
    public TableView startingStopList;
    
    @FXML
    private void Browse(ActionEvent event) {
       Button btn = (Button)event.getSource();
        if(btn.getId().equals(driverTableBrowseButton.getId())){
            FILE_MANAGER.setTitle("Sofőröket tartalmazó tábla kiválasztása");
            driverTable = FILE_MANAGER.TableBrowse();
            if(driverTable != null){
                driverTableDirectory.setText(driverTable.getAbsolutePath());
            }
        }
        else if(btn.getId().equals(vehicleTableBrowseButton.getId())){
            FILE_MANAGER.setTitle("Járműveket tartalmazó tábla kiválasztása");
            vehicleTable = FILE_MANAGER.TableBrowse();
            if(vehicleTable != null){
                vehicleTableDirectory.setText(vehicleTable.getAbsolutePath());
            }
        }
        else if(btn.getId().equals(stopTableBrowseButton.getId())){
            FILE_MANAGER.setTitle("Megállókat tartalmazó tábla kiválasztása");
            stopTable = FILE_MANAGER.TableBrowse();
            if(stopTable != null){
                stopTableDirectory.setText(stopTable.getAbsolutePath());
            }
        }
        else if(btn.getId().equals(timetableBrowseButton.getId())){
            FILE_MANAGER.setTitle("Menetrendet tartalmazó tábla kiválasztása");
            timetableTable = FILE_MANAGER.TableBrowse();
            if(timetableTable != null){
                timetableTableDirectory.setText(timetableTable.getAbsolutePath());
            }
        }
    }
   
    @FXML
    private void ImportTables(ActionEvent event) {
        driverTable = new File(driverTableDirectory.getText());
        stopTable = new File(stopTableDirectory.getText());
        vehicleTable = new File(vehicleTableDirectory.getText());
        timetableTable = new File(timetableTableDirectory.getText());
        
        Boolean driverTableExists = driverTable.isFile();
        Boolean stopTableExists = stopTable.isFile();
        Boolean vehicleTableExists = vehicleTable.isFile();
        Boolean timetableExists = timetableTable.isFile();
        
        if(timetableExists){
           //timetable beolvasás 
            System.out.println("timetable olvasása");
        }
        else{
            if(driverTableExists){
                FILE_MANAGER.ReadDriverTable(driverTable);
            }
            if(stopTableExists){
                FILE_MANAGER.ReadStopTable(stopTable);
            }
            if(vehicleTableExists){
                FILE_MANAGER.ReadVehicleTable(vehicleTable);
            }
        }
        /*else if(!(driverTableExists || stopTableExists || vehicleTableExists)){
            //INPUT_ERROR.noTableGiven();
        }else if(driverTableExists && stopTableExists && vehicleTableExists){
            FILE_MANAGER.ReadDriverTable(driverTable);
            FILE_MANAGER.ReadStopTable(stopTable);
            FILE_MANAGER.ReadVehicleTable(vehicleTable);
        /*}else{
          if(!driverTableExists)
            INPUT_ERROR.driverTableDontExists();
          if(!stopTableExists)
            INPUT_ERROR.stopTableDontExists();
          if(!vehicleTableExists)
            INPUT_ERROR.vehicleTableDontExists();
        }*/
        
    } 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TableColumn stopName = new TableColumn("megálló neve");
        stopName.setMinWidth(160);
        stopName.setCellValueFactory(new PropertyValueFactory<Stop,String>("name"));
        stopName.setSortable(false);
        selectedStopList.getColumns().add(stopName);
        selectedStopList.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                
                Stop clickedStop = (Stop)selectedStopList.getSelectionModel().getSelectedItem();
                if( clickedStop != null){
                    selectedStartLabel.setText(clickedStop.getName());
                    int nextStopIndex = selectedStopList.getSelectionModel().getSelectedIndex()+1;
                    if(nextStopIndex < selectedStopList.getItems().size()){
                        Stop nextStop = (Stop) selectedStopList.getItems().get(nextStopIndex);
                        selectedStopLabel.setText(nextStop.getName());
                    }else{
                        selectedStopLabel.setText("Útvonal vége");
                    }
                }
            }
        
        });
        ArrayList<RoadType> roadTypes = DATA_CONTROLLER.readRoadTypes();
        Map<String,String> roadTypeMap = new HashMap<>();
        for (RoadType rd : roadTypes) {
            roadTypeMap.put(rd.getRoadId(), rd.getRoadType());
        }
        roadChoiceBox.setItems(FXCollections.observableArrayList(roadTypeMap.values()));
    }
    
    @FXML
    public void addNewRoute(ActionEvent event){
        splitPane.setVisible(false);
        splitPane.setDisable(true);
        dialogePane.setVisible(true);
        dialogePane.setDisable(false);
        
        TableColumn stopName = new TableColumn("megálló neve");
        stopName.setMinWidth(160);
        stopName.setCellValueFactory(new PropertyValueFactory<Stop,String>("name"));
        stopName.setSortType(TableColumn.SortType.ASCENDING);
        stopList.getColumns().add(stopName);
        List<Stop> stopArrayList = DATA_CONTROLLER.readStops();
        if(stopArrayList.size() > 0){
            stopList.setItems(FXCollections.observableArrayList(stopArrayList));
        }
        startingStopList = stopList;
        //selectedStopList.getColumns().add(stopName);     
    }
    
    @FXML
    public void exitDialogeWindow(ActionEvent event){
        dialogePane.setVisible(false);
        dialogePane.setDisable(true);
        splitPane.setVisible(true);
        splitPane.setDisable(false);
        stopList = startingStopList;
        selectedStopList.getItems().clear();
        System.out.println("exiting..");
    }
    
    @FXML
    public void finishAddingRoutes(ActionEvent event){
        //megjeleníteni a létrehozott adatokat
        splitPane.setVisible(true);
        splitPane.setDisable(false);
        dialogePane.setVisible(false);
        dialogePane.setDisable(true);
    }
    
    @FXML
    public void selectStop(ActionEvent event){
        Stop stop = (Stop) stopList.getSelectionModel().selectedItemProperty().get();
        selectedStopList.getItems().add(stop);
        stopList.getItems().remove(stop);
    }
    
    @FXML
    public void removeStop(ActionEvent event){
        Stop stop = (Stop) selectedStopList.getSelectionModel().selectedItemProperty().get();
        stopList.getItems().add(stop);
        stopList.sort();
        selectedStopList.getItems().remove(stop);
    
    }
    @FXML
    public void addNewRoad(ActionEvent event){
        if(null != distanceTextField && Double.parseDouble(distanceTextField.getText()) > 0){
            
        }else{
            //ERROR 
        }
    }
}
