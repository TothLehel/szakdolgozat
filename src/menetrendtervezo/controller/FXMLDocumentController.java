/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.controller;

import java.io.File;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.internal.scene.control.skin.agenda.AgendaDaySkin;
import jfxtras.internal.scene.control.skin.agenda.AgendaWeekSkin;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentImplLocal;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import menetrendtervezo.datacontroller.DataController;
import menetrendtervezo.entity.Schedule;
import menetrendtervezo.file.FileManager;
import menetrendtervezo.message.InputMessage;
import menetrendtervezo.message.RouteMessage;
import menetrendtervezo.message.ScheduleMessage;
import menetrendtervezo.route.RoadType;
import menetrendtervezo.route.Route;
import menetrendtervezo.route.RouteDestinations;
import menetrendtervezo.route.RouteTableView;
import menetrendtervezo.route.Stop;
import menetrendtervezo.route.StopDistance;
import menetrendtervezo.spinnerfactory.CycleSpinner;
import menetrendtervezo.spinnerfactory.DaySpinner;
import menetrendtervezo.spinnerfactory.HourSpinner;
import menetrendtervezo.spinnerfactory.MinuteSpinner;
import menetrendtervezo.spinnerfactory.YearAndMonthSpinner;

public class FXMLDocumentController implements Initializable {
    final FileManager FILE_MANAGER = new FileManager();
    File driverTable, stopTable, vehicleTable, timetableTable;
    final InputMessage INPUT_ERROR = new InputMessage();
    final DataController DATA_CONTROLLER = new DataController();
    final ScheduleMessage SCHEDULE_MESSAGE = new ScheduleMessage();
    private String changeScheduleName = null;
    private final RouteMessage ROUTE_MESSAGE = new RouteMessage();       
    @FXML
    TextField driverTableDirectory, stopTableDirectory, 
            vehicleTableDirectory, timetableTableDirectory,
            scheduleNameTextField;
    
    @FXML
    Button driverTableBrowseButton, vehicleTableBrowseButton, 
            stopTableBrowseButton, timetableBrowseButton,
            selectStartButton, selectStopButton, finishAddingRoutesButton;
    
    @FXML 
    Pane dialogePane, schedulePlanningPage;
    
    @FXML
    SplitPane splitPane;
    
    @FXML
    Button closeDialogeButton, exitWithoutSaving;
    
    @FXML 
    TableView stopList, routeTable, allRouteTable,routeTableForSchedule, scheduleList;
    
    @FXML
    Label selectedStartLabel, selectedStopLabel, cyclicLabel;
    
    @FXML
    TextField distanceTextField, routeNameTextField, 
            startingHourTextField, endigHourTextField, startingMinutesTextField,
            endingMinutesTextField;
    
    @FXML
    ChoiceBox roadChoiceBox, weekChoiceBox, monthChoiceBox;
    
    @FXML
    CheckBox cyclicCheckBox;
    
    @FXML
    Slider cyclicSlider; 
    
    @FXML
    DatePicker datePicker;
    
    @FXML 
    Spinner startHourSpinner, endHourSpinner, startMinSpinner, 
            endMinSpinner, dateSpinner, cycleSpinner;
    
    @FXML
    Agenda scheduleAgenda;
    
    @FXML
    TabPane tabPane;
    
    @FXML 
    BeosztasTabController beosztasPageController;
    
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
        initRouteTableForSchedule();
        initScheduleTable();
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>(){
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if("beosztasTab".equals(newValue.getId())){
                    System.out.println("tab change");
                    beosztasPageController.init(scheduleList.getItems());
                }else{
                    beosztasPageController.clearLists();
                }
            }
            
        });
        
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
           
        }
        allRouteTable.setItems(FXCollections.observableList(DATA_CONTROLLER.listRouteTableViews()));
        
        
    }
    private void initScheduleTable(){
        if(scheduleList.getColumns().isEmpty()){
            TableColumn nameCol = new TableColumn("Menetrend neve");
            nameCol.setCellValueFactory(new PropertyValueFactory<Schedule,String>("scheduleName"));
            nameCol.setMinWidth(225);
            nameCol.setMaxWidth(225);
            scheduleList.getColumns().add(nameCol);
        }
        scheduleList.setItems(FXCollections.observableList(new ArrayList<>(DATA_CONTROLLER.listSchedulesOnlyOnce())));
        
    }
    private void isCyclic(){
        cyclicCheckBox.setSelected(false);
        cycleSpinner.setDisable(true);
        cyclicSlider.setDisable(true);
        EventHandler eh = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(!cyclicCheckBox.isSelected()){
                    cycleSpinner.setDisable(true);
                    cyclicSlider.setDisable(true);
                    cyclicLabel.setText("ütem idő");
                }else{
                    cycleSpinner.setDisable(false);
                    cycleSpinner.setValueFactory(new CycleSpinner().getMinSpinner());
                    cycleSpinner.setEditable(true);
                    cyclicSlider.setMin(12);
                    cyclicSlider.setMax(14);
                    cyclicSlider.setMajorTickUnit(1);
                    cyclicSlider.setMinorTickCount(0);
                    cyclicSlider.setSnapToTicks(true);
                    cyclicLabel.setText(String.valueOf((int)cyclicSlider.getValue()) + " Óra");
                    cyclicSlider.valueProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            cyclicLabel.setText(String.valueOf(newValue.intValue()) + " Óra");
                        }
                        
                    });
                    cyclicSlider.setDisable(false);
                
                }
            }
        };
        cyclicCheckBox.setOnAction(eh);
       
    }
    
    @FXML
    private void initSchedulePlanner(){
        
        startHourSpinner.setValueFactory(new HourSpinner().getHourSpinner());
        endHourSpinner.setValueFactory(new HourSpinner().getHourSpinner());
        startMinSpinner.setValueFactory(new MinuteSpinner().getMinSpinner());
        endMinSpinner.setValueFactory(new MinuteSpinner().getMinSpinner());
        dateSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        YearAndMonthSpinner yms = new YearAndMonthSpinner();
        DaySpinner ds = new DaySpinner();
        dateSpinner.setValueFactory(yms.getYearAndMonthSpinner());
        AgendaSkinSwitcher sk = new AgendaSkinSwitcher(scheduleAgenda);
        scheduleAgenda.setAllowDragging(false);
        scheduleAgenda.setAllowResize(false);
        scheduleAgenda.setDisplayedLocalDateTime(LocalDateTime.now());
        scheduleAgenda.getSkin().getSkinnable().skinProperty().addListener(new ChangeListener<Skin<?>>(){
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                if(newValue.getClass().equals(AgendaDaySkin.class)){                 
                    dateSpinner.setValueFactory(ds.getDaySpinner());                                 
                }else{ 
                    dateSpinner.setValueFactory(yms.getYearAndMonthSpinner());                   
                }
            }
            
        });
        
        sk.setLayoutX(925);
        sk.setLayoutY(25);
        schedulePlanningPage.getChildren().add(sk);
        dateSpinner.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(scheduleAgenda.getSkin().getClass().equals(AgendaWeekSkin.class)){
                    scheduleAgenda.setDisplayedLocalDateTime(yms.getFirstDayOfWeek());
                }else{
                    scheduleAgenda.setDisplayedLocalDateTime(ds.getChangedDay());
                }
                
            }
        });
        
        isCyclic();
        initRouteTableForSchedule();
        splitPane.setVisible(false);
        splitPane.setDisable(true);
        schedulePlanningPage.setVisible(true);
        schedulePlanningPage.setDisable(false);
        
    }
    
    private void initRouteTableForSchedule(){
        System.out.println("initRouteTableForSchedule");
        if(routeTableForSchedule.getColumns().isEmpty()){
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
            TableColumn stopCount = new TableColumn("Megállószám");
            stopCount.setCellValueFactory(new PropertyValueFactory<RouteTableView,Integer>("numberCount"));
            stopCount.setMinWidth(125);
            routeTableForSchedule.getColumns().addAll(nameCol,firstStopCol,lastStopCol,distanceCol,stopCount);
        }
        routeTableForSchedule.setItems(allRouteTable.getItems());
        
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
    public void exitWithoutSaving(ActionEvent event){   
        setSchedulePlannerWindowToDefault();
    }
    
    private void setSchedulePlannerWindowToDefault() {
        splitPane.setVisible(true);
        splitPane.setDisable(false);
        schedulePlanningPage.setVisible(false);
        schedulePlanningPage.setDisable(true);
        datePicker.getEditor().setText("");
        routeTableForSchedule.getSelectionModel().select(null);
        scheduleAgenda.appointments().clear();
        scheduleAgenda.setSkin(new AgendaWeekSkin(scheduleAgenda));
        scheduleAgenda.setDisplayedLocalDateTime(LocalDateTime.now());
        scheduleNameTextField.setText("");
        changeScheduleName = null;
        
    }
    @FXML
    public void finishAddingRoutes(ActionEvent event){
        if(routeNameTextField != null 
                && !"".equals(routeNameTextField.getText().trim()) 
                && routeTable.getItems().size() >= 1){
            Route route = new Route();
            route.setRouteName(routeNameTextField.getText());
            DATA_CONTROLLER.updateOrInsertRoute(routeId, route);
            ArrayList<StopDistance> routeArrayList = new ArrayList<> (routeTable.getItems());
            DATA_CONTROLLER.updateOrInsertStopDistances(routeId, routeArrayList);
            double dist = 0;
            ArrayList<RouteDestinations> routeDestinationsList = new ArrayList<>();
            for(StopDistance sd : routeArrayList){
                RouteDestinations routeDestinations = new RouteDestinations();
                routeDestinations.setId(route.getId());
                routeDestinations.setStopDistanceId(sd.getId());
                routeDestinations.setNumber(sd.getNumber());
                routeDestinationsList.add(routeDestinations);
                dist += sd.getDistance();
            }
            DATA_CONTROLLER.updateOrInsertRouteDest(routeId, routeDestinationsList);
            RouteTableView routeTableView = new RouteTableView();
            routeTableView.setRouteId(route.getId());
            routeTableView.setRouteName(route.getRouteName());
            routeTableView.setStartName(routeArrayList.get(0).getSelectedStartName());
            routeTableView.setEndName(routeArrayList.get(routeArrayList.size()-1).getSelectedStopName());
            routeTableView.setDistanceSum(dist);
            int numOfStops = 1;
            for(int i = 1; i < routeArrayList.size(); i++){
                StopDistance prev = routeArrayList.get(i-1);
                StopDistance selected = routeArrayList.get(i);
                if(!prev.getSelectedStartName().equals(selected.getSelectedStartName()) 
                        || !prev.getSelectedStopName().equals(selected.getSelectedStopName())){
                    numOfStops++;    
                }
            }
            routeTableView.setNumberCount(numOfStops);
            DATA_CONTROLLER.updateOrInsertRouteTableView(routeTableView, routeId);
            routeId = -1;
            allRouteTable.setItems(FXCollections.observableList(DATA_CONTROLLER.listRouteTableViews()));
            setDialogeWindowToDefault();
        }else{
            if(routeNameTextField == null || "".equals(routeNameTextField.getText().trim())){
                ROUTE_MESSAGE.emptyRouteNameError();
            }else{
                ROUTE_MESSAGE.emptyStopTableListError();
            }
        }
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
        ObservableList<StopDistance> stopDistanceList = (ObservableList<StopDistance>) routeTable.getItems();
        String selectedStartName = selectedStartLabel.getText();
        String selectedEndName = selectedStopLabel.getText();
        if(stopDistanceList.size() >= 1 ){
            StopDistance prev = (StopDistance)routeTable.getItems().get(routeTable.getItems().size()-1);
            if(!prev.getSelectedStopName().equals(selectedStartName)
                    && (!prev.getSelectedStopName().equals(selectedEndName)
                    || !prev.getSelectedStartName().equals(selectedStartName)) ){
                ROUTE_MESSAGE.notConnectedStopDistancesError();
                return;
            }
        }
        try{
            Double distanceDouble = Double.parseDouble(distanceTextField.getText().replace(",", "."));
            if(!"".equals(distanceTextField.getText()) && distanceDouble > 0
                    && !"Selected start".equals(selectedStartLabel.getText()) && !"Selected stop".equals(selectedStopLabel.getText())
                    &&  !"null".equals(selectedFromChoiceBox) && !selectedStartName.equals(selectedEndName) ){
                StopDistance sd = new StopDistance();
                sd.setNumber(routeTable.getItems().size()+1);
                sd.setSelectedStartName(selectedStartName);
                sd.setSelectedStopName(selectedEndName);
                sd.setDistance(distanceDouble);
                sd.setRoadName(selectedFromChoiceBox);
                for(Stop stopDistance : (ObservableList<Stop>)stopList.getItems()){
                    if(stopDistance.getName() == null ? sd.getSelectedStartName() == null : stopDistance.getName().equals(sd.getSelectedStartName())){
                        sd.setStartId(stopDistance.getId());
                    }else if(stopDistance.getName() == null ? sd.getSelectedStopName() == null : stopDistance.getName().equals(sd.getSelectedStopName())){
                        sd.setEndId(stopDistance.getId());
                    }
                }
                ArrayList<RoadType> roadTypes = DATA_CONTROLLER.readRoadTypes();
                for (RoadType rd : roadTypes) {
                    if(rd.getRoadType().equals(selectedFromChoiceBox)){
                        sd.setRoadId(rd.getRoadId());
                    }
                }
                routeTable.getItems().add(sd);
            }else if(distanceTextField.getText().equals("")){
                ROUTE_MESSAGE.emptyRouteDistanceError();
            }else if(distanceDouble <= 0){
                ROUTE_MESSAGE.routeDistanceLessThanZero();
            }else if("Selected start".equals(selectedStartLabel.getText())){
                ROUTE_MESSAGE.startIsNotSetError();
            }else if("Selected stop".equals(selectedEndName)){
                ROUTE_MESSAGE.stopIsNotSetError();
            }else{
                ROUTE_MESSAGE.startEqualsStopError();
            }
        }catch(NumberFormatException ex){
            ROUTE_MESSAGE.routeDistanceFormatError();
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
    @FXML
    public void addTimeToSchedule(ActionEvent event){
        if(datePicker.getValue() != null){
            LocalDateTime startTime;
            startTime = datePicker.getValue().atTime(LocalTime.of(Integer.parseInt(startHourSpinner.getEditor().getText()), Integer.parseInt(startMinSpinner.getEditor().getText())));
            LocalDateTime endTime;
            endTime = datePicker.getValue().atTime(LocalTime.of(Integer.parseInt(endHourSpinner.getEditor().getText()), Integer.parseInt(endMinSpinner.getEditor().getText())));
            if(!datePicker.getEditor().getText().trim().equals("") 
                    && routeTableForSchedule.getSelectionModel().getSelectedItem() != null
                    && !startTime.equals(endTime)){
                int randomNumber = (int) (Math.random() * 20);
                String groupString = "group" + randomNumber;
                if(endTime.isBefore(startTime)){
                    endTime = endTime.plusDays(1);
                }

                if(cyclicCheckBox.isSelected())
                {
                    LocalDateTime difTime = endTime.minusMinutes(startTime.getHour()*60 + startTime.getMinute());
                    int difference = difTime.getMinute() + difTime.getHour() * 60;
                    LocalDateTime cycleLastTime = startTime.plusHours((int)cyclicSlider.getValue());
                    if((cycleLastTime.getDayOfMonth()-1) == startTime.getDayOfMonth()){
                        System.out.println("nem megfelelő ütemes");
                    }else{
                        int begin = startTime.getHour() * 60 + startTime.getMinute();
                        int end = cycleLastTime.getHour() * 60 + cycleLastTime.getMinute();
                        LocalTime cycle = (LocalTime)cycleSpinner.getValue();
                        LocalDate firstDayOfWeek = datePicker.getValue().with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
                        LocalDate lastDayOfWeek = datePicker.getValue().with(DayOfWeek.of(((WeekFields.of(Locale.getDefault()).getFirstDayOfWeek().getValue() + 5) % DayOfWeek.values().length) + 1));
                        int interval = cycle.getHour() * 60 + cycle.getMinute();
                        int first = firstDayOfWeek.getDayOfMonth();
                        int last = lastDayOfWeek.getDayOfMonth();
                        ArrayList<Appointment> appointmentList = new ArrayList<>();
                        for(int day = first; day <= last; day++){
                            for (int time = begin; time <= end; time += interval) {
                                AppointmentImplLocal appoinment = new Agenda.AppointmentImplLocal()
                                        .withStartLocalDateTime(startTime.withHour(time / 60).withMinute(time % 60).withDayOfMonth(day))
                                        .withEndLocalDateTime(startTime.withHour(time / 60).withMinute(time % 60).plusMinutes(difference).withDayOfMonth(day))
                                        .withSummary(((RouteTableView)routeTableForSchedule.getSelectionModel().getSelectedItem()).getRouteName())
                                        .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(groupString));
                                appointmentList.add(appoinment);
                            }
                        }
                        scheduleAgenda.appointments().addAll(appointmentList);
                    }
                }else{

                    scheduleAgenda.appointments().addAll(
                            new Agenda.AppointmentImplLocal()
                                    .withStartLocalDateTime(startTime)
                                    .withEndLocalDateTime(endTime)
                                    .withSummary(((RouteTableView)routeTableForSchedule.getSelectionModel().getSelectedItem()).getRouteName())
                                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(groupString))
                    );
                }

            }
        }
    }
    @FXML
    public void finishSchedule(ActionEvent event){
        if(!scheduleNameTextField.getText().trim().equals("")){
            Schedule sched = DATA_CONTROLLER.getScheduleByName(scheduleNameTextField.getText().trim());
            if(sched == null ||  changeScheduleName.equals(sched.getScheduleName())){
                ObservableList<Appointment> obs = scheduleAgenda.appointments();
                ObservableList<RouteTableView> rtw = routeTableForSchedule.getItems();
                ArrayList<Schedule> schedules = new ArrayList<>();
                for(Appointment app : obs){
                    sched = new Schedule();
                    sched.setScheduleName(scheduleNameTextField.getText().trim());
                    sched.setStartDate(app.getStartLocalDateTime());
                    sched.setEndTime(app.getEndLocalDateTime());
                    sched.setGroup(app.getAppointmentGroup().getStyleClass());
                    for(RouteTableView r : rtw){
                        if(r.getRouteName().equals(app.getSummary())){
                            sched.setRouteId(r.getRouteId());
                            schedules.add(sched); 
                            break;
                        }
                    }   
                }
                DATA_CONTROLLER.updateOrInsertScheduleAsList(changeScheduleName, schedules);
                changeScheduleName = null;
                scheduleList.setItems(FXCollections.observableList(new ArrayList<>(DATA_CONTROLLER.listSchedulesOnlyOnce())));
                setSchedulePlannerWindowToDefault();
                datePicker.getEditor().setText("");
                routeTableForSchedule.getSelectionModel().select(null);
                scheduleAgenda.appointments().clear();
                scheduleAgenda.setSkin(new AgendaWeekSkin(scheduleAgenda));
                scheduleAgenda.setDisplayedLocalDateTime(LocalDateTime.now());
                scheduleNameTextField.setText("");
            }else{
                SCHEDULE_MESSAGE.scheduleAlreadyExists();
            }
        }
        
    }
    @FXML
    public void deleteSchedule(ActionEvent event){
        Schedule selected = (Schedule)(scheduleList.getSelectionModel().getSelectedItem());
        if(selected != null){
            DATA_CONTROLLER.deleteScheduleByName(selected.getScheduleName());
            scheduleList.setItems(FXCollections.observableList(new ArrayList<>(DATA_CONTROLLER.listSchedulesOnlyOnce())));
        }
    }
    @FXML
    public void changeSchedule(ActionEvent event){
        Schedule selected = (Schedule)(scheduleList.getSelectionModel().getSelectedItem());
        if(selected != null){
            ArrayList<Schedule> sList = DATA_CONTROLLER.changeScheduleByName(selected.getScheduleName());
            initSchedulePlanner();
            scheduleNameTextField.setText(selected.getScheduleName());
            ArrayList<RouteTableView> rtwList = new ArrayList<>(routeTableForSchedule.getItems());
            ArrayList<Appointment> appList = new ArrayList<>();
            for(Schedule s : sList){
                RouteTableView r;
                for(RouteTableView rtw : rtwList){
                    if(rtw.getRouteId() == s.getRouteId()){
                        r = rtw;
                        appList.add(new Agenda.AppointmentImplLocal()
                                .withStartLocalDateTime(s.getStartDate())
                                .withEndLocalDateTime(s.getEndTime())
                                .withSummary(r.getRouteName())
                                .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(s.getGroup()))
                        );
                        break;
                    }
                }    
            }
            scheduleAgenda.appointments().addAll(appList);
            changeScheduleName = selected.getScheduleName();
        }
    }
    
    
}
