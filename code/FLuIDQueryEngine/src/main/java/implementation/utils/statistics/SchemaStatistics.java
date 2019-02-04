package implementation.utils.statistics;

import implementation.connectors.Connection;
import interfaces.Connector;

import java.util.Set;

import static implementation.connectors.Connection.*;

/**
 * Created by Blume Till on 07.10.2016.
 */
public class SchemaStatistics {

    private Connection.DBConnection dbConnection;
    private final String url;
    private final String graph;

    public SchemaStatistics(Connection.DBConnection dbConnection, String url, String graph){
        this.dbConnection = dbConnection;
        this.url = url;
        this.graph = graph;
    }


    public void getInstancesPerDatasource(){

    }

    /**
     * @param connection
     * @param qBody
     * @param vars
     * @return
     */
    public Set<String> queryDatasource(Connector connection, String qBody, String... vars) {
        return connection.executeQuery(SELECT, DISTINCT, vars, qBody);
    }
}
