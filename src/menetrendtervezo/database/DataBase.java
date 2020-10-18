/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;
import menetrendtervezo.entity.Driver;
import menetrendtervezo.entity.Schedule;
import menetrendtervezo.error.InputError;
import menetrendtervezo.route.RouteDestinations;
import menetrendtervezo.entity.Vehicle;
import menetrendtervezo.route.Route;
import menetrendtervezo.route.RouteTableView;
import menetrendtervezo.route.Stop;
import menetrendtervezo.route.StopDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author tlehe
 */
public class DataBase {
    final String JDBC_SERVER = "org.apache.jdbc.EmbeddedDriver";
    final String URL = "jdbc:derby:adatbazis;create=true";
    private Connection conn;
    private Statement createStatement;
    private DatabaseMetaData dbmd = null;
    private static DataBase single_instance = null;
    
    private final InputError INPUT_ERROR = new InputError();
    
    private ArrayList<Driver> drivers = new ArrayList<>();
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
   
    private DataBase() {
        try {
            conn = DriverManager.getConnection(URL);
            if(conn != null){
                createStatement = conn.createStatement();
                dbmd = conn.getMetaData();
                String[] sqlOrders = readSQLFile("src\\menetrendtervezo\\database\\tablak.sql");
                createTables(sqlOrders);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static DataBase getInstance(){
        if(single_instance == null){
            single_instance = new DataBase();
        }
        return single_instance;
    }
    
    private String[] readSQLFile(String src){
        File sqlFile = new File(src);
        Scanner scanner;
        String fileString;
        String[] sqlOrd = null;
        try {
            scanner = new Scanner(sqlFile);
            scanner.useDelimiter("\\Z");
            fileString = scanner.next();
            scanner.close();
            fileString = fileString.replace("[", "");
            fileString = fileString.replace("]", "");
            sqlOrd = fileString.split("GO");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("a(z)"+src +" sql file nem található!");
        }
        return sqlOrd;
    }
    private void createTables(String[] sqlOrders){
        ArrayList<Integer> created = new ArrayList<>();
        for (String sqlOrder : sqlOrders) {
            int indexOfTableStart = sqlOrder.indexOf("(");
            int endIndexOfCreateTable = sqlOrder.lastIndexOf("CREATE TABLE") + 12;
            
            if (indexOfTableStart >= 0) {
                String tableName = sqlOrder.substring(endIndexOfCreateTable, indexOfTableStart).trim();
                created.add(createTable(sqlOrder, tableName));
            }
        }
        if(!created.contains(-1)){
            if(created.contains(1)){
                created.add(executeSQL("src\\menetrendtervezo\\database\\kapcsolatok.sql"));
            }else{
                System.out.println("minden tábla létezett az adatbázisban, így nem szükséges lefuttatni a kapcsolatokat");
            }if(!created.contains(-1) && created.contains(1)){
                created.add(executeSQL("src\\menetrendtervezo\\database\\alap_adatok.sql"));
                
            }else{
                System.out.println("alapadatok már benne vannak a táblákban!");
            }
            
        }else{
            System.out.println("nem sikerült az adatbázist létrehozni");
        }
    }
    private int createTable(String sqlOrder, String tableName){
        ResultSet rs; 
        try {
            rs = dbmd.getTables(null, "APP", tableName.toUpperCase() , null);
            if(!rs.next()){
                createStatement.execute(sqlOrder);
                System.out.println(tableName.toUpperCase() +" tábla létrehozása az adatbázisban");
                rs.close();
                return 1;
            }
            rs.close();
            return 0;
        } catch (SQLException ex) {
            System.out.println("hiba a(z) "+ tableName.toUpperCase()+ " table létrehozásakor: " + ex);
        }
        return -1;
    } 
    
    private int executeSQL(String src){
        String[] sqlOrders = readSQLFile(src);
        for(String sqlOrder : sqlOrders){
            try {
                createStatement.execute(sqlOrder);
            
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("A(z) " + sqlOrder +". SQL parancs lefuttatása nem sikerült a(z) " + src +" fájlban! ex: " + ex );
                return -1;
            }
        }
        System.out.println(src + " sql sikeresen lefuttatva!");
        return 0;
    }
    public void createVehicle(Row row){
        Workbook workbook = row.getSheet().getWorkbook();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Iterator<Cell> cellIt = row.iterator();
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement("INSERT INTO vehicles VALUES(?,?,?)");
            while(cellIt.hasNext()){
                Cell cell = row.getCell(cellIt.next().getColumnIndex(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                CellValue value = evaluator.evaluate(cell);
                if(value != null && value.getCellType() == CellType.STRING){
                    switch (cell.getColumnIndex()) {
                        case 1:
                            if(!itemAlreadyExists(value.getStringValue(), "vehicles")){
                                ps.setString(1, value.getStringValue());
                            }else{
                                System.out.println("A listában már létezik az adott kulcs");
                                return;
                            }
                            break;
                        case 0:
                            ps.setString(2, value.getStringValue());
                            break;
                        case 2:
                            ResultSet rs = null;
                            switch(cell.getStringCellValue()){
                                case "mb":
                                    rs = createStatement.executeQuery("SELECT type FROM vehicle_type_limits WHERE type='mb'");
                                    break;
                                case "b":
                                    rs = createStatement.executeQuery("SELECT type FROM vehicle_type_limits WHERE type='b'");
                                    break;
                                case "tb":
                                    rs = createStatement.executeQuery("SELECT type FROM vehicle_type_limits WHERE type='tb'");
                                    break;
                                default: 
                                    return;
                            }   while(rs.next()){
                                ps.setString(3, rs.getString(1));                       
                            }   break;
                        default:
                            break;
                    }
                }else{
                    return;
                }
            }
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void createStop(Row row){
        Workbook workbook = row.getSheet().getWorkbook();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Iterator<Cell> cellIt = row.iterator();
        try{
            PreparedStatement ps;
            ps = conn.prepareStatement("INSERT INTO stops (name, capacity) VALUES (?,?)");
            while(cellIt.hasNext()){
                Cell cell = row.getCell(cellIt.next().getColumnIndex(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                CellValue value = evaluator.evaluate(cell);
                if(value != null){
                    switch(cell.getColumnIndex()){
                        case 0:
                            if(value.getCellType() == CellType.STRING){
                                ps.setString(1, cell.getStringCellValue());
                            }
                            break;
                        case 1:
                            if(value.getCellType() == CellType.NUMERIC){
                                ps.setInt(2, (int)cell.getNumericCellValue());
                            }
                            break;
                        default:
                            break;
                    }
                }else{
                    return;
                }
            }
            ps.execute();
            ps.close();
        }catch(SQLException ex){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void createDriver(Row row){
        Workbook workbook = row.getSheet().getWorkbook();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Iterator<Cell> cellIt = row.iterator();
        try{
            PreparedStatement ps, dateStatement;
            ps = conn.prepareStatement("INSERT INTO drivers VALUES (?,?,?)");
            dateStatement = conn.prepareStatement("INSERT INTO dates VALUES (?,?,?,?,?)");
//            int cellIndex = 0;
            Cell firstName = evaluator.evaluateInCell(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            Cell lastName = evaluator.evaluateInCell(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            Cell id = evaluator.evaluateInCell(row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            if(id != null){
                int idInInt = (int)id.getNumericCellValue();
                if(!itemAlreadyExists(idInInt, "drivers")){
                    ps.setInt(1,idInInt);
                    ps.setString(2, firstName.getStringCellValue());
                    ps.setString(3, lastName.getStringCellValue());
                    ps.execute();
                    for(int i = 3; i <= row.getLastCellNum(); i+=2){
                        Cell start = evaluator.evaluateInCell(row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                        if(start != null){
                            Row firstRow = row.getSheet().getRow(0);
                            Cell date = evaluator.evaluateInCell(firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                            Cell end = evaluator.evaluateInCell(row.getCell(start.getColumnIndex() + 1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                            if(!itemAlreadyExists(idInInt, date.getDateCellValue(), "dates")){
                                dateStatement.setInt(1,idInInt);
                                java.sql.Date sqlDate = new java.sql.Date(date.getDateCellValue().getTime());
                                dateStatement.setDate(2,sqlDate);
                                if(start.getCellType() == CellType.NUMERIC){
                                    Calendar c = Calendar.getInstance();
                                    Calendar startCalendar = Calendar.getInstance();
                                    Calendar endCalendar = Calendar.getInstance();
                                    startCalendar.setTime(start.getDateCellValue());
                                    c.setTime(date.getDateCellValue());
                                    endCalendar.setTime(end.getDateCellValue());
                                    
                                    c.add(Calendar.HOUR_OF_DAY,startCalendar.get(Calendar.HOUR_OF_DAY));
                                    java.sql.Timestamp sqlStart = new java.sql.Timestamp(c.getTimeInMillis());
                                    int dif = (int)endCalendar.getTimeInMillis() - (int)startCalendar.getTimeInMillis();
                                    if(dif > 0 ){
                                        c.add(Calendar.MILLISECOND, dif);
                                    }else{
                                        c.add(Calendar.MILLISECOND, dif);
                                        c.add(Calendar.DAY_OF_MONTH,1 );
                                    }
                                    java.sql.Timestamp sqlEnd = new java.sql.Timestamp(c.getTimeInMillis());
                                    dateStatement.setTimestamp(3, sqlStart);
                                    dateStatement.setTimestamp(4, sqlEnd);
                                    java.sql.Time sqlDif = new java.sql.Time(dif- 3600000);//ide
                                    dateStatement.setTime(5, sqlDif);
                                    dateStatement.execute();
                                }else if(start.getCellType() == CellType.STRING){
                                    dateStatement.setNull(3, Types.TIMESTAMP);
                                    dateStatement.setNull(4, Types.TIMESTAMP);
                                    dateStatement.setNull(5, Types.TIME);
                                    dateStatement.execute();
                                }
                            }
                        }
                    }
                }
            }
        ps.close();
        dateStatement.close();
        }catch(SQLException ex){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void deleteSQLTable(String tableName){
        try {
            createStatement.execute("DELETE FROM "+ tableName +" WHERE 1=1");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("drivers törölve");
    }
    private Boolean itemAlreadyExists(String primaryKey, String table){
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);
            ps.execute();
            String pkColumnName = ps.getMetaData().getColumnName(1);
            ps = conn.prepareStatement("SELECT "+ pkColumnName +" FROM "+ table +" WHERE "+ pkColumnName +" = ?");
            ps.setString(1, primaryKey);
            ResultSet rs = ps.executeQuery();
            
            if(!rs.next()){
                return false;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    private Boolean itemAlreadyExists(int primaryKey, String table){
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);
            ps.execute();
            String pkColumnName = ps.getMetaData().getColumnName(1);
            ps = conn.prepareStatement("SELECT "+ pkColumnName +" FROM "+ table +" WHERE "+ pkColumnName +" = ?");
            ps.setInt(1, primaryKey);
            ResultSet rs = ps.executeQuery();
            
            if(!rs.next()){
                return false;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    private Boolean itemAlreadyExists(int partialKey1,int partialKey2, String table){ //NEM TESZTELT
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);
            ps.execute();
            String firstColumnName = ps.getMetaData().getColumnName(1);
            String secondColumnName = ps.getMetaData().getColumnName(2);
            ps = conn.prepareStatement("SELECT "+ firstColumnName +", "+ secondColumnName + "FROM "+ table +" WHERE "+ firstColumnName +" = ? AND " + secondColumnName+ " =?" );
            ps.setInt(1,partialKey1);
            ps.setInt(2, partialKey2);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    private Boolean itemAlreadyExists(int partialKey1,Date partialKey2, String table){ //NEM TESZTELT
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);
            ps.execute();
            String firstColumnName = ps.getMetaData().getColumnName(1);
            String secondColumnName = ps.getMetaData().getColumnName(2);
            ps = conn.prepareStatement("SELECT "+ firstColumnName +" FROM "+ table +" Where "+ firstColumnName +" = ? AND " + secondColumnName+ " = ?" );
            ps.setInt(1,partialKey1);
            java.sql.Date sqlDate = new java.sql.Date(partialKey2.getTime());
            ps.setDate(2, sqlDate);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                return false;
            }else{
                //System.out.println(rs.getString(1));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
   

    public ResultSet listTableResultSet(String tableName){
        try {
            ResultSet rs = createStatement.executeQuery("SELECT * FROM " + tableName);
            System.out.println("SELECT * FROM " + tableName);
            
            return rs;
        } catch (SQLException ex) {
            
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void listDrivers(){
        try {
            ResultSet rs = createStatement.executeQuery("SELECT * FROM drivers");
            ArrayList idList;
            idList = new ArrayList();
            while(rs.next()){
               idList.add(rs.getInt(1));
            }
            for(int i = 0;i<idList.size();i++){
                listDates((int)idList.get(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void listDates(int id){
        ResultSet rs = null;
        try {
            rs = createStatement.executeQuery("SELECT * FROM dates WHERE driver_id = " + id);
            while(rs.next()){
                System.out.println(rs.getString(1) + " " +rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void listVehicles(){
        try {
            ResultSet rs = createStatement.executeQuery("SELECT * FROM vehicles");
            while(rs.next()){
                System.out.println(rs.getString(1) + " " +rs.getString(2) + " " + rs.getString(3));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void listStops(){
        try {
            ResultSet rs = createStatement.executeQuery("SELECT * FROM stops");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " +rs.getString(2) + " " + rs.getInt(3));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void addRoute(Route route){
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement("INSERT INTO routes (route_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, route.getRouteName());
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    route.setId(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Creating ROUTES failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void changeRoute(Route route){
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement("UPDATE routes SET route_name = ? WHERE route_id = ? ", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, route.getRouteName());
            ps.setInt(2, route.getId());
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    public void addStopDistance(StopDistance sd){
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO stop_distances (start_stop_id, end_stop_id, distance, roadId) VALUES (?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, sd.getStartId());
            ps.setInt(2, sd.getEndId());
            ps.setDouble(3, sd.getDistance());
            ps.setString(4, sd.getRoadId());
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sd.setId(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Creating Stop_distance failed, no ID obtained.");
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void addRouteDestination(RouteDestinations routeDestination){
        
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO route_destinations VALUES (?,?,?) "); //Id miatt átírni az adatbázist
            ps.setInt(1, routeDestination.getId());
            ps.setInt(2, routeDestination.getNumber());
            ps.setInt(3, routeDestination.getStopDistanceId());
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addRouteTableView(RouteTableView rtv){
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO route_table_view VALUES (?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, rtv.getRouteId());
            ps.setString(2, rtv.getRouteName());
            ps.setString(3, rtv.getStartName());
            ps.setString(4, rtv.getEndName());
            ps.setDouble(5, rtv.getDistanceSum());
            ps.setInt(6, rtv.getNumberCount());
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    public void removeRouteById(int routeId) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM routes WHERE route_id = (?) ");
            ps.setInt(1, routeId);
            ps.executeUpdate();
            System.out.println("ROUTE DELETED");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void removeRouteDestinationsByRouteId(int routeId) {
        deleteRouteDestinationById(routeId);
        deleteStopDistancesByRouteId(routeId);
    }

    public void deleteRouteTableViewByRouteId(int routeId) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM route_table_view WHERE route_id = (?) ");
            ps.setInt(1, routeId);
            ps.executeUpdate();
            System.out.println("ROUTE TABLE VIEW DELETED");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList getStopDistancesByRouteId(int routeId) {
        
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM route_destinations WHERE route_id = (?) ");
            statement.setInt(1, routeId);
            ResultSet rs = statement.executeQuery();
            ArrayList<Integer> stopDistanceIds;
            stopDistanceIds = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            while(rs.next()){
                stopDistanceIds.add(rs.getInt(2)-1, rs.getInt(3));
                sql.append(" ?,");
            }
            String stg = "SELECT * FROM stop_distances WHERE id in (" + sql.deleteCharAt(sql.length()-1).toString() + ")";
            PreparedStatement ps = conn.prepareStatement(stg);
            for(int i = 0; i < stopDistanceIds.size(); i++){
                System.out.println(stopDistanceIds.get(i));
                ps.setInt(i+1, stopDistanceIds.get(i)); //nullpointer itt 
            }
            ResultSet stopDistances = ps.executeQuery();
            ArrayList<StopDistance> stopDistanceList = new ArrayList<>();
            ResultSet stopSet = listTableResultSet("stops");
            ArrayList<Stop> stopList = new ArrayList<>();
            while(stopSet.next()){
                stopList.add(new Stop(stopSet.getInt(1), stopSet.getString(2), stopSet.getInt(3)));
            }
            ResultSet roadSet = listTableResultSet("road_types");
            HashMap<String,String> roadNames= new HashMap<>();
            while(roadSet.next()){
                roadNames.put(roadSet.getString(1), roadSet.getString(2));
            }
            while(stopDistances.next()){
                StopDistance std = new StopDistance();
                std.setId(stopDistances.getInt(1));
                std.setStartId(stopDistances.getInt(2));
                std.setEndId(stopDistances.getInt(3));
                std.setDistance(stopDistances.getDouble(4));
                std.setRoadId(stopDistances.getString(5));
                std.setRoadName(roadNames.get(std.getRoadId()));
                for(Stop stop : stopList){
                    if(stop.getId() == std.getStartId()){
                        std.setSelectedStartName(stop.getName());
                    }
                    else if(stop.getId() == std.getEndId()){
                        std.setSelectedStopName(stop.getName());
                    }
                }
                
                for(int i = 0; i < stopDistanceIds.size(); i++){
                    int stopDist = stopDistanceIds.get(i);
                    if(stopDist == std.getId() ){
                        std.setNumber(i+1);
                        stopDistanceList.add(i, std); 
                
                    }
                }
            }
            return FXCollections.observableList(stopDistanceList);
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void deleteRouteDestinationById(int id) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM route_destinations WHERE route_id = (?) ");
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("ROUTE DESTINATIONS DELETED");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void deleteStopDistancesByRouteId(int routeId) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM stop_distances WHERE id = (SELECT stop_distance_id FROM route_destinations WHERE route_id = (?))");
            ps.setInt(1, routeId);
            ps.executeUpdate();
            System.out.println("STOP DISTANCES DELETED");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    public Schedule getScheduleByName(String text) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM schedule WHERE schedule_name = ?");
            ps.setString(1, text);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Schedule sch = new Schedule(
                        rs.getString("schedule_name"), rs.getInt("driver_id"), rs.getTimestamp("start_date").toLocalDateTime(),
                        rs.getTimestamp("end_date").toLocalDateTime(), rs.getString("license_plate"), rs.getInt("route_id"), rs.getString("app_group")
                );
                return sch;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void addScheduleAsList(ArrayList<Schedule> scheduleList) {   
        try {          
            PreparedStatement ps = conn.prepareStatement("INSERT INTO schedule(schedule_name, start_date, end_date,route_id, app_group) VALUES ( ?, ?, ?, ?, ?)");
            Iterator<Schedule> it = scheduleList.iterator();
            while(it.hasNext()){
                Schedule s = it.next();
                ps.setString(1, s.getScheduleName());
                ps.setTimestamp(2, Timestamp.valueOf(s.getStartDate()));
                ps.setTimestamp(3, Timestamp.valueOf(s.getEndTime()));
                ps.setInt(4, s.getRouteId());
                System.out.println(s.getRouteId());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("SCHEDULE INSERTED");
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}

