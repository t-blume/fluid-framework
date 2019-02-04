package interfaces;

import static implementation.connectors.Connection.*;

import java.util.Map;
import java.util.Set;

/**
 * Created by Blume Till on 24.08.2016.
 */
public interface Connector {


    
    Set<String> executeQuery(QueryType type, QueryOption[] options, String[] selectVars, String body);
    Set<String> executeQuery(QueryType type, QueryOption[] options, String[] selectVars, String body, int limit);

    Set<Map<String,String>> executeQueryDiffVars(QueryType type, QueryOption[] options, String[] selectVars, String body);
    Set<Map<String,String>> executeQueryDiffVars(QueryType type, QueryOption[] options, String[] selectVars, String body, int limit);

    void close();
    int getMaxPredicates();
}
