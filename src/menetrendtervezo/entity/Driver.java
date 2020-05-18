/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.entity;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;

/**
 *
 * @author tlehe
 */
public class Driver {
    private String name;
    private int id;
    private ArrayList<WorkDay> workDays = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

   public void addWorkDay(Cell cell, Cell day, CellValue value){
        WorkDay wd;
        wd = searchWorkDayList(day);
        if(wd == null){
            wd = new WorkDay();
            wd.setDay(day.getDateCellValue());
        }
        if(value.getCellType() == CellType.NUMERIC){
            if(cell.getColumnIndex() % 2 == 0){
                wd.setStart(cell.getDateCellValue());
            }else{
                wd.setEnd(cell.getDateCellValue());
            }
        }else{
            wd.setStart(null);
            wd.setEnd(null);
        }
        workDays.add(wd);
   }
   private WorkDay searchWorkDayList(Cell day){
        WorkDay wd = null;
        for (WorkDay element : workDays) {
            if(element.getDay().equals(day.getDateCellValue())){
                wd = element;
            }
        }
        return wd;
   }
   public String getWorkDays(){
       Iterator<WorkDay> iterator = workDays.iterator();
       String str = "";
       while(iterator.hasNext()){
            WorkDay wd = iterator.next();
            str += wd.getDayInString() + " " + wd.getStartInString() + " " + wd.getEndInString() + " " ;
        }
        return str;
   } 
}
