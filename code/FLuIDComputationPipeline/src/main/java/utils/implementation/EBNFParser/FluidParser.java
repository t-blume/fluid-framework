/*
 * FluidParser.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 */

package utils.implementation.EBNFParser;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.RecursiveDescentParser;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A token stream parser.
 *
 * @author   Till Blume, <tbl@informatik.uni-kiel.de>
 * @version  1.0
 */
public class FluidParser extends RecursiveDescentParser {

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_1 = 3001;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_2 = 3002;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_3 = 3003;

    /**
     * Creates a new parser with a default analyzer.
     *
     * @param in             the input stream to read from
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public FluidParser(Reader in) throws ParserCreationException {
        super(in);
        createPatterns();
    }

    /**
     * Creates a new parser.
     *
     * @param in             the input stream to read from
     * @param analyzer       the analyzer to use while parsing
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public FluidParser(Reader in, FluidAnalyzer analyzer)
        throws ParserCreationException {

        super(in, analyzer);
        createPatterns();
    }

    /**
     * Creates a new tokenizer for this parser. Can be overridden by a
     * subclass to provide a custom implementation.
     *
     * @param in             the input stream to read from
     *
     * @return the tokenizer created
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    protected Tokenizer newTokenizer(Reader in)
        throws ParserCreationException {

        return new FluidTokenizer(in);
    }

    /**
     * Initializes the parser by creating all the production patterns.
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        ProductionPattern             pattern;
        ProductionPatternAlternative  alt;

        pattern = new ProductionPattern(FluidConstants.SCHEMA_ELEMENT,
                                        "SCHEMA_ELEMENT");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.SIMPLE_SCHEMA_ELEMENT, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.COMPLEX_SCHEMA_ELEMENT, 1, 1);
        alt.addProduction(SUBPRODUCTION_1, 0, -1);
        alt.addProduction(FluidConstants.INSTANCE_PARAM, 0, 1);
        alt.addProduction(FluidConstants.BISIM_PARAM, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.SIMPLE_SCHEMA_ELEMENT,
                                        "SIMPLE_SCHEMA_ELEMENT");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.DIRECTION_OP, 0, 1);
        alt.addToken(FluidConstants.OBJECT_CLUSTER, 1, 1);
        alt.addProduction(FluidConstants.LABEL_PARAM, 0, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.DIRECTION_OP, 0, 1);
        alt.addToken(FluidConstants.PROPERTY_CLUSTER, 1, 1);
        alt.addProduction(FluidConstants.LABEL_PARAM, 0, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.DIRECTION_OP, 0, 1);
        alt.addToken(FluidConstants.PROPERTYOBJECT_CLUSTER, 1, 1);
        alt.addProduction(FluidConstants.LABEL_PARAM, 0, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.BASIC_ELEMENTS, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.BASIC_ELEMENTS,
                                        "BASIC_ELEMENTS");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.TAUTOLOGY, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.IDENTITY, 1, 1);
        alt.addProduction(FluidConstants.LABEL_PARAM, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.COMPLEX_SCHEMA_ELEMENT,
                                        "COMPLEX_SCHEMA_ELEMENT");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.CSE_OPEN, 1, 1);
        alt.addProduction(FluidConstants.SIMPLE_SCHEMA_ELEMENT, 1, 1);
        alt.addProduction(SUBPRODUCTION_2, 0, -1);
        alt.addToken(FluidConstants.CSE_SEP, 1, 1);
        alt.addProduction(FluidConstants.SIMPLE_SCHEMA_ELEMENT, 1, 1);
        alt.addProduction(SUBPRODUCTION_3, 0, -1);
        alt.addToken(FluidConstants.CSE_SEP, 1, 1);
        alt.addProduction(FluidConstants.SCHEMA_ELEMENT, 1, 1);
        alt.addToken(FluidConstants.CSE_CLOSE, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.COMBINE_OP,
                                        "COMBINE_OP");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.UNION, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.INTERSECTION, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.DIRECTION_OP,
                                        "DIRECTION_OP");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.UNDIRECTED, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.INCOMING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.OUTGOING, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.BISIM_PARAM,
                                        "BISIM_PARAM");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.BISIM_OP, 1, 1);
        alt.addToken(FluidConstants.NUMBER, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.LABEL_PARAM,
                                        "LABEL_PARAM");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.REL_OP, 1, 1);
        alt.addProduction(FluidConstants.PROPERTY_SET, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.INSTANCE_PARAM,
                                        "INSTANCE_PARAM");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.INSTANCE_OP, 1, 1);
        alt.addProduction(FluidConstants.INSTANCE_SET, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.INSTANCE_SET,
                                        "INSTANCE_SET");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.SAME_AS, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.RELATED_PROPERTY, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FluidConstants.PROPERTY_SET,
                                        "PROPERTY_SET");
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.TYPES, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.RELATIONS, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FluidConstants.RDFS, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_1,
                                        "Subproduction1");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.COMBINE_OP, 1, 1);
        alt.addProduction(FluidConstants.SIMPLE_SCHEMA_ELEMENT, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.COMPLEX_SCHEMA_ELEMENT, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_2,
                                        "Subproduction2");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.COMBINE_OP, 1, 1);
        alt.addProduction(FluidConstants.SIMPLE_SCHEMA_ELEMENT, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_3,
                                        "Subproduction3");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FluidConstants.COMBINE_OP, 1, 1);
        alt.addProduction(FluidConstants.SIMPLE_SCHEMA_ELEMENT, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);
    }
}
