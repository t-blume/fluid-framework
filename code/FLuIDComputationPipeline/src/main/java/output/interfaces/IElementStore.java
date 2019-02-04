package output.interfaces;

import common.interfaces.ILocatable;
import common.interfaces.IResource;

import java.util.Collection;
import java.util.List;

public interface IElementStore<T extends ILocatable> extends IElementListener<T>{
    /**
     * Checks whether an element specified by its {@link IResource} locator is
     * contained in the window
     *
     * @param locator
     *            The locator of the element
     * @return <code>true</code>, if the element is contained in the window,
     *         <code>false</code> otherwise
     */
    boolean contains(IResource locator);

    /**
     * Returns the schema element for the given {@link IResource} locator
     *
     * @param elementLocator
     *            The element which's relations should be queried
     * @return A set containing the relations between elements
     */
    T getSchemaElement(IResource elementLocator, Class<? extends T> schemaElementType);

    /**
     * Removes and returns the element specified by the given {@link IResource} locator.
     *
     * @param elementLocator
     *            The instance's locator
     * @return The instance specified by the locator, <code>null</code>
     *         otherwise
     */
    boolean removeElement(IResource elementLocator, Class<? extends T> elementType);



    Collection<T> removeLast(int n);

    /**
     * Adds a schema element computed from this instance to the data store
     *
     * @param element
     */
    void add(T element);


    /**
     * Returns the number of elements contained in the store
     *
     * @return The number of elements
     */
    int size();

    /**
     * Flushes the whole window. That means, that all elements have to be removed
     * from it, triggering the listener callbacks.
     */
    void flush();

    /**
     * Signals that no more elements should be cached. Listeners should be
     * notified and the window should be emptied
     */
    void close();

    /**
     * Checks whether the Cache is closed
     *
     * @return <code>true</code> if the window is closed, <code>false</code> else
     */
    boolean isClosed();



    /**
     * Registers an {@link IElementListener}. It will be called for each element
     * with its relations removed from the window
     *
     * @param listener
     *            The listener to be registered
     */
    void registerCacheListener(IElementListener<T> listener);


}
