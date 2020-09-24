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
public class RouteTableView {
    private int routeId;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public RouteTableView(int routeId, String routeName, double distanceSum, int numberCount, String startName, String endName) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.distanceSum = distanceSum;
        this.numberCount = numberCount;
        this.startName = startName;
        this.endName = endName;
    }
    private String routeName;
    private double distanceSum;
    private int numberCount;
    private String startName;
    private String endName;

    public RouteTableView(){};

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    
    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public double getDistanceSum() {
        return distanceSum;
    }

    public void setDistanceSum(double distanceSum) {
        this.distanceSum = distanceSum;
    }

    public int getNumberCount() {
        return numberCount;
    }

    public void setNumberCount(int numberCount) {
        this.numberCount = numberCount;
    }

}
