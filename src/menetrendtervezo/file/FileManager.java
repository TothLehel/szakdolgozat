/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.file;

import menetrendtervezo.error.InputError;
import menetrendtervezo.database.DataBase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author tlehe
 */
public class FileManager {
    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private static final DataBase DB = new DataBase();
    private static final InputError INPUT_ERROR = new InputError();

    public void setTitle(String title) {
        FILE_CHOOSER.setTitle(title);
    }

    public File TableBrowse(){
        Stage stage = new Stage();
        FileChooser.ExtensionFilter xlsx = new FileChooser.ExtensionFilter("xlsx", "*.xlsx");
        FILE_CHOOSER.getExtensionFilters().add(xlsx);
        File table = FILE_CHOOSER.showOpenDialog(stage);
        return table;
    }
    public void ReadDriverTable(File table){
        XSSFWorkbook workbook = createWorkbook(table);
        if(workbook != null){
            iterateDriverWorkbook(workbook);
            try {
                workbook.close();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void ReadVehicleTable(File table){
        XSSFWorkbook workbook = createWorkbook(table);
        if(workbook != null){
            iterateVehicleWorkbook(workbook);
            try {
                workbook.close();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     public void ReadStopTable(File table){
        XSSFWorkbook workbook = createWorkbook(table);
        if(workbook != null){
            iterateStopWorkbook(workbook);
            try {
                workbook.close();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private static XSSFWorkbook createWorkbook(File file){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return workbook;
    }
    
    private static void iterateDriverWorkbook(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
        if(isDriverTableFormatCorrect(sheet)){
            while(rowIt.hasNext()){
                Row row = rowIt.next();
                if(row.getRowNum() >= 2 ){
                    //System.out.println(row.getRowNum());
                    DB.createDriver(row);
                }
                /*Iterator<Cell> cellIt = row.iterator();
                
                while(cellIt.hasNext()){
                    Cell cell = row.getCell(cellIt.next().getColumnIndex(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if(cell != null){
                        CellValue value = evaluator.evaluate(cell);
                        Cell day;
                        if(cell.getColumnIndex() %2 == 0){
                            day = sheet.getRow(0).getCell(cell.getColumnIndex());
                        }else{
                            day = sheet.getRow(0).getCell(cell.getColumnIndex() - 1);
                        }
                        evaluator.evaluateInCell(day);
                        switchDriverTable(cell, day, value);
                    }
                }*/
            }
            DB.listDrivers();
            DB.deleteSQLTable("dates");
            DB.deleteSQLTable("drivers");
        }else{
            INPUT_ERROR.driverTableFormatError();
            DB.DeleteAll();
        }
        //DB.listDrivers();
    }
    private static void iterateVehicleWorkbook(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Cell vehicleNameHeader = sheet.getRow(0).getCell(0);
        Cell vehicleLicenceHeader = sheet.getRow(0).getCell(1);
        Cell vehicleTypeHeader = sheet.getRow(0).getCell(2);
        if(isVehicleTableFormatCorrect(vehicleNameHeader, vehicleLicenceHeader, vehicleTypeHeader)){
            while(rowIt.hasNext()){
                Row row = rowIt.next();
                if(row.getRowNum() > 1 ){
                    DB.createVehicle(row);
                }
            }
            DB.listVehicles();
        }else{
            INPUT_ERROR.vehicleTableFormatError();
            DB.DeleteAll();
        }
    }
    private static void iterateStopWorkbook(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Cell stopNameHeader = sheet.getRow(0).getCell(0);
        Cell capacityHeader = sheet.getRow(0).getCell(1);
        if(isStopTableFormatCorrect(stopNameHeader, capacityHeader)){
            while(rowIt.hasNext()){
                Row row = rowIt.next();
                if(row.getRowNum() != 0){
                    DB.createStop(row);
                }
            }
            DB.listStops();
        }else{
            INPUT_ERROR.stopTableFormatError();
            DB.DeleteAll();
        }
        
    }
    private static Boolean isDriverTableFormatCorrect(XSSFSheet sheet){
        Row firstRow = sheet.getRow(0);
        Cell driverLastname = firstRow.getCell(0 ,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Cell driverForname = firstRow.getCell(1 , Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Cell driverID = firstRow.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        int lastCellNumber = firstRow.getLastCellNum();
        if(driverLastname != null && driverForname !=null && driverID != null 
            && driverLastname.getCellType() == CellType.STRING 
            && driverForname.getCellType() == CellType.STRING
            && driverID.getCellType() == CellType.STRING 
            && driverLastname.getStringCellValue().equals("Vezetéknév")
            && driverForname.getStringCellValue().equals("Keresztnév") 
            && driverID.getStringCellValue().equals("Sofőr azonosító"))
        {
            for(int i = 3; i < lastCellNumber; i++){
                Cell cell = firstRow.getCell(i);
                switch(cell.getCellType()){
                    case STRING:
                        return false;
                    case FORMULA:
                        CellValue value = evaluator.evaluate(cell);
                        if(value.getCellType() != CellType.NUMERIC){
                            return false;
                        }
                        break;
                }
            } 
        }else{
            return false;
        }
        return true;
    }
    private static Boolean isVehicleTableFormatCorrect(Cell vehicleNameHeader, Cell vehicleLicenceHeader, Cell vehicleTypeHeader){
        if(vehicleNameHeader != null && vehicleLicenceHeader != null && vehicleTypeHeader != null){
            if(vehicleNameHeader.getCellType() == CellType.STRING 
                    && vehicleLicenceHeader.getCellType() == CellType.STRING
                    && vehicleTypeHeader.getCellType() == CellType.STRING
                    && vehicleNameHeader.getStringCellValue().equals("Jármű megnevezése") 
                    && vehicleLicenceHeader.getStringCellValue().equals("Rendszáma")
                    && vehicleTypeHeader.getStringCellValue().equals("Típusa")
                    )
            {
                return true;
            }
        }
        return false;
    }
    private static Boolean isStopTableFormatCorrect(Cell stopNameHeader, Cell capacityHeader){
        if(stopNameHeader != null && capacityHeader != null){
            if(stopNameHeader.getCellType() == CellType.STRING 
                    && capacityHeader.getCellType() == CellType.STRING
                    && stopNameHeader.getStringCellValue().equals("Megálló neve") 
                    && capacityHeader.getStringCellValue().equals("Férőhelyek száma")
                    )
            {
                return true;
            }
        }
        return false;
    }
    private static void switchDriverTable(Cell cell, Cell day, CellValue value){
        if(cell != null && cell.getRowIndex() >= 2){
            DB.createDrvier(cell, day, value);
        }
    }
    
}
    

