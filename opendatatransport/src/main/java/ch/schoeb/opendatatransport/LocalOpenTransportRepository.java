package ch.schoeb.opendatatransport;

import java.util.ArrayList;
import java.util.Random;

import ch.schoeb.opendatatransport.model.Connection;
import ch.schoeb.opendatatransport.model.ConnectionList;
import ch.schoeb.opendatatransport.model.Station;
import ch.schoeb.opendatatransport.model.StationList;

public class LocalOpenTransportRepository implements IOpenTransportRepository {
    @Override
    public StationList findStations(String query) {
        ArrayList<Station> stations = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Station station = new Station();
            stations.add(station);
        }

        waitRandomTime();

        StationList list = new StationList();
        list.setStations(stations);
        return list;
    }

    @Override
    public ConnectionList searchConnections(String from, String to) {
        return searchConnections(from, to, null, null, null, false);
    }

    @Override
    public ConnectionList searchConnections(String from, String to, String via, String date, String time, Boolean isArrivalTime) {
        ArrayList<Connection> connections = new ArrayList<>();

        Connection firstDirectConnection = new Connection();
        connections.add(firstDirectConnection);

        Connection secondIndirectConnection = new Connection();
        connections.add(secondIndirectConnection);

        waitRandomTime();

        ConnectionList list = new ConnectionList();
        list.setConnections(connections);

        return list;
    }

    private void waitRandomTime() {
        Random r = new Random();
        try {
            Thread.sleep(r.nextInt(10000));
        } catch (InterruptedException e) {

        }
    }
}
