package ch.schoeb.opendatatransport.model;

import java.util.List;

import static android.R.attr.id;

public class Connection{
    private Number capacity1st;
    private Number capacity2nd;
    private String duration;
    private ConnectionStation from;
    private List<String> products;
    private List<Section> sections;
    private Service service;
    private ConnectionStation to;
    private Number transfers;

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
    public String getDuration(){
        return this.duration;
    }
    public void setDuration(String duration){
        this.duration = duration;
    }
    public ConnectionStation getFrom(){
        return this.from;
    }
    public void setFrom(ConnectionStation from){
        this.from = from;
    }
    public List getProducts(){
        return this.products;
    }
    public void setProducts(List products){
        this.products = products;
    }
    public List<Section> getSections(){
        return this.sections;
    }
    public void setSections(List<Section> sections){
        this.sections = sections;
    }
    public Service getService(){
        return this.service;
    }
    public void setService(Service service){
        this.service = service;
    }
    public ConnectionStation getTo(){
        return this.to;
    }
    public void setTo(ConnectionStation to){
        this.to = to;
    }
    public Number getTransfers(){
        return this.transfers;
    }
    public void setTransfers(Number transfers){
        this.transfers = transfers;
    }

    @Override
    public String toString(){
        return from.getStation().getName() + " -> " + to.getStation().getName() + " on " + from.getDeparture();
    }

    public int getId() {
        return id;
    }

}