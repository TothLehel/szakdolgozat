/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Skin;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import jfxtras.internal.scene.control.skin.agenda.AgendaDaySkin;
import jfxtras.internal.scene.control.skin.agenda.AgendaWeekSkin;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import menetrendtervezo.datacontroller.DataController;
import menetrendtervezo.entity.Driver;
import menetrendtervezo.entity.Schedule;
import menetrendtervezo.entity.Vehicle;
import menetrendtervezo.route.RouteTableView;
import menetrendtervezo.spinnerfactory.DaySpinner;
import menetrendtervezo.spinnerfactory.YearAndMonthSpinner;

/**
 * FXML Controller class
 *
 * @author tlehe
 */
public class BeosztasTabController implements Initializable {

    @FXML
    ChoiceBox scheduleSelector;
    
    @FXML
    Spinner dateSpinner;
    
    @FXML
    Agenda scheduleAgenda;
    
    @FXML
    TableView driverList, busList;
    
    @FXML
    AnchorPane assignmentPage;
    
    private Collection<Schedule> schedList;
    
    private HashMap<Appointment,Schedule> scheduleMap;
    
    private HashMap<Integer, RouteTableView> routeTableMap; 
    
    final DataController DATA_CONTROLLER = new DataController();
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
    }

    public void init(ObservableList obs){
        scheduleMap = new HashMap<>();
        routeTableMap = new HashMap<>();
        refreshScheduleList(obs);
        initDateSpinner();
        loadRouteTableView();
        initDriverTableView();
        initAppointments();
        initVehicleTableView();
        initAppointmentSelector();
        
    }
    public void refreshScheduleList(ObservableList obs){
        scheduleSelector.getItems().clear();
        scheduleSelector.getItems().add("Válasszon menetrendet");
        ArrayList<Schedule> sch = new ArrayList<>(obs);
        for(Schedule s : sch){
            scheduleSelector.getItems().add(s.getScheduleName());
        }
        scheduleSelector.getSelectionModel().selectFirst();
        
    }
    public void initDateSpinner(){
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
        
        sk.setLayoutX(420);
        sk.setLayoutY(115);
        assignmentPage.getChildren().add(sk);
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
    }
    public void initAppointments(){
       
        scheduleSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                scheduleAgenda.appointments().clear();
                if(newValue != null && !newValue.equals("Válasszon menetrendet")){
                    refreshAppointmentList();
                }else{
                    busList.getItems().clear();
                    driverList.getItems().clear();
                }
            
            }
                
        });
    }
                
    private void refreshAppointmentList(){
        scheduleAgenda.appointments().clear();
        ArrayList<Schedule> sList = DATA_CONTROLLER.changeScheduleByName((String)scheduleSelector.getSelectionModel().getSelectedItem());
        ArrayList<Agenda.Appointment> appList = new ArrayList<>();
        for(Schedule s : sList){
            String driverName = " ";
            String vehicleName = " ";
            if(s.getDriverId() != 0){
                Driver driver = DATA_CONTROLLER.getDriverById(s.getDriverId());
                driverName = driverName + "| " +driver.getName();
                
            }
            if(s.getLicensePlate() != null){
                Vehicle vehicle = DATA_CONTROLLER.getVehicleByLicencePlate(s.getLicensePlate());
                vehicleName = vehicleName + "| " + vehicle.getName() + " | " + vehicle.getType();
                
            }
            RouteTableView rtw = routeTableMap.get(s.getRouteId());
            if(rtw != null){
                Agenda.AppointmentImplLocal app = new Agenda.AppointmentImplLocal();
                app.withStartLocalDateTime(s.getStartDate());
                app.withEndLocalDateTime(s.getEndTime());
                app.withSummary(rtw.getRouteName() + driverName + vehicleName);
                app.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(s.getGroup()));
                appList.add(app);
                scheduleMap.put(app, s);
            }
            
        }
        scheduleAgenda.appointments().addAll(appList);
    
    } 

                
    public void initAppointmentSelector(){
        scheduleAgenda.selectedAppointments().addListener(new ListChangeListener< Appointment >() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Appointment> c) {
                if(!scheduleAgenda.selectedAppointments().isEmpty()){
                    for(Appointment a : scheduleAgenda.selectedAppointments()){
                        LocalDateTime from = a.getStartLocalDateTime();
                        LocalDateTime to = a.getEndLocalDateTime();
                        Schedule s = scheduleMap.get(a);
                        ArrayList<Driver> dList = DATA_CONTROLLER.getDriversBetweenDates(from, to,s.getScheduleId());
                        ArrayList<Vehicle> vList = DATA_CONTROLLER.getVehcilesBetweenDates(from, to, s.getScheduleId());
                        driverList.setItems(FXCollections.observableArrayList(dList));
                        busList.setItems(FXCollections.observableArrayList(vList));
                    }
                }
            }

        });
    }
    private void initDriverTableView(){
        if(driverList.getColumns().isEmpty()){
            TableColumn name = new TableColumn("Sofőr neve");
            name.setMinWidth(160);
            name.setCellValueFactory(new PropertyValueFactory<Driver,String>("name"));
            name.setSortable(false);
            TableColumn id = new TableColumn("Id");
            id.setMinWidth(25);
            id.setMaxWidth(35);
            id.setCellValueFactory(new PropertyValueFactory<Driver,String>("id"));
            id.setSortable(false);
            driverList.getColumns().add(id);
            driverList.getColumns().add(name);
        }
    }
    private void initVehicleTableView(){
        if(busList.getColumns().isEmpty()){
            TableColumn name = new TableColumn("járat neve");
            name.setMinWidth(75);
            name.setCellValueFactory(new PropertyValueFactory<Vehicle,String>("name"));
            name.setSortable(false);
            TableColumn id = new TableColumn("rendszám");
            id.setMinWidth(75);
            id.setCellValueFactory(new PropertyValueFactory<Vehicle,String>("licencePlate"));
            id.setSortable(false);
            TableColumn type = new TableColumn("típus");
            type.setMinWidth(75);
            type.setCellValueFactory(new PropertyValueFactory<Vehicle,String>("type"));
            type.setSortable(false);
            busList.getColumns().add(id);
            busList.getColumns().add(name);
            busList.getColumns().add(type);
        }
    }
    public void clearLists(){
        driverList.getItems().clear();
        busList.getItems().clear();
    }
    
    private void loadRouteTableView(){
        routeTableMap.clear();
        ArrayList<RouteTableView> routeTable = DATA_CONTROLLER.listRouteTableViews();
        for(RouteTableView rtw : routeTable){
            routeTableMap.put(rtw.getRouteId(), rtw);
        }
    }
    
    @FXML
    public void addDriverAndVehicle(ActionEvent action){
        Vehicle selectedVehicle = (Vehicle)busList.getSelectionModel().getSelectedItem();
        Driver selectedDriver = (Driver)driverList.getSelectionModel().getSelectedItem();
        if(selectedDriver != null & selectedVehicle != null){
            ObservableList<Appointment> appList = scheduleAgenda.selectedAppointments();
            ArrayList<Appointment> temporaryAppList = new ArrayList<>();
            for(Appointment a : appList){
                Schedule s = scheduleMap.get(a);
                DATA_CONTROLLER.updateScheduleWithDriverAndVechicle(s.getScheduleId(), selectedDriver, selectedVehicle);
                Appointment temporaryApp = a;
                temporaryApp.setSummary(routeTableMap.get(s.getRouteId()).getRouteName()+" | "+ selectedDriver.getName() + " | " +selectedVehicle.getName() + " | " + selectedVehicle.getType());
                temporaryAppList.add(temporaryApp);
            }
            scheduleAgenda.appointments().removeAll(appList);
            scheduleAgenda.appointments().addAll(temporaryAppList);
        }
    }
    @FXML
    public void deleteDriverAndVehicle(ActionEvent action){
        ObservableList<Appointment> appList = scheduleAgenda.selectedAppointments();
        ArrayList<Appointment> temporaryAppList = new ArrayList<>();
        if(!appList.isEmpty()){
            for(Appointment a : appList){
                Schedule s = scheduleMap.get(a);
                if(a.getSummary().split("\\|").length > 1){
                    DATA_CONTROLLER.deleteScheduleDriverAndVehcile(s.getScheduleId());
                    Appointment temporaryApp = a;
                    temporaryApp.setSummary(routeTableMap.get(s.getRouteId()).getRouteName());
                    temporaryAppList.add(temporaryApp);
                }else{
                   temporaryAppList.add(a);
                }
            }
            scheduleAgenda.appointments().removeAll(appList);
            scheduleAgenda.appointments().addAll(temporaryAppList);
        }
    }
}
