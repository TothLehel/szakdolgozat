/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author tlehe
 */
public class WorkDay {
    private Date day;
    private Date start;
    private Date end;
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Date getStart() {
        return start;
    }
    public String getDayInString(){
        if(day != null){
            return DAY_FORMAT.format(day);
        }
        return "-";
    }
    
    public String getStartInString(){
        if(start != null){
            return TIME_FORMAT.format(start);
        }
        return "-";
    }
    
    public String getEndInString(){
        if(end == null)
            return "-";
        return TIME_FORMAT.format(end);
    }
    public void setStart(Date start) {
        if(start != null){
            Calendar c = Calendar.getInstance();
            c.setTime(day);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(start);
            c.add(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY) );
            this.start = c.getTime();
        }else{
            this.start = null;
        }
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        if(end != null){
            Calendar c = Calendar.getInstance();
            c.setTime(day);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(end);
            c.add(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
            if(getStart().before(end) && getStart() != null){
                this.end = c.getTime();
            }else{
                
                c.add(Calendar.DAY_OF_MONTH, 1);
                this.end = c.getTime();
            }
        }else{
            this.end = null;
        }
    }
}
