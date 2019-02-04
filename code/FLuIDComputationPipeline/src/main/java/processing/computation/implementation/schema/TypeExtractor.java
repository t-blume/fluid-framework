package processing.computation.implementation.schema;

import common.implemenation.NodeResource;
import common.interfaces.IQuint;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import utils.interfaces.IValueHandler;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * extract type of object
 */
public class TypeExtractor implements IValueHandler {
    @Override
    public Link extract(IQuint quint) {
        NodeResource nr;
        if ((nr = (NodeResource) quint.getObject()).getNode() instanceof Literal) {
            ObjectCluster literalCluster = determineLiteral(nr.getNode());
            if (literalCluster == null)
                literalCluster = ObjectCluster.OC_UNRESOLVED_LITERAL;

            try {
                return new Link(null, new URI(literalCluster.getURI()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                return new Link(null, new URI(quint.getObject().toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    protected ObjectCluster determineLiteral(Node node) {
        if (node instanceof Literal) {
            Literal l = (Literal) node;
            Resource r = l.getDatatype();
            return ObjectCluster.DATATYPE_MAP.get(r);
        }
        return null;
    }

    @Override
    public String toString() {
        return "TypeExtractor";
    }
}
