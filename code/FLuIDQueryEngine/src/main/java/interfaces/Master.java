package interfaces;

import static implementation.connectors.Connection.*;
import implementation.utils.datastructs.SetSet;

/**
 * Created by Blume Till on 24.08.2016.
 */
public interface Master<T> {

    void notify(T t, boolean isGold);

    void registerWorker();

    void deregisterWorker();

    SetSet getResultsGold();

    SetSet getResultsEval();

    DBConnection getDBConnection();

    String getUrl();

    String getGraphGold();

    String getGraphEval();

}
