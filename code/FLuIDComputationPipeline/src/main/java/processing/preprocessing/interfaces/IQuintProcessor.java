package processing.preprocessing.interfaces;


import common.IQuint;

import java.util.List;

/**
 * A processing step in the {@link IQuintPipeline}. It is supposed to do some
 * processing on a given {@link IQuint}. For each quint, the processor can emit a
 * number of new quints. The original quints won't be saved for further steps, so
 * in order to keep it, the implementation has to return it.
 * 
 * @author Bastian
 * 
 */
public interface IQuintProcessor {

	/**
	 * Does some kind of processing with the quint and emits a number of quints
	 * for further processing.
	 * 
	 * @param q
	 *            The quint to be processed
	 * @return A list containing the emitted quint
	 */
	List<IQuint> processQuint(IQuint q);

	/**
	 * Give the processors the chance to print statistics etc.
	 */
	void finished();
}
