package processing.preprocessing.interfaces;


import common.IQuint;

/**
 * Listener used for handling processed {@link IQuint}s. The
 * {@link #finishedQuint(IQuint)} method will be called whenever an
 * {@link IQuint} has finished the processing stage.
 * 
 * @see {@link IQuintPipeline}
 * 
 * @author Bastian
 * 
 */
public interface IQuintListener {

	/**
	 * This method will be called every time, an {@link IQuint} finishes
	 * processing
	 * 
	 * @param i
	 *            The finished quint
	 */
	void finishedQuint(IQuint i);

	/**
	 * Signals that no more quints will follow
	 */
	void finished();
}
