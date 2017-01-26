package ch.schoeb.opendatatransport.model;

public class ConnectionStation {
    private String arrival;
    private String arrivalTimestamp;
    private String delay;
    private String departure;
    private Number departureTimestamp;
    private Station location;
    private String platform;
    private Prognosis prognosis;
    private Station station;

    public String getArrival(){
        return this.arrival;
    }
    public void setArrival(String arrival){
        this.arrival = arrival;
    }
    public String getArrivalTimestamp(){
        return this.arrivalTimestamp;
    }
    public void setArrivalTimestamp(String arrivalTimestamp){
        this.arrivalTimestamp = arrivalTimestamp;
    }
    public String getDelay(){
        return this.delay;
    }
    public void setDelay(String delay){
        this.delay = delay;
    }
    public String getDeparture(){
        return this.departure;
    }
    public void setDeparture(String departure){
        this.departure = departure;
    }
    public Number getDepartureTimestamp(){
        return this.departureTimestamp;
    }
    public void setDepartureTimestamp(Number departureTimestamp){
        this.departureTimestamp = departureTimestamp;
    }
    public Station getLocation(){
        return this.location;
    }
    public void setLocation(Station location){
        this.location = location;
    }
    public String getPlatform(){
        return this.platform;
    }
    public void setPlatform(String platform){
        this.platform = platform;
    }
    public Prognosis getPrognosis(){
        return this.prognosis;
    }
    public void setPrognosis(Prognosis prognosis){
        this.prognosis = prognosis;
    }
    public Station getStation(){
        return this.station;
    }
    public void setStation(Station station){
        this.station = station;
    }
}