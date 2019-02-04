package implementation.connectors;

import org.eclipse.rdf4j.query.*;
import interfaces.Connector;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

import static implementation.connectors.Connection.*;


import java.util.*;

/**
 * Created by Blume Till on 25.08.2016.
 */
public class RDF4JConnector implements Connector {
    private final int maxPredicates = 10;
    private final String server_uri;
    private final String repository_id;
    private final Repository repository;
    private final RepositoryConnection con;

    public RDF4JConnector(String serverUri, String repositoryId) throws RepositoryException {
        server_uri = serverUri;
        repository_id = repositoryId;
        repository = new HTTPRepository(server_uri, repository_id);
        repository.initialize();
        con = repository.getConnection();
    }

    @Override
    public Set<String> executeQuery(QueryType type, QueryOption[] options, String[] selectVars, String body) {
        return executeQuery(type, options, selectVars, body, -1);
    }

    @Override
    public Set<String> executeQuery(QueryType type, QueryOption[] options, String[] selectVars, String body, int limit) {
        Set<Map<String,String>> resultTree = executeQueryDiffVars(type, options, selectVars, body, limit);

        Set<String> resultSet = new HashSet<>();
        resultTree.forEach(MAP -> {
            StringBuilder sb = new StringBuilder();
            MAP.entrySet().forEach(ENTRY -> sb.append(ENTRY.getValue()));
            resultSet.add(sb.toString());
        });

        return resultSet;
    }

    @Override
    public Set<Map<String,String>> executeQueryDiffVars(QueryType type, QueryOption[] options, String[] selectVars, String body) {
        return executeQueryDiffVars(type, options, selectVars, body, -1);
    }

    @Override
    public Set<Map<String,String>> executeQueryDiffVars(QueryType type, QueryOption[] options, String[] selectVars, String body, int limit) {
        TupleQueryResult results;
        try {

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryBuilder(type, options, selectVars, body, -1));

            //IMPORTANT NOTE: Get results as list to allow closing the connection!
            results = tupleQuery.evaluate();
        } catch (QueryEvaluationException | RepositoryException e) {
            e.printStackTrace();
            return null;
        }
        Set<Map<String,String>> resultTree = new HashSet<>();
        try {
            while (results.hasNext()) {
                BindingSet bindingSet = results.next();
                Map<String,String> resultMap = new HashMap<>();
                if(selectVars == null || selectVars.length <= 0)
                    bindingSet.forEach(X -> resultMap.put(X.getName(), X.getValue().stringValue()));
                else
                    for(String var : selectVars)
                        resultMap.put(var, bindingSet.getValue(var).stringValue());

                resultTree.add(resultMap);
            }
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
            return null;
        }
        return resultTree;
    }

    @Override
    public void close() {

    }

    @Override
    public int getMaxPredicates() {
        return maxPredicates;
    }


    /**
     * Helps creating simple SPARQL queries. Selected Variables will be returned, if null all variables.
     *
     * @param type
     * @param options
     * @param selectVars
     * @param body
     * @return
     */
    private String queryBuilder(QueryType type, QueryOption[] options, String[] selectVars, String body, int limit) {
        String query = "";
        if (type == null)
            return null;

        switch (type) {
            case SELECT: {
                query += "SELECT";
                break;
            }
        }
        if (options != null) {
            for (QueryOption option : options) {
                switch (option) {
                    case DISTINCT: {
                        query += " DISTINCT";
                        break;
                    }
                }
            }
        }
        if (selectVars != null && selectVars.length > 0)
            for (String var : selectVars)
                query += " ?" + var.trim();
        else
            query += " *";


        query += " WHERE {" + body + "} ";
        if (limit > 0)
            query += " LIMIT " + limit;

        return query;
    }
}
