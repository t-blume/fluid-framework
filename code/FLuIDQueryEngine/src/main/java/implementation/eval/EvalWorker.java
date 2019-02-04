package implementation.eval;

import interfaces.Connector;
import interfaces.Master;

import java.util.Set;


import static implementation.connectors.Connection.*;

/**
 * Created by Blume Till on 24.08.2016.
 */
public class EvalWorker extends Thread{
    private Master master;
    private boolean isGold;
    private Connector connection;
    private Set<String> qBodies;
    private String[] vars;

    public EvalWorker(Master master, boolean isGold, Set<String> qBodies, String... vars){
        this.master = master;
        this.isGold = isGold;
        String graphID = isGold ? master.getGraphGold() : master.getGraphEval();
        this.connection = master.getDBConnection().getConnector(master.getUrl(), graphID);
        this.qBodies = qBodies;
        this.vars = vars;
    }

    public void run(){
        for(String qBody : qBodies)
            master.notify(connection.executeQuery(SELECT, DISTINCT, vars, qBody), isGold);

        master.deregisterWorker();
        connection.close();
    }

}
