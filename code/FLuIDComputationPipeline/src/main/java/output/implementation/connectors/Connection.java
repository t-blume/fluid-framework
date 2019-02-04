package output.implementation.connectors;


import output.interfaces.IConnector;

/**
 * Created by Blume Till on 26.08.2016.
 */

public class Connection {

    //Function Interface magic
    @FunctionalInterface
    public interface DBConnection {
        IConnector getConnector(String url, String id);
    }


    public static IConnector getRDF4JConnector(String url, String id) {
        return new RDF4JConnector(url, id);
    }

    
}