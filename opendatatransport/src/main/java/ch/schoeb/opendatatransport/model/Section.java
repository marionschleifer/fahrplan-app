package ch.schoeb.opendatatransport.model;

public class Section {

    private Journey journey;

    private ConnectionStation departure;

    private ConnectionStation arrival;

    public ConnectionStation getArrival() {
        return arrival;
    }

    public void setArrival(ConnectionStation arrival) {
        this.arrival = arrival;
    }

    public ConnectionStation getDeparture() {
        return departure;
    }

    public void setDeparture(ConnectionStation departure) {
        this.departure = departure;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }
}
