package processing.preprocessing.implementation;


import common.implemenation.NodeResource;
import common.implemenation.Quad;
import common.interfaces.IQuint;
import common.interfaces.IResource;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.yars.nx.Resource;
import processing.preprocessing.interfaces.IQuintProcessor;
import utils.implementation.Constants;
import utils.implementation.Helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: A few SameAs statements do not comply with Definition 1 (SameAs statement), e.g. some simply connect an RDF resource to a literal string6
 */

public class NoisyDataFilter implements IQuintProcessor {
    private static final Logger logger = LogManager.getLogger(NoisyDataFilter.class.getSimpleName());

    private String prefix;
    private boolean fixLiterals;
    private boolean fixBlankNodes;

    private PrintStream noiseOut = null;

    private long removedQuints = 0;
    private long fixedLiterals = 0;
    private long fixedBlankNodes = 0;

    //TODO: merge with de-anonimyzer
    public NoisyDataFilter(String prefix, boolean fixLiterals, boolean fixBlankNodes, String output) {
        this.prefix = prefix;
        this.fixLiterals = fixLiterals;
        this.fixBlankNodes = fixBlankNodes;
        try {
            noiseOut = new PrintStream(new FileOutputStream(Helper.createFile(output + File.separator + "noise.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<IQuint> processQuint(IQuint q) {
        List<IQuint> l = new ArrayList<>();

        // Context always has to be an IRI
        if (!validIRIorBlank(q.getContext())) {
            removedQuints++;
            noiseOut.println("Removing statement (reason: 1/Invalid Context IRI):\n{" + q + "}");
            return l;
        }

        // Subject, has to be either an IRI or a blank-node; check only if subject is an IRI, i.e., "<...>"
        if (!validIRIorBlank(q.getSubject())) {
            removedQuints++;
            noiseOut.println("Removing statement (reason: 2/Invalid Subject IRI):\n{" + q + "}");
            return l;
        }
        // Predicate always has to be an IRI
        if (!validIRIorBlank(q.getPredicate())) {
            removedQuints++;
            noiseOut.println("Removing statement (reason: 3/Invalid Predicate IRI):\n{" + q + "}");
            return l;
        }
        //special cases in which object must be an IRI
        if ((q.getPredicate().toN3().matches("(<" + Constants.OWL_SameAs + ">|<" + Constants.RDF_TYPE + ">)"))
                && !validIRIorBlank(q.getObject())) {
            removedQuints++;
            noiseOut.println("Removing statement (reason: 4/Invalid Object IRI):\n{" + q + "}");
            return l;
        }
        //if this is not a literal, it has to be IRI or Blank Node
        if (!q.getObject().toN3().matches("\".*\"(@[a-z]+)?")) {
            if (!validIRIorBlank(q.getObject())) {
                noiseOut.println("Invalid Literal:\n{" + q + "}");
                //we can try to fix literals here
                if (fixLiterals) {
                    IResource possibleFixedLiteral = fixLiteral(q.getObject());
                    if (possibleFixedLiteral != null) {
                        // we could fix the literal
                        IQuint fixedQuint = new Quad(q.getSubject(), q.getPredicate(), possibleFixedLiteral, q.getContext());
                        noiseOut.println("Fixing Literal:\n{" + fixedQuint + "}");
                        l.add(fixedQuint);
                        return l;
                    }
                }
                noiseOut.println("Removing statement (reason: 5/Invalid Literal):\n{" + q + "}");
                removedQuints++;
                return l;
            }
        }
        //It is an older code, Sir, but it checks out
        if(fixBlankNodes){
            IResource c = deanon(q.getContext(), new NodeResource(new Resource(prefix)));
            IResource s = deanon(q.getSubject(), c);
            IResource p = deanon(q.getPredicate(), c);
            IResource o = deanon(q.getObject(), c);
            IQuint fixedQuint = new Quad(s, p, o, c);
            l.add(fixedQuint);
        }else
            l.add(q);

        return l;
    }

    @Override
    public void finished() {
        logger.info("Removed " + removedQuints + " invalid statements.");
        if(fixLiterals)
            logger.info("Fixed " + fixedLiterals + " invalid literals.");
        if(fixBlankNodes)
            logger.info("Fixed " + fixedBlankNodes + " Blank Nodes");
    }

    @Override
    public String toString() {
        return "NoisyDataFilter{" +
                "prefix='" + prefix + '\'' +
                ", fixLiterals=" + fixLiterals +
                ", fixBlankNodes=" + fixBlankNodes +
                '}';
    }

    ////////////////////////////////////////////////////////////////////////////
    private boolean validIRIorBlank(IResource r) {
        //Quick Fix: since plain text seems to be a valid IRI, e.g., <asdfgaf>
        //if its not a blank node, there is a dot necessary to form a absolute URI
        if (!r.toN3().contains("_:") && !r.toN3().contains("."))
            return false;


        IRIFactory iriFactory = IRIFactory.iriImplementation();
        IRI iri = iriFactory.create(r.toString());

        if (iri.hasViolation(false)) {
//            Iterator<Violation> iterator = iri.violations(false);
//            int numberOfViolations = 0;
////            boolean isMaybeBlankNode = false;
//            while (iterator.hasNext()) {
//                Violation violation = iterator.next();
////                isMaybeBlankNode = violation.getViolationCode() == 0;
//                numberOfViolations++;
//                //if there is more than 1 error, cannot be just a blank node
////                if (numberOfViolations > 1)
////                    return false;
//
//            }

            if (iri.toString().startsWith("_:")) {
                iri = iriFactory.create(r.toString().replaceAll("_:(\\/\\/)?", prefix));
                if (iri.hasViolation(false))
                    return false;

            }else
                return false;
        }
        return true;
    }

    private IResource fixLiteral(IResource l) {
        if (Helper.isLiteral(l)) {
            IRIFactory iriFactory = IRIFactory.iriImplementation();
            IRI iri = iriFactory.create(l.toString());

            boolean invalidCharacters = false;
            boolean hasUpperCase = false;
            boolean whitespaces = false;
            boolean invalidIANA_SCHEME = false;

            if (iri.hasViolation(true)) {
                Iterator<Violation> iterator = iri.violations(true);
                while (iterator.hasNext()) {
                    Violation v = iterator.next();
                    if (v.getViolationCode() == 0)
                        invalidCharacters = true;
                    else if (v.getViolationCode() == 11)
                        hasUpperCase = true;
                    else if (v.getViolationCode() == 17 || v.getViolationCode() == 18)
                        whitespaces = true;
                    else if (v.getViolationCode() == 44)
                        invalidIANA_SCHEME = true;
                }
            }
            //if all of them are true, then this is either the worst URL ever or a literal
            if (whitespaces && invalidCharacters && invalidIANA_SCHEME && hasUpperCase) {
                fixedLiterals++;
                return new NodeResource(new Resource("\""+l.toString() + "\""));
            }
        }
        return null;
    }
    private IResource deanon(IResource resource, IResource context) {
        String resourceText = resource.toString();
        if (!resourceText.matches("(http(s)?|(ftp)):\\/\\/.*")) {
            String newPrefix;
            if (context != null) {
                newPrefix = context.toString();
                // In case the context itself was a blank node -> use prefix
                if (newPrefix.startsWith("_:"))
                    newPrefix = prefix;

            } else
                newPrefix = prefix;


            if (!(newPrefix.endsWith("/") || newPrefix.endsWith("#")))
                newPrefix = newPrefix + "/";

            //remove possible blank node prefix
            resourceText = resourceText.replaceAll("_:", "");
            fixedBlankNodes++;
            return new NodeResource(new Resource(newPrefix + resourceText));
        } else
            return resource;
    }
}
