package com.hust.ewsystem.DAO.DTO;


import com.hust.ewsystem.DAO.PO.RealPoint;

import java.util.Map;

public class RealToStandMapping {
    private Map<String, RealPoint> realToStandPointMap;
    private Map<String, String> realLabelToStandLabelMap;

    public RealToStandMapping(Map<String, RealPoint> realToStandPointMap, Map<String, String> realLabelToStandLabelMap) {
        this.realToStandPointMap = realToStandPointMap;
        this.realLabelToStandLabelMap = realLabelToStandLabelMap;
    }

    public Map<String, RealPoint> getRealToStandPointMap() {
        return realToStandPointMap;
    }

    public Map<String, String> getRealLabelToStandLabelMap() {
        return realLabelToStandLabelMap;
    }
}
