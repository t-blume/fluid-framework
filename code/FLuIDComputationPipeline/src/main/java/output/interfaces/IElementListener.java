package output.interfaces;


import common.ILocatable;

/**
 * A listener for emitted schema elements
 * 
 * @author Bastian
 *
 */
public interface IElementListener<T extends ILocatable> {

	/**
	 * An element has been emitted
	 * 
	 * @param el
	 *            The emitted elements
	 */
	void elementEmitted(T el);

	/**
	 * The emitter has stopped sending new elements
	 * 
	 * @param emitter
	 *            The emitter
	 */
	void finished(IElementStore<T> emitter);
}
