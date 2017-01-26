package ch.schoeb.opendatatransport.model;

public class Prognosis{
    private String arrival;
    private Number capacity1st;
    private Number capacity2nd;
    private String departure;
    private String platform;

    public String getArrival(){
        return this.arrival;
    }
    public void setArrival(String arrival){
        this.arrival = arrival;
    }
    public Number getCapacity1st(){
        return this.capacity1st;
    }
    public void setCapacity1st(Number capacity1st){
        this.capacity1st = capacity1st;
    }
    public Number getCapacity2nd(){
        return this.capacity2nd;
    }
    public void setCapacity2nd(Number capacity2nd){
        this.capacity2nd = capacity2nd;
    }
    public String getDeparture(){
        return this.departure;
    }
    public void setDeparture(String departure){
        this.departure = departure;
    }
    public String getPlatform(){
        return this.platform;
    }
    public void setPlatform(String platform){
        this.platform = platform;
    }
}
