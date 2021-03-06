/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.spinnerfactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.converter.LocalTimeStringConverter;

/**
 *
 * @author tlehe
 */
public class MinuteSpinner {
    SpinnerValueFactory minSpinner = new SpinnerValueFactory<LocalTime>() {
        {
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("mm");
            setConverter(new LocalTimeStringConverter(hourFormatter, null));
            setValue(LocalTime.now());
            
        }
        
        @Override
        public void decrement(int steps) {
            if (getValue() == null)
                setValue(LocalTime.now());
            else {
                LocalTime time = (LocalTime) getValue();
                setValue(time.minusMinutes(steps));
            }
        }
        
        @Override
        public void increment(int steps) {
            if (this.getValue() == null)
                setValue(LocalTime.now());
            else {
                LocalTime time = (LocalTime) getValue();
                
                setValue(time.plusMinutes(steps));
            }
        }
    };

    public SpinnerValueFactory getMinSpinner() {
        return minSpinner;
    }
    
}
