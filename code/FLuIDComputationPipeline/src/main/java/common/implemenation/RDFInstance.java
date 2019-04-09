package common.implemenation;


import common.IInstanceElement;
import common.IQuint;
import common.IResource;
import utils.implementation.Constants;

import java.util.HashSet;
import java.util.Set;

/**
 * An instance which encapsulates a multitude of statements made about it
 *
 * @author Bastian
 *
 */
public class RDFInstance implements IInstanceElement {

	public static IResource createLocator(IResource resource){
		return new TypedResource(resource, RESOURCE_TYPE);
	}
	private Set<IQuint> outgoingQuints;
	private Set<IQuint> incomingQuints;

	private IResource resource;

	public static String RESOURCE_TYPE = "Instance";

	/**
	 * Constructs a new instance, which can be referenced by the given locator
	 *
	 * @param locator The locator for this instance
	 */
	public RDFInstance(IResource locator) {
		this.resource = createLocator(locator);
		outgoingQuints = new HashSet<>();
		incomingQuints = new HashSet<>();
	}

	@Override
	public Set<IQuint> getOutgoingQuints() {
		return outgoingQuints;
	}
	@Override
	public Set<IQuint> getIncomingQuints() {
		return incomingQuints;
	}

	@Override
	public void addOutgoingQuint(IQuint q) {
		outgoingQuints.add(q);
	}
	@Override
	public void addIncomingQuint(IQuint q) {
		incomingQuints.add(q);
	}

	@Override
	public IInstanceElement clone() {
		IInstanceElement element = new RDFInstance(getLocator());
		for(IQuint quint : getOutgoingQuints())
			element.addOutgoingQuint(quint);
		for(IQuint quint : getIncomingQuints())
			element.addIncomingQuint(quint);
		return element;
	}

	@Override
	public String toString() {
		Set<String> contexts = new HashSet<>();
		Set<String> typeSet = new HashSet<>();
		for (IQuint quint : outgoingQuints) {
			contexts.add(quint.getContext().toString());
			if(quint.getPredicate().toString().equals(Constants.RDF_TYPE))
				typeSet.add(quint.getObject().toString());

		}

		return "Instance: " + resource + "\n" + "\tOutgoing: " + outgoingQuints.size()
				+ " Incoming: " + incomingQuints.size() +" Types: "+ typeSet + " Contexts: " + contexts;
	}

	@Override
	public IResource getLocator() {
		return resource;
	}

}
