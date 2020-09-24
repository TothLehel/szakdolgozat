/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.datacontroller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import menetrendtervezo.database.DataBase;
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
    public void createDriver(Row row){
        db.createDriver(row);
    }
    public void deleteSQLTable(String tableName){
        db.deleteSQLTable(tableName);
    }
    public void createVehicle(Row row){
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


}
