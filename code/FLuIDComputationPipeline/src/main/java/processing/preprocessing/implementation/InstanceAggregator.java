package processing.preprocessing.implementation;


import common.IInstanceElement;
import common.IQuint;
import common.implemenation.RDFInstance;
import common.implementation.Quad;
import processing.preprocessing.interfaces.IQuintListener;
import utils.interfaces.IElementCache;


/**
 * Aggregates information of instances
 * 
 * @author Bastian
 * @editor Till
 */
public class InstanceAggregator implements IQuintListener {

	public void setWindow(IElementCache<IInstanceElement> window) {
		this.window = window;
	}

	protected IElementCache<IInstanceElement> window;
	protected final boolean useIncomingProps;


	/**
	 * Creates an InstanceAggregator which uses the given window to store
	 * instances
	 *
	 */
	public InstanceAggregator() {
		this(false);
	}
	public InstanceAggregator(boolean useIncomingProps) {
		this.useIncomingProps = useIncomingProps;
	}

	@Override
	public void finishedQuint(IQuint i) {
		addQuint2Cache(i, true);
		//add incoming props as inverted props
		if(useIncomingProps)
			addQuint2Cache(new Quad(i.getObject(), i.getPredicate(), i.getSubject(), i.getContext()), false);

	}

	protected IInstanceElement createInstance(IQuint quint){
		return new RDFInstance(quint.getSubject());
	}

	protected void addQuint2Cache(IQuint quint, boolean asOutgoing){
		IInstanceElement element = createInstance(quint);
		if (window.contains(element.getLocator()))
			element = window.get(element.getLocator());
		else
			window.add(element);

		if(asOutgoing)
			element.addOutgoingQuint(quint);
		else
			element.addIncomingQuint(quint);
	}

	@Override
	public void finished() {
		window.close();
	}

}
