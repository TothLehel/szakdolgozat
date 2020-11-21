/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.file;

import menetrendtervezo.message.InputMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import menetrendtervezo.datacontroller.DataController;
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
public class FileManager extends DataController{
    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private static final DataController DATA_CONTROLLER = new DataController();
    private static final InputMessage INPUT_MESSAGE = new InputMessage();

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
        boolean succesfull = true;
        DATA_CONTROLLER.deleteSQLTable("dates");
        DATA_CONTROLLER.deleteSQLTable("drivers");
        if(isDriverTableFormatCorrect(sheet)){
            while(rowIt.hasNext()){
                Row row = rowIt.next();
                if(row.getRowNum() >= 2 ){ 
                    try {
                        DATA_CONTROLLER.createDriver(row);
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        switch(ex.getMessage()){
                            case "driver" :
                                INPUT_MESSAGE.driverAlreadyExists(row);
                                break;
                            case "date" :
                                INPUT_MESSAGE.dateAlreadyExists(row);
                                break;
                        }
                        succesfull = false;
                        DATA_CONTROLLER.deleteSQLTable("dates");
                        DATA_CONTROLLER.deleteSQLTable("drivers");
                        break;
                    }
                }
            }
            if(succesfull){
                DATA_CONTROLLER.listDrivers();
                INPUT_MESSAGE.succesfulDriverTableInput();
            }
        }else{
            INPUT_MESSAGE.driverTableFormatError();
        }
    }
    private static void iterateVehicleWorkbook(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
        boolean succesful = true;
        if(isVehicleTableFormatCorrect(sheet)){
            DATA_CONTROLLER.deleteSQLTable("vehicles");
            while(rowIt.hasNext()){
                Row row = rowIt.next();
                if(row.getRowNum() > 1 ){  
                    try {
                        DATA_CONTROLLER.createVehicle(row);
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        INPUT_MESSAGE.licencePlateExists(row);
                        DATA_CONTROLLER.deleteSQLTable("vehicles");
                        succesful = false;
                        break;
                    }
                }
            }
            if(succesful){
                INPUT_MESSAGE.succesfulVehicleTableInput();
            }
        }else{
            INPUT_MESSAGE.vehicleTableFormatError();     
        }
    }
    private static void iterateStopWorkbook(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
        if(isStopTableFormatCorrect(sheet)){
            DATA_CONTROLLER.deleteSQLTable("stops");
            while(rowIt.hasNext()){
                Row row = rowIt.next();
                if(row.getRowNum() != 0){
                    DATA_CONTROLLER.createStop(row);
                }
            }
            INPUT_MESSAGE.succesfulStopTableInput();
        }else{
            INPUT_MESSAGE.stopTableFormatError();
            
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
    private static Boolean isVehicleTableFormatCorrect(XSSFSheet sheet){
        Cell vehicleNameHeader = sheet.getRow(0).getCell(0,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Cell vehicleLicenceHeader = sheet.getRow(0).getCell(1,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Cell vehicleTypeHeader = sheet.getRow(0).getCell(2,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
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
    private static Boolean isStopTableFormatCorrect(XSSFSheet sheet){
        Cell stopNameHeader = sheet.getRow(0).getCell(0,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Cell capacityHeader = sheet.getRow(0).getCell(1,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
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

    public void readTable(File table, String tableName) {
        XSSFWorkbook workbook = createWorkbook(table);
        if(workbook != null){
            switch(tableName){
                case "driver" :
                    iterateDriverWorkbook(workbook);
                    break;
                case "stop" :
                    iterateStopWorkbook(workbook);
                    break;
                case "vehicle" :
                    iterateVehicleWorkbook(workbook);
                    break;
            } 
            try {
                workbook.close();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
    

