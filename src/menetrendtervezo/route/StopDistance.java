/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.route;

/**
 *
 * @author tlehe
 */
public class StopDistance {
    private int id;
    private int startId;
    private int endId;
    private double distance;
    private String roadId;
    private int traficId;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getRoadId() {
        return roadId;
    }

    public void setRoadId(String roadId) {
        this.roadId = roadId;
    }

    public int getTraficId() {
        return traficId;
    }

    public void setTraficId(int traficId) {
        this.traficId = traficId;
    }
    private String selectedStartName;
    private String selectedStopName;
    private String roadName;
    private Integer number;

    public String getSelectedStartName() {
        return selectedStartName;
    }

    public void setSelectedStartName(String selectedStartName) {
        this.selectedStartName = selectedStartName;
    }

    public String getSelectedStopName() {
        return selectedStopName;
    }

    public void setSelectedStopName(String selectedStopName) {
        this.selectedStopName = selectedStopName;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }
   
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
