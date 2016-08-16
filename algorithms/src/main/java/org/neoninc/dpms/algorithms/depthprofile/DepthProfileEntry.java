package org.neoninc.dpms.algorithms.depthprofile;

public class DepthProfileEntry {
    private Double depth = null;
    private Double temp = null;
    private Double depth2 = null;
    private Double temp2 = null;
    private Double depth3 = null;
    private Double deltaDepth = null;
    private Double deltaDepth1m = null;
    private Double deltaTemp = null;
    private Double deltaTemp1m = null;
    private Double deltaTempPerDepth = null;
    private boolean thermocline = false;
    private String metalimnion = null;
    private String stratification = null;
    private Double nextDepth2 = null;
    private Double nextDepth3 = null;
    private Double sectionDepthMin = null;
    private Double sectionDepthMax = null;
    private Double sectionThickness = null;
    private Double duplicateFlag = null;
    
    public Double getDepth() {
        return depth;
    }
    public void setDepth(Double depth) {
        this.depth = depth;
    }
    public Double getTemp() {
        return temp;
    }
    public void setTemp(Double temp) {
        this.temp = temp;
    }
    public Double getDepth2() {
        return depth2;
    }
    public void setDepth2(Double depth2) {
        this.depth2 = depth2;
    }
    public Double getTemp2() {
        return temp2;
    }
    public void setTemp2(Double temp2) {
        this.temp2 = temp2;
    }
    public Double getDepth3() {
        return depth3;
    }
    public void setDepth3(Double depth3) {
        this.depth3 = depth3;
    }
    public Double getDeltaDepth() {
        return deltaDepth;
    }
    public void setDeltaDepth(Double deltaDepth) {
        this.deltaDepth = deltaDepth;
    }
    public Double getDeltaDepth1m() {
        return deltaDepth1m;
    }
    public void setDeltaDepth1m(Double deltaDepth1m) {
        this.deltaDepth1m = deltaDepth1m;
    }
    public Double getDeltaTemp() {
        return deltaTemp;
    }
    public void setDeltaTemp(Double deltaTemp) {
        this.deltaTemp = deltaTemp;
    }
    public Double getDeltaTemp1m() {
        return deltaTemp1m;
    }
    public void setDeltaTemp1m(Double deltaTemp1m) {
        this.deltaTemp1m = deltaTemp1m;
    }
    public Double getDeltaTempPerDepth() {
        return deltaTempPerDepth;
    }
    public void setDeltaTempPerDepth(Double deltaTempPerDepth) {
        this.deltaTempPerDepth = deltaTempPerDepth;
    }
    public boolean isThermocline() {
        return thermocline;
    }
    public void setThermocline(boolean thermocline) {
        this.thermocline = thermocline;
    }
    public String getMetalimnion() {
        return metalimnion;
    }
    public void setMetalimnion(String metalimnion) {
        this.metalimnion = metalimnion;
    }
    public String getStratification() {
        return stratification;
    }
    public void setStratification(String stratification) {
        this.stratification = stratification;
    }
    public Double getNextDepth2() {
        return nextDepth2;
    }
    public void setNextDepth2(Double nextDepth2) {
        this.nextDepth2 = nextDepth2;
    }
    public Double getNextDepth3() {
        return nextDepth3;
    }
    public void setNextDepth3(Double nextDepth3) {
        this.nextDepth3 = nextDepth3;
    }
    public Double getSectionDepthMin() {
        return sectionDepthMin;
    }
    public void setSectionDepthMin(Double sectionDepthMin) {
        this.sectionDepthMin = sectionDepthMin;
    }
    public Double getSectionDepthMax() {
        return sectionDepthMax;
    }
    public void setSectionDepthMax(Double sectionDepthMax) {
        this.sectionDepthMax = sectionDepthMax;
    }
    public Double getSectionThickness() {
        return sectionThickness;
    }
    public void setSectionThickness(Double sectionThickness) {
        this.sectionThickness = sectionThickness;
    }
    public Double getDuplicateFlag() {
        return duplicateFlag;
    }
    public void setDuplicateFlag(Double duplicateFlag) {
        this.duplicateFlag = duplicateFlag;
    }
}
