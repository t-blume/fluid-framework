package processing.computation.implementation.payload;

import common.interfaces.IInstanceElement;
import common.interfaces.ISchemaElement;

public abstract class PayloadElementFactory {


    public abstract void createPayloadElements(IInstanceElement instanceElement, ISchemaElement schemaElement);

}
