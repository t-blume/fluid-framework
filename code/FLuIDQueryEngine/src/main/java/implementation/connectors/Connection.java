package implementation.connectors;

import interfaces.Connector;

/**
 * Created by Blume Till on 26.08.2016.
 */

public class Connection {

    //Function Interface magic
    @FunctionalInterface
    public interface DBConnection {
        Connector getConnector(String url, String id);
    }

    public static Connector getRDF4JConnector(String url, String id) {
        return new RDF4JConnector(url, id);
    }


    public enum QueryType {
        SELECT
    }

    public enum QueryOption {
        DISTINCT
    }
    public static final QueryType SELECT = QueryType.SELECT;
    public static final QueryOption[] DISTINCT = new QueryOption[]{QueryOption.DISTINCT};

}