package common.implemenation;


import common.interfaces.IInstanceElement;
import common.interfaces.IQuint;
import common.interfaces.IResource;

import java.util.HashSet;
import java.util.Set;

import static utils.implementation.Constants.OWL_SameAs;


/**
 * An instance which encapsulates a multitude of statements made about it
 *
 * @author Bastian
 */
public class OWLSameAsInstance extends RDFInstance {

    private Set<IResource> owlSameAsInstances;

    public OWLSameAsInstance(IResource locator) {
        super(locator);
        owlSameAsInstances = new HashSet<>();
    }

    @Override
    public void addOutgoingQuint(IQuint q) {
        if (q.getPredicate().toString().equals(OWL_SameAs))
            owlSameAsInstances.add(q.getObject());
        else
            super.addOutgoingQuint(q);
    }

    @Override
    public void addIncomingQuint(IQuint q) {
        if (q.getPredicate().toString().equals(OWL_SameAs))
            owlSameAsInstances.add(q.getObject());
        else
            super.addIncomingQuint(q);
    }

    @Override
    public IInstanceElement clone() {
        OWLSameAsInstance element = new OWLSameAsInstance(getLocator());
        for (IQuint quint : getOutgoingQuints())
            element.addOutgoingQuint(quint);
        for (IQuint quint : getIncomingQuints())
            element.addIncomingQuint(quint);
        for (IResource resource : getOWLSameAsInstances())
            element.addOWLSameAs(resource);
        return element;
    }

    public void addOWLSameAs(IResource target) {
        owlSameAsInstances.add(target);
    }

    public Set<IResource> getOWLSameAsInstances() {
        return owlSameAsInstances;
    }

    @Override
    public String toString() {
        return super.toString() + " sameAs: " + owlSameAsInstances.size();
    }

}
