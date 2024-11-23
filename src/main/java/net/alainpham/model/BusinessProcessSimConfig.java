package net.alainpham.model;

public class BusinessProcessSimConfig {
    
    private String name;
    private int durationSecondsAverage;
    private int durationSecondsStdDeviation;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getDurationSecondsAverage() {
        return durationSecondsAverage;
    }
    public void setDurationSecondsAverage(int durationSecondsAverage) {
        this.durationSecondsAverage = durationSecondsAverage;
    }
    public int getDurationSecondsStdDeviation() {
        return durationSecondsStdDeviation;
    }
    public void setDurationSecondsStdDeviation(int durationSecondsStdDeviation) {
        this.durationSecondsStdDeviation = durationSecondsStdDeviation;
    }



    
}
