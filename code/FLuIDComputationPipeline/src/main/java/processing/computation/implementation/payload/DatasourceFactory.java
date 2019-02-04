package processing.computation.implementation.payload;

import common.interfaces.*;

import java.util.HashSet;
import java.util.Set;

public class DatasourceFactory extends PayloadElementFactory {


    @Override
    public void createPayloadElements(IInstanceElement instanceElement, ISchemaElement schemaElement) {
        Set<IResource> contextResources = extract(instanceElement);
        IPayloadElement payloadElement = new DatasourceElement(contextResources);
        schemaElement.addPayload(payloadElement);
        //System.out.println("PAYELEM: " + payloadElement);
    }


    private static Set<IResource> extract(IInstanceElement instance) {
        Set<IResource> contexts = new HashSet<>();
        for (IQuint q : instance.getOutgoingQuints())
          contexts.add(q.getContext());

        return contexts;
    }

    @Override
    public String toString() {
        return "DataSourcePayload";
    }
}
