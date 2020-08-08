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
public class RoadType {
    
    private String roadId;
    private String roadType;

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getRoadId() {
        return roadId;
    }

    public void setRoadId(String roadId) {
        this.roadId = roadId;
    }

    public RoadType(String roadId, String roadType) {
        this.roadId = roadId;
        this.roadType = roadType;
    }
    
    
}
