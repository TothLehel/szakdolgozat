/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.datacontroller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import menetrendtervezo.database.DataBase;
import menetrendtervezo.entity.Driver;
import menetrendtervezo.entity.Schedule;
import menetrendtervezo.entity.Vehicle;
import menetrendtervezo.route.RoadType;
import menetrendtervezo.route.Route;
import menetrendtervezo.route.RouteDestinations;
import menetrendtervezo.route.RouteTableView;
import menetrendtervezo.route.Stop;
import menetrendtervezo.route.StopDistance;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author tlehe
 */
public class DataController {
    private DataBase db = DataBase.getInstance();
    
    public ArrayList<Stop> readStops(){
        ResultSet rs = db.listTableResultSet("stops");
        ArrayList<Stop> stops = new ArrayList<>();
        try {
            while(rs.next()){ 
                stops.add(new Stop(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stops;
    }
    public void createDriver(Row row) throws SQLIntegrityConstraintViolationException{
        db.createDriver(row);
    }
    public void deleteSQLTable(String tableName){
        db.deleteSQLTable(tableName);
    }
    public void createVehicle(Row row) throws SQLIntegrityConstraintViolationException{
        db.createVehicle(row);
    }
    public void createStop(Row row){
        db.createStop(row);
    }
    public ArrayList<RoadType> readRoadTypes(){
        ResultSet rs = db.listTableResultSet("road_types");
        ArrayList<RoadType> roadTypes = new ArrayList<>();
        try {
            while(rs.next()){ 
                roadTypes.add(new RoadType(rs.getString(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return roadTypes;
    }
    public void addStopDistance(StopDistance stopDistance){
        db.addStopDistance(stopDistance);
    }
    
    public void addRoute(Route route){
        db.addRoute(route);
    }
    public void addRouteDestination(RouteDestinations routeDest){
        db.addRouteDestination(routeDest);
    }
    public void addRouteTableView(RouteTableView rtv){
        db.addRouteTableView(rtv);
    }
    public void changeRoute(Route route){
        db.changeRoute(route);
    }
    public ArrayList<RouteTableView> listRouteTableViews()  {
        ResultSet rs = db.listTableResultSet("route_table_view");
        ArrayList<RouteTableView> rtv = new ArrayList<>();
        try {
            while(rs.next()){ 
                rtv.add(new RouteTableView(rs.getInt("route_id"), rs.getString("route_name"), rs.getDouble("distance"), rs.getInt("num_of_stops"), rs.getString("start_name"),rs.getString("end_name")));   
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rtv;
    }

    public void removeRouteById(int routeId) {
        db.removeRouteById(routeId);
    }

    public void removeRouteDestinationsByRouteId(int routeId) {
        db.removeRouteDestinationsByRouteId(routeId);
    }

    
    public ObservableList getStopDistancesByRouteId(int routeId) {
        return db.getStopDistancesByRouteId(routeId);
    }

    public void deleteRouteDestinationById(int id) {
        db.deleteRouteDestinationById(id);
    }

    public void deleteStopDistancesByRouteId(int routeId) {
        db.deleteStopDistancesByRouteId(routeId);
    }

    public void deleteRouteTableViewByRouteId(int routeId) {
        db.deleteRouteTableViewByRouteId(routeId);
    }

    public Schedule getScheduleByName(String text) {
        return db.getScheduleByName(text);
    }

    public void addScheduleAsList(ArrayList<Schedule> scheduleList) {
        db.addScheduleAsList(scheduleList);
    }

    public ArrayList<Schedule> listSchedules(){
        return db.listSchedules();
    }

    public void deleteScheduleByName(String scheduleName) {
        db.deleteScheduleByName(scheduleName);
    }

    public ArrayList<Schedule> changeScheduleByName(String scheduleName) {
        return db.changeScheduleByName(scheduleName);
    }
    public Set<Schedule> listSchedulesOnlyOnce(){
        return db.listSchedulesOnlyOnce();
    }

    public void updateScheduleAsList(ArrayList<Schedule> schedules) {
        db.updateScheduleAsList(schedules);
    }
    public ArrayList<Driver> getDriversBetweenDates(LocalDateTime from, LocalDateTime to){
        return db.getDriversBetweenDates(from, to);
    }
    public ArrayList<Vehicle> getVehcilesBetweenDates(LocalDateTime from, LocalDateTime to, int scheduleId){
        return db.getVehcilesBetweenDates(from, to, scheduleId);
    }

    public void updateScheduleWithDriverAndVechicle(int selectedSchedule, Driver selectedDriver, Vehicle selectedVehicle) {
        db.updateScheduleWithDriverAndVechicle(selectedSchedule, selectedDriver, selectedVehicle);
    }

    public Driver getDriverById(int driverId) {
        return db.getDriverById(driverId);
    }

    public Vehicle getVehicleByLicencePlate(String licensePlate) {
        return db.getVehicleByLicencePlate(licensePlate);
    }

    public void deleteScheduleDriverAndVehcile(int scheduleId) {
        db.deleteScheduleDriverAndVehcile(scheduleId);
    }
    public void listDrivers(){
        db.listDrivers();
    }
    public void updateOrInsertStopDistances(int routeId, ArrayList<StopDistance> stopDistances){
        db.updateOrInsertStopDistances(routeId, stopDistances);
    }

    public void updateOrInsertRouteDest(int routeId, ArrayList<RouteDestinations> routeArrayList) {
        db.updateOrInsertRouteDest(routeId, routeArrayList);
    }

    public void updateOrInsertRouteTableView(RouteTableView routeTableView, int routeId) {
        db.updateOrInsertRouteTableView(routeTableView, routeId);
    }
    public void updateOrInsertRoute(int routeId, Route route){
        db.updateOrInsertRoute(routeId, route);
    }

    public void updateOrInsertScheduleAsList(String changeScheduleName, ArrayList<Schedule> schedules) {
        db.updateOrInsertScheduleAsList(changeScheduleName,  schedules);
    }

    public boolean isDriverTableEmpty() {
        return db.isDriverTableEmpty();
    }

    public boolean isVehicleTableEmpty() {
        return db.isVehicleTableEmpty();
    }

    public boolean isScheduleContainsRoad(int routeId) {
        return db.isScheduleContainsRoad(routeId);
    }

    public boolean isScheduleBound(String scheduleName) {
        return db.isScheduleBound(scheduleName);
    }
    
            
}
