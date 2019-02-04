package processing.preprocessing.implementation;


import common.implemenation.OWLSameAsInstance;
import common.implemenation.Quad;
import common.interfaces.IInstanceElement;
import common.interfaces.IQuint;

import static utils.implementation.Constants.OWL_SameAs;


/**
 * Aggregates information of instances
 * 
 * @author Bastian
 *
 */
public class InstanceAggregatorOWLSameAs extends InstanceAggregator {


	public InstanceAggregatorOWLSameAs(boolean useIncomingProps) {
		super(useIncomingProps);
	}

	@Override
	public void finishedQuint(IQuint i) {
		super.finishedQuint(i);
		//insert owl:sameAS ALWAYS as symmetric relation
		if(i.getPredicate().toString().equals(OWL_SameAs))
			addQuint2Cache(new Quad(i.getObject(), i.getPredicate(), i.getSubject(), i.getContext()), false);
	}

	@Override
	protected IInstanceElement createInstance(IQuint quint){
		return new OWLSameAsInstance(quint.getSubject());
	}

	@Override
	public void finished() {
		window.close();
	}
}
