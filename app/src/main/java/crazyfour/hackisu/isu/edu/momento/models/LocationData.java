package crazyfour.hackisu.isu.edu.momento.models;

import java.util.Date;

/**
 * Created by Sriram on 20-02-2016.
 */
public class LocationData {
    String featureName;
    Date locationDate;

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Date getLocationDate() {
        return locationDate;
    }

    public void setLocationDate(Date locationDate) {
        this.locationDate = locationDate;
    }

    public LocationData(String featureName, Date locationDate) {
        this.featureName = featureName;
        this.locationDate = locationDate;
    }
}
