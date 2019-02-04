package processing.computation.implementation;

import common.interfaces.IInstanceElement;
import common.interfaces.ISchemaElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.interfaces.IElementStore;
import processing.computation.implementation.payload.PayloadElementFactory;
import processing.computation.implementation.schema.SchemaElementFactory;
import utils.interfaces.IElementCacheListener;

public class SchemaComputation implements IElementCacheListener<IInstanceElement> {

    private static final Logger logger = LogManager.getLogger(SchemaComputation.class.getName());

    private final SchemaElementFactory schemaElementFactory;
    private final PayloadElementFactory payloadElementFactory;

    public SchemaComputation(SchemaElementFactory schemaElementFactory, PayloadElementFactory payloadElementFactory){
        this.schemaElementFactory = schemaElementFactory;
        this.payloadElementFactory = payloadElementFactory;
    }

    public IElementStore<ISchemaElement> getSchemaCache(){
        return schemaElementFactory.getSchemaElementsStore();
    }

    @Override
    public void elementFlushed(IInstanceElement instance) {
        if (instance.getOutgoingQuints().isEmpty()) //keep old instance definition
            return;


        //TODO: be careful here, direct connection to the triple store means element is gone and PAYLOAD is not written!
        ISchemaElement schemaElementPrime = schemaElementFactory.createSchemaElement(instance);
        payloadElementFactory.createPayloadElements(instance, schemaElementPrime);
    }

    @Override
    public void finished() {
        logger.info("Finishing up the work...");
        schemaElementFactory.finished();
        logger.info("Schema computation finished!");
    }

    @Override
    public String toString() {
        return "SchemaComputation:" +
                "\n\tschemaElementFactory: " + schemaElementFactory +
                "\n\tpayloadElementFactory: " + payloadElementFactory +
                '}';
    }
}
