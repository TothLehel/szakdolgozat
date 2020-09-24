/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import menetrendtervezo.datacontroller.DataController;
import menetrendtervezo.file.FileManager;
import menetrendtervezo.error.InputError;
import menetrendtervezo.route.RoadType;
import menetrendtervezo.route.Route;
import menetrendtervezo.route.RouteDestinations;
import menetrendtervezo.route.RouteTableView;
import menetrendtervezo.route.Stop;
import menetrendtervezo.route.StopDistance;

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
            stopTableBrowseButton, timetableBrowseButton,
            selectStartButton, selectStopButton, finishAddingRoutesButton;
    
    @FXML 
    Pane dialogePane;
    
    @FXML
    SplitPane splitPane;
    
    @FXML
    Button closeDialogeButton;
    
    @FXML 
    TableView stopList, routeTable, allRouteTable;
    
    @FXML
    Label selectedStartLabel, selectedStopLabel;
    
    @FXML
    TextField distanceTextField, routeNameTextField;
    
    @FXML
    ChoiceBox roadChoiceBox;
    
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
        ArrayList<RoadType> roadTypes = DATA_CONTROLLER.readRoadTypes();
        Map<String,String> roadTypeMap = new HashMap<>();
        for (RoadType rd : roadTypes) {
            roadTypeMap.put(rd.getRoadId(), rd.getRoadType());
        }
        
        roadChoiceBox.setItems(FXCollections.observableArrayList(roadTypeMap.values()));
        roadChoiceBox.getSelectionModel().selectFirst();
        initRouteTable();
        initAllRouteTable();
        
    }
    private void initAllRouteTable(){
        
        if(allRouteTable.getColumns().isEmpty()){
            TableColumn nameCol = new TableColumn("Név");
            nameCol.setCellValueFactory(new PropertyValueFactory<RouteTableView,String>("routeName"));
            nameCol.setMinWidth(110);
            nameCol.setMaxWidth(110);
            TableColumn firstStopCol = new TableColumn("Első megálló");
            firstStopCol.setCellValueFactory(new PropertyValueFactory<RouteTableView,String>("startName"));
            firstStopCol.setMinWidth(125);
            TableColumn lastStopCol = new TableColumn("Utolsó megálló");
            lastStopCol.setCellValueFactory(new PropertyValueFactory<RouteTableView,String>("endName"));
            lastStopCol.setMinWidth(120);
            TableColumn distanceCol = new TableColumn("Távolság");
            distanceCol.setCellValueFactory(new PropertyValueFactory<RouteTableView,Double>("distanceSum"));
            distanceCol.setMinWidth(70);
            distanceCol.setMaxWidth(70);
            TableColumn stopCount = new TableColumn("Megállók száma");
            stopCount.setCellValueFactory(new PropertyValueFactory<RouteTableView,Integer>("numberCount"));
            stopCount.setMinWidth(125);
            allRouteTable.getColumns().addAll(nameCol,firstStopCol,lastStopCol,distanceCol,stopCount);
            allRouteTable.setItems(FXCollections.observableList(DATA_CONTROLLER.listRouteTableViews()));
        
        }
    }
    @FXML
    public void addNewRoute(ActionEvent event){
        splitPane.setVisible(false);
        splitPane.setDisable(true);
        dialogePane.setVisible(true);
        dialogePane.setDisable(false);
        if(stopList == null){
            stopList = new TableView();
        }
        if(stopList.getColumns().isEmpty()){
            TableColumn stopName = new TableColumn("megálló neve");
            stopName.setMinWidth(160);
            stopName.setCellValueFactory(new PropertyValueFactory<Stop,String>("name"));
            stopName.setSortType(TableColumn.SortType.ASCENDING);
            stopList.getColumns().add(stopName);
            
        }
        List<Stop> stopArrayList = DATA_CONTROLLER.readStops();
        if(stopArrayList.size() > 0){
            stopList.setItems(FXCollections.observableArrayList(stopArrayList));
        }
        System.out.println("initRouteTable");
        initRouteTable();
        
    }
    private int routeId = -1;
    @FXML
    public void changeSelectedRoute(){
        splitPane.setVisible(false);
        splitPane.setDisable(true);
        dialogePane.setVisible(true);
        dialogePane.setDisable(false);
        if(stopList == null){
            stopList = new TableView();
        }
        if(stopList.getColumns().isEmpty()){
            TableColumn stopName = new TableColumn("megálló neve");
            stopName.setMinWidth(160);
            stopName.setCellValueFactory(new PropertyValueFactory<Stop,String>("name"));
            stopName.setSortType(TableColumn.SortType.ASCENDING);
            stopList.getColumns().add(stopName);
            
        }
        List<Stop> stopArrayList = DATA_CONTROLLER.readStops();
        if(stopArrayList.size() > 0){
            stopList.setItems(FXCollections.observableArrayList(stopArrayList));
        }
        initRouteTable();
        RouteTableView rtv = (RouteTableView)allRouteTable.getSelectionModel().getSelectedItem();
        routeTable.setItems(DATA_CONTROLLER.getStopDistancesByRouteId(rtv.getRouteId()));
        routeNameTextField.setText(rtv.getRouteName());
        routeId = rtv.getRouteId();
    }
    @FXML
    public void initRouteTable(){
        if(routeTable.getColumns().isEmpty()){
            TableColumn numberCol = new TableColumn("Sorrend");
            numberCol.setCellValueFactory(new PropertyValueFactory<StopDistance,Integer>("number"));
            numberCol.setMinWidth(75);
            TableColumn fromCol = new TableColumn("Innen");
            fromCol.setCellValueFactory(new PropertyValueFactory<StopDistance,String>("selectedStartName"));
            fromCol.setMinWidth(145);
            TableColumn toCol = new TableColumn("Ide");
            toCol.setCellValueFactory(new PropertyValueFactory<StopDistance,String>("selectedStopName"));
            toCol.setMinWidth(145);
            TableColumn roadCol = new TableColumn("Út típusa");
            roadCol.setCellValueFactory(new PropertyValueFactory<StopDistance,String>("roadName"));
            roadCol.setMinWidth(145);
            TableColumn distanceCol = new TableColumn("Távolság");
            distanceCol.setCellValueFactory(new PropertyValueFactory<StopDistance,Double>("distance"));
            distanceCol.setMinWidth(75);
            routeTable.getColumns().addAll(numberCol,fromCol,toCol,roadCol,distanceCol);
        }
        
    }
    
    @FXML
    public void exitDialogeWindow(ActionEvent event){
        setDialogeWindowToDefault();
    }
    private void setDialogeWindowToDefault(){
        dialogePane.setVisible(false);
        dialogePane.setDisable(true);
        splitPane.setVisible(true);
        splitPane.setDisable(false);
        routeTable.getItems().clear();
        routeNameTextField.setText("");
        selectedStartLabel.setText("Selected start");
        selectedStopLabel.setText("Selected stop");
        distanceTextField.setText("");
        roadChoiceBox.getSelectionModel().select(0);
        System.out.println("exiting..");
    }
    
    @FXML
    public void finishAddingRoutes(ActionEvent event){
        if(routeNameTextField != null && !"".equals(routeNameTextField.getText().trim()) && routeTable.getItems().size() >= 1){
            Route route = new Route();
            route.setRouteName(routeNameTextField.getText());
            System.out.println(route.toString());
            if(routeId != -1){
                route.setId(routeId);
                DATA_CONTROLLER.changeRoute(route);
                DATA_CONTROLLER.deleteRouteDestinationById(routeId);
                DATA_CONTROLLER.deleteStopDistancesByRouteId(routeId);
                DATA_CONTROLLER.deleteRouteTableViewByRouteId(routeId);
                routeId = -1;
            }else{
                DATA_CONTROLLER.addRoute(route);
            }
            System.out.println("routeid: " +route.getId());
            ArrayList<StopDistance> routeArrayList = new ArrayList<> (routeTable.getItems());
            RouteDestinations routeDestinations = new RouteDestinations();
            routeDestinations.setId(route.getId());
            //stop distance
            RouteTableView routeTableView = new RouteTableView();
            double dist = 0;
            
            
            for(StopDistance sd : routeArrayList){
                DATA_CONTROLLER.addStopDistance(sd);
                routeDestinations.setStopDistanceId(sd.getId());
                routeDestinations.setNumber(sd.getNumber());
                System.out.println("RouteDest: " + routeDestinations.getId() + " " + routeDestinations.getNumber() + " " + routeDestinations.getStopDistanceId());
                
                DATA_CONTROLLER.addRouteDestination(routeDestinations);
                dist += sd.getDistance();
            }
            routeTableView.setRouteId(route.getId());
            routeTableView.setRouteName(route.getRouteName());
            routeTableView.setStartName(routeArrayList.get(0).getSelectedStartName());
            routeTableView.setEndName(routeArrayList.get(routeArrayList.size()-1).getSelectedStopName());
            routeTableView.setDistanceSum(dist);
            int numOfStops = 0;
            for(int i = 1; i < routeArrayList.size(); i++){
                StopDistance prev = routeArrayList.get(i-1);
                StopDistance selected = routeArrayList.get(i);
                if(!prev.getSelectedStartName().equals(selected.getSelectedStartName()) || !prev.getSelectedStopName().equals(selected.getSelectedStopName())){
                    numOfStops++;    
                }
            }
            numOfStops++;
            routeTableView.setNumberCount(numOfStops);
            DATA_CONTROLLER.addRouteTableView(routeTableView);
            allRouteTable.setItems(FXCollections.observableList(DATA_CONTROLLER.listRouteTableViews()));
            
        }else{
            //error
        }
        System.out.println("route inserted!");
        setDialogeWindowToDefault();
    }
    
    @FXML
    public void selectStart(ActionEvent event){
        Stop stop = (Stop) stopList.getSelectionModel().selectedItemProperty().get();
        selectedStartLabel.setText(stop.getName());
    }
    
    @FXML
    public void selectEnd(ActionEvent event){
        Stop stop = (Stop)stopList.getSelectionModel().selectedItemProperty().get();
        selectedStopLabel.setText(stop.getName());
    }
    @FXML
    public void addNewRoad(ActionEvent event){
        String selectedFromChoiceBox = String.valueOf(roadChoiceBox.getSelectionModel().selectedItemProperty().getValue());
        if(!"".equals(distanceTextField.getText()) && Double.parseDouble(distanceTextField.getText()) > 0 
                && !"".equals(selectedStartLabel.getText()) && !"".equals(selectedStopLabel.getText()) 
                &&  !"null".equals(selectedFromChoiceBox) ){
                
                StopDistance sd = new StopDistance();
                sd.setNumber(routeTable.getItems().size()+1);
                sd.setSelectedStartName(selectedStartLabel.getText());
                sd.setSelectedStopName(selectedStopLabel.getText());
                sd.setDistance(Double.parseDouble(distanceTextField.getText()));
                sd.setRoadName(selectedFromChoiceBox);
                for(Stop stopDistance : (ObservableList<Stop>)stopList.getItems()){
                    if(stopDistance.getName() == null ? sd.getSelectedStartName() == null : stopDistance.getName().equals(sd.getSelectedStartName())){
                        sd.setStartId(stopDistance.getId());
                        System.out.println("stopId for start: " + stopDistance.getId());
                    }else if(stopDistance.getName() == null ? sd.getSelectedStopName() == null : stopDistance.getName().equals(sd.getSelectedStopName())){
                        sd.setEndId(stopDistance.getId());
                        System.out.println("stopId for end: " + stopDistance.getId() );
                    }
                }
                ArrayList<RoadType> roadTypes = DATA_CONTROLLER.readRoadTypes();
                for (RoadType rd : roadTypes) {
                    if(rd.getRoadType().equals(selectedFromChoiceBox)){
                        sd.setRoadId(rd.getRoadId());
                    }
                }
                
                routeTable.getItems().add(sd);
        }else{
            System.out.println("ERROR creation");
        }
    }
    @FXML
    public void deleteRoad(ActionEvent event){
        try{
            StopDistance removeStop = (StopDistance)routeTable.getSelectionModel().getSelectedItem();
            routeTable.getItems().remove(removeStop);
            ObservableList<StopDistance> stopList = routeTable.getItems();
            for(int i = 0 ; i <  stopList.size(); i++){
                stopList.get(i).setNumber(i+1);
            }
            routeTable.setItems(stopList);
        }catch(NullPointerException e){
            System.out.println("no item selected");
        }
    }
    @FXML
    public void moveUp(ActionEvent event){
        try{
            StopDistance stop = (StopDistance)routeTable.getSelectionModel().getSelectedItem();
            int stopIndex = routeTable.getSelectionModel().getSelectedIndex();
            if(stopIndex > 0){
                StopDistance change = (StopDistance) routeTable.getItems().get(stopIndex-1);
                stop.setNumber(stopIndex);
                change.setNumber(stopIndex + 1);
                routeTable.getItems().set(stopIndex-1, stop);
                routeTable.getItems().set(stopIndex,change);
                ObservableList<StopDistance> stopList = routeTable.getItems();
            }
        }catch(NullPointerException e){
            System.out.println("no item selected");
        }
    }
    
    @FXML
    public void moveDown(ActionEvent event){
        try{
            StopDistance stop = (StopDistance)routeTable.getSelectionModel().getSelectedItem();
            int stopIndex = routeTable.getSelectionModel().getSelectedIndex();
            if(stopIndex < routeTable.getItems().size()-1){
                StopDistance change = (StopDistance) routeTable.getItems().get(stopIndex+1);
                stop.setNumber(stopIndex+2);
                change.setNumber(stopIndex+1);
                routeTable.getItems().set(stopIndex+1, stop);
                routeTable.getItems().set(stopIndex,change);
            }
        }catch(NullPointerException e ){
            System.out.println("no item selected");
        }    
    }
    @FXML
    public void changeRoad(ActionEvent event){
        try{
            StopDistance stop = (StopDistance)routeTable.getSelectionModel().getSelectedItem();
            selectedStartLabel.setText(stop.getSelectedStartName());
            selectedStopLabel.setText(stop.getSelectedStopName());
            roadChoiceBox.getSelectionModel().select(stop.getRoadName());
            distanceTextField.setText(Double.toString(stop.getDistance()));
        }catch(NullPointerException e){
            System.out.println("none selected");
        }
    }
    @FXML
    public void deleteRoute(ActionEvent event){
        try{
            RouteTableView rtv = (RouteTableView)allRouteTable.getSelectionModel().getSelectedItem();
            allRouteTable.getItems().remove(rtv);
            System.out.println("rtv route id: " + rtv.getRouteId());
            DATA_CONTROLLER.removeRouteDestinationsByRouteId(rtv.getRouteId()); //route_destinations table
            DATA_CONTROLLER.deleteRouteTableViewByRouteId(rtv.getRouteId());
            DATA_CONTROLLER.removeRouteById(rtv.getRouteId()); //route table
        }catch(NullPointerException e){
            System.out.println("none selected");
        }
    }
    
}
