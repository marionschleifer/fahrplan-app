package ch.schoeb.opendatatransport;

public class OpenTransportRepositoryFactory {

    public static IOpenTransportRepository CreateOnlineOpenTransportRepository(){
        return new OnlineOpenTransportRepository();
    }

    public static IOpenTransportRepository CreateLocalOpenTransportRepository(){
        return new LocalOpenTransportRepository();
    }

}
