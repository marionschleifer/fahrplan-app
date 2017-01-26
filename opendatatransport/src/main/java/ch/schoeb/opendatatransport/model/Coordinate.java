package ch.schoeb.opendatatransport.model;

import java.util.List;

public class Coordinate{
    private String type;
    private Number x;
    private Number y;

    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    public Number getX(){
        return this.x;
    }
    public void setX(Number x){
        this.x = x;
    }
    public Number getY(){
        return this.y;
    }
    public void setY(Number y){
        this.y = y;
    }
}
