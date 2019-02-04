package common.interfaces;

import java.util.Collection;

/**
 * Models an element of a schema.
 */
public interface ISchemaElement extends ILocatable {


    String getURI();
    /**
     * Takes all information from other schema element and combine it with its
     * own information. All information will be merged according to Element specific
     * rules. The element calling will have all the information.
     *
     * Remark: This will change its locator resource.
     *
     * @param otherSchemaElement
     */
    void merge(ISchemaElement otherSchemaElement);


    void addPayload(IPayloadElement payloadElement);

    Collection<IPayloadElement> getPayload();

    //ISchemaElement clone();

}
