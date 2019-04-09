package output.interfaces;

import common.IResource;
import common.interfaces.ISchemaElement;

import java.util.Set;


public interface IUpdateCoordinator {

    /**
     * Returns schemaElementLocator on success, null otherwise
     * @param schemaFactoryHash
     * @param instanceElementLocator
     * @return
     */
    IResource previousSchemaElement(int schemaFactoryHash, IResource instanceElementLocator);

    /**
     * Tell the oracle what you just did
     *  @param schemaFactoryHash
     * @param instanceElementLocator
     * @param schemaElementLocator
     * @param contexts
     */
    void addSchemaElement(int schemaFactoryHash, IResource instanceElementLocator, IResource schemaElementLocator, Set<IResource> contexts);

    /**
     * Tell the oracle what you just did referring to related instances that will be affected by an update
     *  @param schemaFactoryHash
     * @param instanceElementLocator
     * @param schemaElementLocator
     * @param contexts
     */
    void addSchemaElement(int schemaFactoryHash, IResource instanceElementLocator, IResource schemaElementLocator,
                          Set<IResource> contexts, Set<IResource> parentInstanceElementsLocators);


    Set<IResource> getParentInstances(IResource instanceElementLocator, IResource schemaElementLocator);

    /**
     * Returns true if need for merge, false for overwrite
     * @param oldSchemaElement
     * @param newSchemaElement
     * @return
     */
    boolean needForMerge(ISchemaElement oldSchemaElement, ISchemaElement newSchemaElement);


    void printInfo();
}
