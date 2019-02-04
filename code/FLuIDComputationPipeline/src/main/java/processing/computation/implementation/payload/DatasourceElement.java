package processing.computation.implementation.payload;

import common.implemenation.NodeResource;
import common.interfaces.IPayloadElement;
import common.interfaces.IResource;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Resource;
import utils.implementation.FLuIDVocabulary;
import utils.implementation.Hash;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;


/**
 * Storing all context information in one PAYLOAD element
 */
@RDFBean(FLuIDVocabulary.CLASS_DATASOURCE_ELEMENT)
public class DatasourceElement implements IPayloadElement {
    //id
    private IResource resource;
    //PAYLOAD
    private Set<IResource> contexts;

    public DatasourceElement() {
        this.contexts = new HashSet<>();
    }

    /**
     * @param contexts
     */
    public DatasourceElement(Set<IResource> contexts) {
        this.contexts = contexts;
    }

    @RDFSubject
    public String getURI() {
        return FLuIDVocabulary.createSubjectURIPayloadPrefix(this.getClass()) + getLocator().toString();
    }
    public void setURI(String uri) {
        //Nothing todo here
    }

    public Set<IResource> contexts() {
        return contexts;
    }

    public void contexts(Set<IResource> contexts) {
        this.contexts = contexts;
    }

    @RDF(FLuIDVocabulary.PAYLOAD)
    public Set<URI> getPayload(){
        Set<URI> payload = new HashSet<>();
        contexts.forEach(C -> {
            try {
                payload.add(new URI(C.toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        return payload;
    }
    public void setPayload(Set<URI> payload){
        contexts = new HashSet<>();
        payload.forEach(P -> contexts.add(new NodeResource(new Resource(P.toString()))));
    }
    @Override
    public void merge(IPayloadElement payloadElement) {
        if(payloadElement != null && payloadElement instanceof DatasourceElement)
            this.contexts.addAll(((DatasourceElement)payloadElement).contexts());

    }

    @Override
    public IResource getLocator() {
        return new IResource() {
            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof IResource))
                    return false;
                IResource r = (IResource) obj;
                return this.toString().equals(r.toString());
            }

            @Override
            public int hashCode() {
                return contexts.hashCode();
            }


            @Override
            public String toString() {
                return Hash.md5(Integer.toString(this.hashCode()));
            }

            @Override
            public String toN3() {
                return (new BNode(toString())).toString();
            }

        };
    }

    @Override
    public String toString() {
        return "DatasourceElement{" +
                "contexts=" + contexts +
                '}';
    }
}
