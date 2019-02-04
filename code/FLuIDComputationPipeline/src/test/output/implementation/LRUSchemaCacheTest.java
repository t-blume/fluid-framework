package output.implementation;

import common.implemenation.NodeResource;
import common.interfaces.IResource;
import org.semanticweb.yars.nx.Node;
import processing.computation.implementation.payload.DatasourceElement;
import processing.computation.implementation.schema.ComplexSchemaElement;
import processing.computation.implementation.schema.ObjectCluster;

import java.util.HashSet;
import java.util.Set;

public class LRUSchemaCacheTest {
    public static void main(String[] args) {
        ComplexSchemaElement complexSchemaElement = new ComplexSchemaElement();

        ObjectCluster objectCluster = new ObjectCluster();

        complexSchemaElement.subjectElements().put(1337, objectCluster);

        LRUSchemaCache lruSchemaCache = new LRUSchemaCache(4000);
        lruSchemaCache.add(complexSchemaElement);

        System.out.println(lruSchemaCache.size()); //each contained element is also in window, so size == 2?

        Set<IResource> contexts = new HashSet<>();
        contexts.add(new NodeResource(new Node() {
            @Override
            public String getLabel() {
                return "contextA";
            }

            @Override
            public int compareTo(Node o) {
                return 0;
            }
        }));
        lruSchemaCache.getSchemaElement(objectCluster.getLocator(), ObjectCluster.class).addPayload(new DatasourceElement(contexts));


        System.out.println(complexSchemaElement);

        System.out.println(lruSchemaCache.getSchemaElement(complexSchemaElement.getLocator(), complexSchemaElement.getClass()));
    }
}
