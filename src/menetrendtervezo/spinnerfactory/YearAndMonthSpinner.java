/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.spinnerfactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LocalTimeStringConverter;

/**
 *
 * @author tlehe
 */
public class YearAndMonthSpinner {
    String dateString;
    LocalDateTime firstDayOfWeek = LocalDateTime.now().with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
    LocalDateTime lastDayOfWeek = LocalDateTime.now().with(DayOfWeek.of(((WeekFields.of(Locale.getDefault()).getFirstDayOfWeek().getValue() + 5) % DayOfWeek.values().length) + 1));
    DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy MMMM dd");
    SpinnerValueFactory yearAndMonthSpinner = new SpinnerValueFactory<String>() {
        {
            
            dateString = "";
            dateString += hourFormatter.format(firstDayOfWeek) + " - ";
            dateString += hourFormatter.format(lastDayOfWeek);
            setValue(dateString);
            
        }
        
        @Override
        public void decrement(int steps) {
            if (getValue() == null)
                setValue(dateString);
            else {
                firstDayOfWeek =  firstDayOfWeek.minusWeeks(steps) ;
                lastDayOfWeek = lastDayOfWeek.minusWeeks(steps);
                String str = hourFormatter.format(firstDayOfWeek) + " - " + hourFormatter.format(lastDayOfWeek);
                setValue(str);
            }
        }
        
        @Override
        public void increment(int steps) {
            if (this.getValue() == null)
                setValue(dateString);
            else {
                firstDayOfWeek =  firstDayOfWeek.plusWeeks(steps) ;
                lastDayOfWeek = lastDayOfWeek.plusWeeks(steps);
                String str = hourFormatter.format(firstDayOfWeek) + " - " + hourFormatter.format(lastDayOfWeek);
                setValue(str);
            }
        }
    };

    public SpinnerValueFactory getYearAndMonthSpinner() {
        return yearAndMonthSpinner;
    }

    public LocalDateTime getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public LocalDateTime getLastDayOfWeek() {
        return lastDayOfWeek;
    }

    
}
