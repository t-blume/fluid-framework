package output.interfaces;


import common.interfaces.IQuint;
import common.interfaces.IResource;
import org.apache.jena.query.Query;

import java.util.List;

/**
 * Created by Blume Till on 24.08.2016.
 */
public interface IConnector {

    void clear();

    void close();

    String getContext();

    boolean addQuint(IQuint quint);

    boolean addQuints(List<IQuint> quints);

    boolean hasQuint(IQuint quint);

    List<IQuint> getQuints(IResource subjectURI);

    void removeQuint(IQuint quint);

    void removeQuints(List<IQuint> quints);

    List<IQuint> executeQuery(Query query);

}
