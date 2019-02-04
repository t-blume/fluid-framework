package common.interfaces;

import java.util.HashMap;
import java.util.Set;

public interface ISchemaGraph {

    /**
     * Return a set of inferable TypeInformation for the given instance
     * To ensure maximal inference, infer all properties before calling
     * this method
     *
     * @param instance
     * @return
     */
    Set<String> inferSubjectTypes(IInstanceElement instance);

    /**
     * Return a set of inferrable properties for the given instance
     *
     * @param instance
     * @return
     */
    HashMap<String, Set<String>> inferProperties(IInstanceElement instance);

    /**
     * Return a set of inferable TypeInformation about the object
     * for the given statement
     *
     * @param quint
     * @return
     */
    Set<String> inferObjectTypes(IQuint quint);


    /**
     * Add a ontology-level statement to the SchemaGraph.
     *
     * @param schemaStatement
     * @return
     */
    boolean add(IQuint schemaStatement);

}
