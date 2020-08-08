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
import menetrendtervezo.database.DataBase;
import menetrendtervezo.route.RoadType;
import menetrendtervezo.route.Stop;
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
}
