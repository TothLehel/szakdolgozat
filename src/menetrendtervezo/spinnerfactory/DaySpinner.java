/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.spinnerfactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javax.swing.text.DateFormatter;

/**
 *
 * @author tlehe
 */
public class DaySpinner {
    String dateString;
    LocalDateTime firstDayOfWeek = LocalDateTime.now().with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
    //LocalDateTime lastDayOfWeek = LocalDateTime.now().with(DayOfWeek.of(((WeekFields.of(Locale.getDefault()).getFirstDayOfWeek().getValue() + 5) % DayOfWeek.values().length) + 1));
    LocalDateTime changedDay = firstDayOfWeek;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd EEEE");
    SpinnerValueFactory daySpinner = new SpinnerValueFactory<String>() {
        {
            dateString = formatter.format(firstDayOfWeek);
            setValue(dateString);
            
        }
        
        @Override
        public void decrement(int steps) {
            if (getValue() == null)
                setValue(dateString);
            else {
                changedDay =  changedDay.minusDays(steps) ;
                firstDayOfWeek = changedDay.with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
                dateString = formatter.format(changedDay);
                setValue(dateString);
            }
        }
        
        @Override
        public void increment(int steps) {
            if (getValue() == null)
                setValue(dateString);
            else {
                changedDay =  changedDay.plusDays(steps) ;
                firstDayOfWeek = changedDay.with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
                dateString = formatter.format(changedDay);
                setValue(dateString);
            }
        }
    };

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public SpinnerValueFactory getDaySpinner() {
        return daySpinner;
    }

    public String getDateString() {
        return dateString;
    }

    public LocalDateTime getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public LocalDateTime getChangedDay() {
        return changedDay;
    }
    
}
