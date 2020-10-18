/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menetrendtervezo.entity;

import java.time.LocalDateTime;

/**
 *
 * @author tlehe
 */
public class Schedule {
    private String scheduleName;
    private int driverId;
    private LocalDateTime startDate;
    private LocalDateTime endTime;
    private String licensePlate;
    private int routeId;
    private String group;

    public String getScheduleName() {
        return scheduleName;
    }
    public Schedule() {}

    public Schedule(String scheduleName, LocalDateTime startDate, LocalDateTime endTime, int routeId, String group) {
        this.scheduleName = scheduleName;
        this.startDate = startDate;
        this.endTime = endTime;
        this.routeId = routeId;
        this.group = group;
    }

    public Schedule(String scheduleName, int driverId, LocalDateTime startDate, LocalDateTime endTime, String licensePlate, int routeId, String group) {
        this.scheduleName = scheduleName;
        this.driverId = driverId;
        this.startDate = startDate;
        this.endTime = endTime;
        this.licensePlate = licensePlate;
        this.routeId = routeId;
        this.group = group;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    
    
}
