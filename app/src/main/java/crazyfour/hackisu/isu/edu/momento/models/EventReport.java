package crazyfour.hackisu.isu.edu.momento.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishanthsivakumar on 2/21/16.
 */
public class EventReport {

    List<ReportTuple> callReport = null;
    List<ReportTuple> messageReport = null;
    List<ReportTuple> locationReport = null;

    public EventReport(){
        callReport = new ArrayList<>();
        messageReport = new ArrayList<>();
        locationReport = new ArrayList<>();
    }

    public List<ReportTuple> getCallReport() {
        return callReport;
    }

    public void setCallReport(List<ReportTuple> callReport) {
        this.callReport = callReport;
    }

    public List<ReportTuple> getMessageReport() {
        return messageReport;
    }

    public void setMessageReport(List<ReportTuple> messageReport) {
        this.messageReport = messageReport;
    }

    public List<ReportTuple> getLocationReport() {
        return locationReport;
    }

    public void setLocationReport(List<ReportTuple> locationReport) {
        this.locationReport = locationReport;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for(ReportTuple cReport: callReport){
            result.append(cReport.toString());
        }
        for(ReportTuple mReport: messageReport){
            result.append(mReport.toString());
        }
        for(ReportTuple lReport: locationReport){
            result.append(lReport.toString());
        }
        return result.toString();
    }
}
