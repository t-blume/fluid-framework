package output.implementation.connectors;

import common.IResource;
import common.implementation.NodeResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.yars.nx.Resource;
import processing.computation.implementation.payload.DatasourceElement;
import processing.computation.implementation.schema.ComplexSchemaElement;
import processing.computation.implementation.schema.Link;
import processing.computation.implementation.schema.ObjectCluster;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class RDF4JConnectorTest {

    private RDF4JConnector rdf4JConnector;
    @Before
    public void setUp() throws Exception {
        rdf4JConnector = new RDF4JConnector("http://localhost:8080/rdf4j-server/","testing");
        rdf4JConnector.clear();
    }

    @After
    public void tearDown() throws Exception {
        //rdf4JConnector.clear();
    }

    @Test
    public void addSchemaElement() {
        Set<IResource> contexts = new HashSet<>();
        contexts.add(new NodeResource(new Resource("http://example.com")));

        ComplexSchemaElement complexSchemaElement = new ComplexSchemaElement();
        ObjectCluster schemaElement1 = new ObjectCluster();
        try {
            schemaElement1.addAttribute(new Link(null, new URI("http://test-object1.com")));
            schemaElement1.addAttribute(new Link(null, new URI("http://test-object2.com")));

            complexSchemaElement.subjectElements().put(1337, schemaElement1);
            complexSchemaElement.addPayload(new DatasourceElement(contexts));

            String URI = complexSchemaElement.getURI();
            rdf4JConnector.addSchemaElement(complexSchemaElement);

            System.out.println("Local object: ");
            System.out.println(complexSchemaElement);

            ComplexSchemaElement schemaElement2 = (ComplexSchemaElement) rdf4JConnector.getSchemaElement(URI, ComplexSchemaElement.class);

            System.out.println("Remote object: ");
            System.out.println(schemaElement2);

            schemaElement1.addAttribute(new Link(null, new URI("http://test-object3.com")));

            System.out.println("Changed element: ");
            System.out.println(complexSchemaElement);

            rdf4JConnector.updateSchemaElement(complexSchemaElement);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSchemaElement() {
    }
}