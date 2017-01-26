package ch.schoeb.opendatatransport.model;

import java.util.List;

public class ConnectionList{

    private List<Connection> connections;

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    @Override
    public String toString(){

        String connections = "";

        for (int i = 0; i < getConnections().size(); i++) {
            connections += i + ". " + getConnections().get(i).toString() + "\r\n";
        }

        return connections;
    }
}
