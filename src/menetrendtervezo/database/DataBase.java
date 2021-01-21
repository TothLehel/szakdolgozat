/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfxtras.scene.control.LocalTimePicker;
import menetrendtervezo.entity.Driver;
import menetrendtervezo.entity.Schedule;
import menetrendtervezo.route.RouteDestinations;
import menetrendtervezo.entity.Vehicle;
import menetrendtervezo.route.Route;
import menetrendtervezo.route.RouteTableView;
import menetrendtervezo.route.Stop;
import menetrendtervezo.route.StopDistance;
import org.apache.derby.iapi.store.raw.RowLock;
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
    private final String URL = "jdbc:derby:adatbazis;create=true";
    private Connection conn;
    private Statement createStatement;
    private DatabaseMetaData dbmd = null;
    private static DataBase single_instance = null;
    
    private DataBase() {
        try {
            conn = DriverManager.getConnection(URL);
            if(conn != null){
                createStatement = conn.createStatement();
                dbmd = conn.getMetaData();
                String[] sqlOrders = readSQLFile("tablak.sql");
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
        InputStream sqlFile = DataBase.class.getResourceAsStream(src);
        Scanner scanner;
        String fileString;
        String[] sqlOrd = null;
        scanner = new Scanner(sqlFile);
        scanner.useDelimiter("\\Z");
        fileString = scanner.next();
        scanner.close();
        fileString = fileString.replace("[", "");
        fileString = fileString.replace("]", "");
        sqlOrd = fileString.split("GO");
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
                created.add(executeSQL("kapcsolatok.sql"));
            }else{
                Logger.getLogger(DataBase.class.getName()).log(Level.INFO, 
                        "minden tábla létezett az adatbázisban, így nem szükséges lefuttatni a kapcsolatokat");
            }if(!created.contains(-1) && created.contains(1)){
                created.add(executeSQL("alap_adatok.sql"));
                
            }else{
                Logger.getLogger(DataBase.class.getName()).
                        log(Level.INFO, "alapadatok már benne vannak a táblákban!");
            }
        }else{
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, "nem sikerült az adatbázist létrehozni");
            System.exit(-1);
        }
    }
    private int createTable(String sqlOrder, String tableName){
        ResultSet rs; 
        try {
            rs = dbmd.getTables(null, "APP", tableName.toUpperCase() , null);
            if(!rs.next()){
                createStatement.execute(sqlOrder);
                Logger.getLogger(DataBase.class.getName()).log(Level.INFO, "{0} tábla létrehozása az adatbázisban", tableName.toUpperCase());
                rs.close();
                return 1;
            }
            rs.close();
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, "hiba a(z) "+ tableName.toUpperCase()+ " table létrehozásakor: " , ex);
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
    public void createVehicle(Row row) throws SQLIntegrityConstraintViolationException{
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
                            ps.setString(1, value.getStringValue());
                            break;
                        case 0:
                            ps.setString(2, value.getStringValue());
                            break;
                        case 2:
                            switch(cell.getStringCellValue()){
                                case "mb":
                                    ps.setString(3, "mb");
                                    break;
                                case "b":
                                    ps.setString(3, "b");
                                    break;
                                case "tb":
                                    ps.setString(3, "tb");
                                    break;
                                default: 
                                    return;
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
        } catch(SQLIntegrityConstraintViolationException ex){
            throw new SQLIntegrityConstraintViolationException();
        
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
    public void createDriver(Row row) throws SQLIntegrityConstraintViolationException {
        Workbook workbook = row.getSheet().getWorkbook();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        try{
            PreparedStatement ps, dateStatement;
            ps = conn.prepareStatement("INSERT INTO drivers VALUES (?,?,?)");
            dateStatement = conn.prepareStatement("INSERT INTO dates VALUES (?,?,?,?,?)");
            Cell firstName = evaluator.evaluateInCell(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            Cell lastName = evaluator.evaluateInCell(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            Cell id = evaluator.evaluateInCell(row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            if(id != null && firstName != null && lastName != null){
                int idInInt = (int)id.getNumericCellValue();
                ps.setInt(1,idInInt);
                ps.setString(2, firstName.getStringCellValue());
                ps.setString(3, lastName.getStringCellValue());
                try{
                    ps.execute();
                }catch(SQLIntegrityConstraintViolationException ex ){
                    ps.close();
                    throw new SQLIntegrityConstraintViolationException("driver"); 
                }
                for(int i = 3; i <= row.getLastCellNum(); i+=2){
                    Cell start = evaluator.evaluateInCell(row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                    Row firstRow = row.getSheet().getRow(0);
                    Cell date = evaluator.evaluateInCell(firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                    Cell end = evaluator.evaluateInCell(row.getCell(i + 1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));   
                    if(start != null && date != null && end != null){
                        dateStatement.setInt(1,idInInt);
                        java.sql.Date sqlDate = new java.sql.Date(date.getDateCellValue().getTime());
                        dateStatement.setDate(2,sqlDate);
                        if(start.getCellType() == CellType.NUMERIC){
                            LocalDateTime dateLDT, startLDT, endLDT;
                            dateLDT = LocalDateTime.ofInstant(date.getDateCellValue().toInstant(), ZoneId.systemDefault());
                            startLDT = LocalDateTime.of(dateLDT.toLocalDate(), LocalDateTime.ofInstant(start.getDateCellValue().toInstant(), ZoneId.systemDefault()).toLocalTime());
                            endLDT = LocalDateTime.of(dateLDT.toLocalDate(), LocalDateTime.ofInstant(end.getDateCellValue().toInstant(), ZoneId.systemDefault()).toLocalTime());
                            
                            if(endLDT.isBefore(startLDT) ){
                                endLDT = endLDT.plusDays(1);
                            }
                            Duration dif = Duration.between(startLDT, endLDT);
                            LocalTime workhours = LocalTime.ofSecondOfDay(dif.getSeconds());
                            LocalTime pause = LocalTime.of(0, 0);
                            if(workhours.isAfter(LocalTime.of(6, 0))){
                                pause = pause.plusMinutes(20);
                            }if(workhours.isAfter(LocalTime.of(9, 0))){
                                pause = pause.plusMinutes(25);
                            }
                            System.out.println(startLDT + " " + endLDT);
                            startLDT =  LocalDateTime.of(startLDT.toLocalDate(),LocalTime.of(startLDT.getHour(), startLDT.getMinute(), startLDT.getSecond()));
                            endLDT = LocalDateTime.of(endLDT.toLocalDate(),LocalTime.of(endLDT.getHour(), endLDT.getMinute(), endLDT.getSecond()));
                            dateStatement.setTimestamp(3, Timestamp.valueOf(startLDT));
                            dateStatement.setTimestamp(4, Timestamp.valueOf(endLDT));
                            dateStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.of(dateLDT.toLocalDate(), pause)));
                            try{
                                dateStatement.execute();
                            }catch(SQLIntegrityConstraintViolationException ex ){
                                dateStatement.close();
                                throw new SQLIntegrityConstraintViolationException("date");
                            }     
                        }else if(start.getCellType() == CellType.STRING){
                            dateStatement.setNull(3, Types.TIMESTAMP);
                            dateStatement.setNull(4, Types.TIMESTAMP);
                            dateStatement.setNull(5, Types.TIME);
                            try{
                                dateStatement.execute();
                            }catch(SQLIntegrityConstraintViolationException ex ){
                                throw new SQLIntegrityConstraintViolationException("date");
                            }
                        } 
                    }     
                }
            }
        ps.close();
        dateStatement.close();
        }catch(SQLIntegrityConstraintViolationException ex ){
            throw new SQLIntegrityConstraintViolationException(ex.getMessage());   
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
        System.out.println(tableName + " törölve");
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
                System.out.println(rs.getString(1) + " " +rs.getString(2) + " " + rs.getTimestamp(3) + " " + rs.getTimestamp(4) + " " + rs.getTimestamp(5));
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
    public void updateOrInsertRoute(int routeId, Route route){
        PreparedStatement ps = null;
        try{
            if(routeId == -1){
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
                ps.close();
            }else{
                ps = conn.prepareStatement("UPDATE routes SET route_name = ? WHERE route_id = ? ");
                ps.setString(1, route.getRouteName());
                route.setId(routeId);
                ps.setInt(2, routeId);  
                System.out.println(route.toString());
                ps.executeUpdate();
                ps.close();
            }
            
        }catch(SQLException ex){
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
                System.out.println("olvasott stopDistance " +stopDistanceIds.get(i));
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
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM schedule WHERE schedule_name = ? ");
            ps.setString(1, text);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Schedule sch = new Schedule( rs.getInt("schedule_id"),
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
                ps.setString(5, s.getGroup());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("SCHEDULE INSERTED");
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public ArrayList<Schedule> listSchedules() {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM schedule");
            ResultSet rs = ps.executeQuery();
            ArrayList<Schedule> scheduleList = new ArrayList<>();
            while(rs.next()){
                Schedule sch = new Schedule( rs.getInt("schedule_id"),
                        rs.getString("schedule_name"), rs.getInt("driver_id"), rs.getTimestamp("start_date").toLocalDateTime(),
                        rs.getTimestamp("end_date").toLocalDateTime(), rs.getString("license_plate"), rs.getInt("route_id"), rs.getString("app_group")
                );
                scheduleList.add(sch);
            }
            return scheduleList;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Set<Schedule> listSchedulesOnlyOnce() {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT schedule_name FROM schedule");
            ResultSet rs = ps.executeQuery();
            Set<Schedule> scheduleNames = new HashSet<>();
            while(rs.next()){
                Schedule s = new Schedule();
                s.setScheduleName(rs.getString("schedule_name"));
                scheduleNames.add(s);
            }
            return scheduleNames;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void deleteScheduleByName(String scheduleName) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM schedule WHERE schedule_name = ? ");
            ps.setString(1,scheduleName);
            ps.executeUpdate();
            System.out.println("SCHEDULE DELETED BY NAME OF " + scheduleName);
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Schedule> changeScheduleByName(String scheduleName) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM schedule where schedule_name = ? ");
            ps.setString(1, scheduleName);
            ResultSet rs = ps.executeQuery();
            ArrayList<Schedule> scheduleList = new ArrayList<>();
            while(rs.next()){
                Schedule sch = new Schedule( rs.getInt("schedule_id"),
                        rs.getString("schedule_name"), rs.getInt("driver_id"), rs.getTimestamp("start_date").toLocalDateTime(),
                        rs.getTimestamp("end_date").toLocalDateTime(), rs.getString("license_plate"), rs.getInt("route_id"), rs.getString("app_group")
                );
                scheduleList.add(sch);
            }
            return scheduleList;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void updateScheduleAsList(ArrayList<Schedule> schedules) {
        // NINCS KÉSZ7
        
        try {          
            PreparedStatement ps = conn.prepareStatement("UPDATE schedule set schedule_name = ?, start_date = ?, end_date = ? ,route_id = ?, app_group = ? WHERE schedule_id = ?");
            Iterator<Schedule> it = schedules.iterator();
            while(it.hasNext()){
                Schedule s = it.next();
                ps.setString(1, s.getScheduleName());
                ps.setTimestamp(2, Timestamp.valueOf(s.getStartDate()));
                ps.setTimestamp(3, Timestamp.valueOf(s.getEndTime()));
                ps.setInt(4, s.getRouteId());
                ps.setString(5, s.getGroup());
                ps.setInt(6, s.getRouteId());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("SCHEDULE INSERTED");
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public ArrayList<Driver> getDriversBetweenDates(LocalDateTime from, LocalDateTime to){
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT driver_id FROM schedule WHERE ("
                            + "(? NOT BETWEEN start_date AND end_date) "
                            + "OR (? NOT BETWEEN start_date AND end_date)) "
                            + "OR ((start_date NOT BETWEEN ? AND ?) "
                            + "AND (end_date NOT BETWEEN ? AND ?))");      
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            ps.setTimestamp(3, Timestamp.valueOf(from));
            ps.setTimestamp(4, Timestamp.valueOf(to));
            ps.setTimestamp(5, Timestamp.valueOf(from));
            ps.setTimestamp(6, Timestamp.valueOf(to));
            ResultSet rs = ps.executeQuery();
            Set<Integer> idSet = new HashSet<>() ;
            while(rs.next()){
                int id = rs.getInt("driver_id");
                idSet.add(id);
            }
            StringBuilder sql = new StringBuilder();
            PreparedStatement driverStatement;
            for(int s : idSet){
                sql.append(s).append(",");
            }
            if(sql.length() >= 1){
                sql.deleteCharAt(sql.length()-1);
            }
            driverStatement = conn.prepareStatement("SELECT * FROM drivers WHERE id not IN ("+sql.toString()+") "
                    + "AND id in (SELECT driver_id FROM dates WHERE ((? BETWEEN start_time AND end_time) AND (? BETWEEN start_time AND end_time)))");
            driverStatement.setTimestamp(1, Timestamp.valueOf(from));
            driverStatement.setTimestamp(2, Timestamp.valueOf(to));
            ResultSet driverResultSet = driverStatement.executeQuery();
            ArrayList<Driver> driverList = new ArrayList<>();
            while(driverResultSet.next()){
                Driver driver = new Driver();
                driver.setId(driverResultSet.getInt("id"));
                driver.setName(driverResultSet.getString("last_name")+ " " +driverResultSet.getString("for_name"));
                driverList.add(driver);
            }
            return driverList;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public ArrayList<Vehicle> getVehcilesBetweenDates(LocalDateTime from, LocalDateTime to, int scheduleId){
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT license_plate FROM schedule WHERE "
                            + "((start_date BETWEEN ? AND ?) OR (end_date BETWEEN ? AND ?) "
                            + "OR (? BETWEEN start_date AND end_date) "
                            + "AND (? BETWEEN start_date AND end_date)) "
                            + "AND NOT schedule_id = ? "); 
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            ps.setTimestamp(3, Timestamp.valueOf(from));
            ps.setTimestamp(4, Timestamp.valueOf(to));
            ps.setTimestamp(5, Timestamp.valueOf(from));
            ps.setTimestamp(6, Timestamp.valueOf(to));
            ps.setInt(7, scheduleId);
            ResultSet rs = ps.executeQuery();
            Set<String> licencePlateSet = new HashSet<>() ;
            while(rs.next()){
                String plate = rs.getString("license_plate");
                licencePlateSet.add(plate);
            }
            
            StringBuilder sql = new StringBuilder();
            PreparedStatement vehicleStatement;
            for(String s : licencePlateSet){
                sql.append("'").append(s).append("',");
            }
            if(sql.length() > 1){
                sql.deleteCharAt(sql.length()-1);
                vehicleStatement = conn.prepareCall("SELECT * FROM vehicles WHERE license_plate NOT IN ("+sql.toString()+")");
            }else{
                vehicleStatement = conn.prepareCall("SELECT * FROM vehicles ");
            }
            
            ResultSet vehicleResultSet = vehicleStatement.executeQuery();
            ArrayList<Vehicle> vehicleList = new ArrayList<>();
            while(vehicleResultSet.next()){
                Vehicle veh = new Vehicle();
                veh.setLicencePlate(vehicleResultSet.getString("license_plate"));
                veh.setName(vehicleResultSet.getString("name"));
                veh.setType(vehicleResultSet.getString("type"));
                vehicleList.add(veh);
            }
            return vehicleList;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
        
    }

    public void updateScheduleWithDriverAndVechicle(int selectedSchedule, Driver selectedDriver, Vehicle selectedVehicle) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE schedule set driver_id = ?, license_plate = ? WHERE schedule_id = ?"); 
            ps.setInt(1, selectedDriver.getId());
            ps.setString(2, selectedVehicle.getLicencePlate());
            ps.setInt(3, selectedSchedule);
            ps.executeUpdate();
            System.out.println("schedule updated");
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Driver getDriverById(int driverId) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM drivers WHERE id = ?"); 
            ps.setInt(1,driverId);
            ResultSet rs = ps.executeQuery();
            Driver driver = new Driver();
            while(rs.next()){
                driver.setId(rs.getInt("id"));
                driver.setName(rs.getString("last_name")+ " " +rs.getString("for_name"));
            }
            return driver;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Driver();
    }

    public Vehicle getVehicleByLicencePlate(String licensePlate) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM vehicles WHERE license_plate = ?"); 
            ps.setString(1, licensePlate);
            ResultSet rs = ps.executeQuery();
            Vehicle v = new Vehicle();
            while(rs.next()){
                v.setLicencePlate(licensePlate);
                v.setName(rs.getString("name"));
                v.setType(rs.getString("type"));
            }
            return v;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Vehicle();
    }

    public void deleteScheduleDriverAndVehcile(int scheduleId) { // még lehet hibás
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE schedule set driver_id = ?, license_plate = ?  WHERE schedule_id = ?"); 
            ps.setNull(1, Types.INTEGER);
            ps.setNull(2, Types.VARCHAR);
            ps.setInt(3, scheduleId);
            ps.executeUpdate();
            System.out.println("schedule deleted");
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stop getStopByName(String stopName) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM stops WHERE name = ? "); 
            ps.setString(1, stopName);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                return new Stop(rs.getInt(1), rs.getString(2), rs.getInt(3));
            }
            return null;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void updateOrInsertStopDistances(int routeId, ArrayList<StopDistance> stopDistances){
        try{
            if(routeId == -1){
                PreparedStatement ps;
                ps = conn.prepareStatement("INSERT INTO stop_distances (start_stop_id, end_stop_id, distance, roadId) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                Iterator<StopDistance> it = stopDistances.iterator();
                while(it.hasNext()){
                    StopDistance sd = it.next();
                    ps.setInt(1, sd.getStartId());
                    ps.setInt(2, sd.getEndId());
                    ps.setDouble(3, sd.getDistance());
                    ps.setString(4, sd.getRoadId());
                    ps.executeUpdate();
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        sd.setId(generatedKeys.getInt(1));
                        System.out.println("keygen sout "+sd.toString());
                    }
                    ps.clearParameters();
                }
                ps.close();
            }else{            
                PreparedStatement ps = conn.prepareStatement("SELECT stop_distance_id FROM route_destinations WHERE route_id = (?) ");
                ps.setInt(1, routeId);
                ResultSet rs = ps.executeQuery(); 
                ArrayList<Integer> idList = new ArrayList<>();
                while(rs.next()){
                    idList.add(rs.getInt(1));
                }
                ps.close();
                rs.close();
                PreparedStatement update = conn.prepareStatement("UPDATE stop_distances SET start_stop_id = ?, end_stop_id = ?, distance = ?, roadId = ? WHERE id = ? ");
                int i = 0;
                while(i<idList.size() && i <stopDistances.size()){
                    StopDistance sd = stopDistances.get(i);
                    update.setInt(1, sd.getStartId());
                    update.setInt(2, sd.getEndId());
                    update.setDouble(3, sd.getDistance());
                    update.setString(4, sd.getRoadId());
                    update.setInt(5, sd.getId());
                    update.addBatch();
                    i++;
                }
                update.executeBatch();
                update.close();
                if(i == stopDistances.size()){
                    PreparedStatement delete = conn.prepareStatement("DELETE FROM stop_distances WHERE id = ? ");
                    while(i< idList.size()){
                        delete.setInt(1, idList.get(i));
                        delete.addBatch();
                        i++;
                    }
                    delete.executeBatch();
                    delete.close();
                }else{
                    updateOrInsertStopDistances(-1, new ArrayList<>(stopDistances.subList(i, stopDistances.size())));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateOrInsertRouteDest(int routeId, ArrayList<RouteDestinations> routeArrayList) {
        try {
            if(routeId == -1){
                PreparedStatement ps = conn.prepareStatement("INSERT INTO route_destinations VALUES (?,?,?) ");
                Iterator<RouteDestinations> it =  routeArrayList.iterator();
                while(it.hasNext()){
                    RouteDestinations rd = it.next();
                    ps.setInt(1, rd.getId());
                    ps.setInt(2, rd.getNumber());
                    ps.setInt(3, rd.getStopDistanceId());
                    ps.addBatch();
                    System.out.println("inserting routeDest " +rd.toString());
                }
                ps.executeBatch();
                ps.close();
            }else{
                PreparedStatement ps = conn.prepareStatement("SELECT number FROM route_destinations WHERE route_id = (?) ");
                ps.setInt(1, routeId);
                ResultSet rs = ps.executeQuery();
                ArrayList<Integer> numberList = new ArrayList<>();
                while(rs.next()){
                    numberList.add(rs.getInt(1));
                }
                System.out.println(numberList.toString());
                System.out.println(routeArrayList.toString());
                PreparedStatement update = conn.prepareStatement("UPDATE route_destinations SET stop_distance_id = ? WHERE route_id = ? AND number = ? ");
                int i = 0;
                while(i<numberList.size() && i <routeArrayList.size()){
                    RouteDestinations rd = routeArrayList.get(i);
                    update.setInt(1, rd.getStopDistanceId());
                    update.setInt(2, rd.getId());
                    update.setInt(3, numberList.get(i));
                    System.out.println("updateing routeDest " + rd.toString());
                    update.addBatch();
                    i++;
                }
                update.executeBatch();
                update.close();
                if(i == routeArrayList.size()){
                    PreparedStatement delete = conn.prepareStatement("DELETE FROM route_destinations WHERE route_id = ? AND number = ?");
                    while(i< numberList.size()){
                        delete.setInt(1, routeId);
                        delete.setInt(2, numberList.get(i));
                        delete.addBatch();
                        i++;
                    }
                    delete.executeBatch();
                }else{
                    updateOrInsertRouteDest(-1, new ArrayList<>(routeArrayList.subList(i, routeArrayList.size())));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateOrInsertRouteTableView(RouteTableView rtv, int routeId) {
        try {
            if(routeId == -1){
                PreparedStatement ps = conn.prepareStatement("INSERT INTO route_table_view VALUES (?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, rtv.getRouteId());
                ps.setString(2, rtv.getRouteName());
                ps.setString(3, rtv.getStartName());
                ps.setString(4, rtv.getEndName());
                ps.setDouble(5, rtv.getDistanceSum());
                ps.setInt(6, rtv.getNumberCount());
                ps.executeUpdate();
                ps.close();
            }else{
                PreparedStatement ps = conn.prepareStatement("UPDATE route_table_view SET route_name = ?, start_name = ?, end_name = ?, distance = ?, num_of_stops = ? WHERE route_id = ? ");
                ps.setString(1, rtv.getRouteName());
                ps.setString(2, rtv.getStartName());
                ps.setString(3, rtv.getEndName());
                ps.setDouble(4, rtv.getDistanceSum());
                ps.setInt(5, rtv.getNumberCount());
                ps.setInt(6, routeId);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateOrInsertScheduleAsList(String changeScheduleName, ArrayList<Schedule> scheduleList) {
        try {
            if(changeScheduleName == null){
                PreparedStatement ps = conn.prepareStatement("INSERT INTO schedule(schedule_name, start_date, end_date,route_id, app_group) VALUES ( ?, ?, ?, ?, ?)");
                Iterator<Schedule> it = scheduleList.iterator();
                while(it.hasNext()){
                    Schedule s = it.next();
                    ps.setString(1, s.getScheduleName());
                    ps.setTimestamp(2, Timestamp.valueOf(s.getStartDate()));
                    ps.setTimestamp(3, Timestamp.valueOf(s.getEndTime()));
                    ps.setInt(4, s.getRouteId());
                    ps.setString(5, s.getGroup());
                    ps.addBatch();
                }
                ps.executeBatch();
                Logger.getLogger(DataBase.class.getName()).log(Level.INFO, "SCHEDULE INSERTED");
            }else{
                PreparedStatement ps = conn.prepareStatement("SELECT schedule_id FROM schedule WHERE schedule_name = ?");
                ps.setString(1, changeScheduleName);
                ResultSet rs = ps.executeQuery();
                ArrayList<Integer> idList = new ArrayList<>();
                while(rs.next()){
                    idList.add(rs.getInt(1));
                }
                PreparedStatement update = conn.prepareStatement("UPDATE schedule SET schedule_name = ?, start_date = ?, end_date = ? ,route_id = ?, app_group = ? WHERE schedule_id = ? ");
                int i = 0;
                while(i<idList.size() && i <scheduleList.size()){
                    Schedule s = scheduleList.get(i);
                    update.setString(1, s.getScheduleName());
                    update.setTimestamp(2, Timestamp.valueOf(s.getStartDate()));
                    update.setTimestamp(3, Timestamp.valueOf(s.getEndTime()));
                    update.setInt(4, s.getRouteId());
                    update.setString(5, s.getGroup());
                    update.setInt(6, idList.get(i));
                    update.addBatch();
                    i++;
                }
                update.executeBatch();
                update.close();
                Logger.getLogger(DataBase.class.getName()).log(Level.INFO, "SCHEDULE UPDATED");
                if(scheduleList.size() != idList.size()){
                    if(i == scheduleList.size()){
                        PreparedStatement delete = conn.prepareStatement("DELETE FROM schedule WHERE schedule_id = ? ");
                        while(i< idList.size()){
                            delete.setInt(1, idList.get(i));
                            delete.addBatch();
                            i++;
                        }
                        delete.executeBatch();
                        delete.close();
                        Logger.getLogger(DataBase.class.getName()).log(Level.INFO, "SCHEDULE DELETED");
                    }else{
                        updateOrInsertScheduleAsList(null, new ArrayList<>(scheduleList.subList(i, scheduleList.size())));
                    }
                }
            } 
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isDriverTableEmpty() {
       try{
           PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM drivers");
           ResultSet rs = ps.executeQuery();
           int count = 0;
           if(rs.next()){
                count = rs.getInt(1);
           }
           if(count != 0){    
               return false;
           }
       }catch(SQLException ex ){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
       }
       return true;
    }

    public boolean isVehicleTableEmpty() {
        try{
           PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM vehicles");
           ResultSet rs = ps.executeQuery();
           int count = 0;
           if(rs.next()){
                count = rs.getInt(1);
           }
           if(count != 0){    
               return false;
           }
       }catch(SQLException ex ){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
       }
       return true;
    }

    public boolean isScheduleContainsRoad(int routeId) {
        try{
           PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM schedule where route_id=?");
           ps.setInt(1, routeId);
           ResultSet rs = ps.executeQuery();
           int count = 0;
           if(rs.next()){
                count = rs.getInt(1);
           }
           if(count != 0){    
               return true;
           }
       }catch(SQLException ex ){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
       }
       return false;
    }

    public boolean isScheduleBound(String scheduleName) {
        try{
           PreparedStatement ps = conn.prepareStatement("SELECT driver_id, license_plate FROM schedule where schedule_name = ?");
           ps.setString(1, scheduleName);
           ResultSet rs = ps.executeQuery();
           if(rs.next()){
               System.out.println(rs.getString("driver_id") + " " + rs.getString("license_plate"));
               return true;
           }
           
       }catch(SQLException ex ){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
       }
       return false;
    }

    
}

