package output.implementation;

import common.IResource;
import common.implementation.NodeResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.yars.nx.Resource;
import processing.computation.implementation.payload.DatasourceElement;
import processing.computation.implementation.schema.ComplexSchemaElement;
import processing.computation.implementation.schema.Link;
import processing.computation.implementation.schema.ObjectCluster;
import processing.computation.implementation.schema.PropertyCluster;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class IncSchemaStoreTest {
    private static final Logger logger = LogManager.getLogger(output.implementation.IncSchemaStoreTest.class.getName());


    private RDF4JCachedSchemaElementStore elementStore;


    @Before
    public void setUp() {
        elementStore = new RDF4JCachedSchemaElementStore("http://localhost:8080/rdf4j-server/", "testing", 10000);
    }

    @After
    public void tearDown() throws Exception {
        //elementStore.flush();
    }

    @Test
    public void testSchemaElements() {
        Set<IResource> contexts = new HashSet<>();
        contexts.add(new NodeResource(new Resource("http://example.com")));

        ComplexSchemaElement complexSchemaElement = new ComplexSchemaElement();
        ObjectCluster schemaElement1 = new ObjectCluster();
        try {
            System.out.println(elementStore.size());

            schemaElement1.addAttribute(new Link(null, new URI("http://test-object1.com")));
            schemaElement1.addAttribute(new Link(null, new URI("http://test-object2.com")));

            complexSchemaElement.subjectElements().put(1337, schemaElement1);
            complexSchemaElement.addPayload(new DatasourceElement(contexts));

            String URI = complexSchemaElement.getURI();
            elementStore.add(complexSchemaElement);

            ObjectCluster schemaElement2 = new ObjectCluster();
            schemaElement2.addAttribute(new Link(null, new URI("http://test-object1.com")));
            schemaElement2.addAttribute(new Link(null, new URI("http://test-object2.com")));
            System.out.println(elementStore.size());

            elementStore.add(schemaElement2);
            System.out.println("Local object: ");
            System.out.println(complexSchemaElement);
            System.out.println(elementStore.size());

            PropertyCluster schemaElement3 = new PropertyCluster();
            schemaElement3.addAttribute(new Link(new URI("http://test-peroprty1.com"), null));
            schemaElement3.addAttribute(new Link(new URI("http://test-peroprty2.com"), null));
            ComplexSchemaElement schemaElement4 = new ComplexSchemaElement();
            schemaElement4.subjectElements().put(1337, schemaElement2);
            schemaElement4.subjectElements().put(42, schemaElement3);
            elementStore.add(schemaElement4);

            System.out.println(elementStore.size());
            System.out.println(schemaElement4);

            ComplexSchemaElement toUpdateElement = (ComplexSchemaElement) elementStore.getSchemaElement(complexSchemaElement.getLocator(), ComplexSchemaElement.class);
            System.out.println(toUpdateElement);
            ((ObjectCluster) toUpdateElement.subjectElements().get(1337)).addAttribute(new Link(null, new URI("http://test-object8.com")));

            System.out.println(toUpdateElement);

            System.out.println(elementStore.getSchemaElement(schemaElement4.getLocator(), ComplexSchemaElement.class));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
