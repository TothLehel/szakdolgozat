/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import menetrendtervezo.datacontroller.DataController;
import menetrendtervezo.file.FileManager;

/**
 *
 * @author tlehe
 */
public class adatbevitelTabController implements Initializable{
    final FileManager FILE_MANAGER = new FileManager();
    File driverTable, stopTable, vehicleTable, timetableTable;
    final DataController DATA_CONTROLLER = new DataController();
           
    @FXML
    TextField driverTableDirectory, stopTableDirectory, 
            vehicleTableDirectory, timetableTableDirectory;
    
    @FXML
    Button driverTableBrowseButton, vehicleTableBrowseButton, 
            stopTableBrowseButton, timetableBrowseButton,
            selectStartButton, selectStopButton, finishAddingRoutesButton;
    
    
    
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
    }
    @FXML
    private void importTables(ActionEvent event) {
        driverTable = new File(driverTableDirectory.getText());
        stopTable = new File(stopTableDirectory.getText());
        vehicleTable = new File(vehicleTableDirectory.getText());
        
        if(driverTable.isFile()){
            FILE_MANAGER.readTable(driverTable, "driver");
        }
        if(stopTable.isFile()){
            FILE_MANAGER.readTable(stopTable, "stop");
        }
        if(vehicleTable.isFile()){
            FILE_MANAGER.readTable(vehicleTable, "vehicle");
        }
    } 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
    }
}
