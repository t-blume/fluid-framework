package implementation.eval;


import implementation.utils.datastructs.SetSet;
import interfaces.Master;
import implementation.connectors.Connection;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Blume Till on 24.08.2016.
 */
public class EvalMaster implements Master<Set<String>> {

    //do not use too many workers
    private int maxThreads;
    //keep track of worker
    private int activeWorker = 0;
    //synchronize changes on master
    private final Object syncObject = new Object();

    //internal variables
    private final Connection.DBConnection dbConnection;
    private final String url;
    private final String graphGold;
    private SetSet resultsGold = new SetSet();
    private final String graphEval;
    private SetSet resultsEval = new SetSet();


    /**
     * Allows unlimited threads
     *
     * @param graphGold
     * @param graphEval
     */
    public EvalMaster(Connection.DBConnection dbConnection, String url, String graphGold, String graphEval) {
        this(dbConnection, url, graphGold, graphEval, Integer.MAX_VALUE);
    }

    /**
     * Creates at most maxThreads per connection
     *
     * @param graphGold
     * @param graphEval
     * @param maxThreads
     */
    public EvalMaster(Connection.DBConnection dbConnection, String url, String graphGold, String graphEval, int maxThreads) {
        this.dbConnection = dbConnection;
        this.url = url;
        this.graphGold = graphGold;
        this.graphEval = graphEval;
        this.maxThreads = maxThreads;
    }

    public void evaluateLongPredicateQuery(Set<String> predicates, String var, int maxPredicates) {
        Set<String> queries = predicateQuerySplitter(var, predicates, maxPredicates);
        int queriesPerThread = (int) Math.ceil(queries.size() / maxThreads);

        Iterator<String> iterator = queries.iterator();
        Set<String> queryFragment = new TreeSet<>();
        while (iterator.hasNext()) {
            queryFragment.add(iterator.next());
            if (queryFragment.size() >= queriesPerThread) {
                if (graphGold != null) {
                    new EvalWorker(this, true, queryFragment, var).start();
                    registerWorker();
                }
                if (graphEval != null) {
                    new EvalWorker(this, false, queryFragment, var).start();
                    registerWorker();
                }
                queryFragment = new TreeSet<>();
            }
        }
        //wait for workers to finish
        synchronized (syncObject) {
            while (activeWorker > 0) {
                try {
                    syncObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void notify(Set<String> strings, boolean isGold) {
        synchronized (syncObject) {
            if (isGold)
                resultsGold.add(strings);
            else
                resultsEval.add(strings);
        }
    }

    @Override
    public void registerWorker() {
        synchronized (syncObject) {
            activeWorker++;
        }
    }

    @Override
    public void deregisterWorker() {
        synchronized (syncObject) {
            activeWorker--;
            syncObject.notifyAll();
        }
    }

    @Override
    public SetSet getResultsGold() {
        synchronized (syncObject) {
            return resultsGold;
        }
    }

    @Override
    public SetSet getResultsEval() {
        synchronized (syncObject) {
            return resultsEval;
        }
    }


    //////////////////////////////////////////////
    //////////////      HELPER      //////////////
    //////////////////////////////////////////////

    /**
     *
     * @param var
     * @param predicates
     * @param predsPerQuery
     * @return
     */
    public static Set<String> predicateQuerySplitter(String var, Set<String> predicates, int predsPerQuery) {
        Set<String> queries = new TreeSet<>();
        if (predicates == null || predicates.isEmpty())
            return queries;

        String tmpQuery = "";
        Iterator<String> iterator = predicates.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            tmpQuery += "?" + var + " <" + iterator.next() + "> [] .";
            count++;
            if (count >= predsPerQuery) {
                count = 0;
                queries.add("{" + tmpQuery + "}");
                tmpQuery = "";
            }
        }
        //add remaining
        if (!tmpQuery.isEmpty())
            queries.add("{" + tmpQuery + "}");

        return queries;
    }


    public Connection.DBConnection getDBConnection() {
        return dbConnection;
    }

    public String getUrl() {
        return url;
    }

    public String getGraphGold() {
        return graphGold;
    }

    public String getGraphEval() {
        return graphEval;
    }
}
