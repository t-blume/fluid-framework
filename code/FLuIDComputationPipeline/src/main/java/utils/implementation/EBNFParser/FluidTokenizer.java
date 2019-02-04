/*
 * FluidTokenizer.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 */

package utils.implementation.EBNFParser;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A character stream tokenizer.
 *
 * @author   Till Blume, <tbl@informatik.uni-kiel.de>
 * @version  1.0
 */
public class FluidTokenizer extends Tokenizer {

    /**
     * Creates a new tokenizer for the specified input stream.
     *
     * @param input          the input stream to read
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    public FluidTokenizer(Reader input) throws ParserCreationException {
        super(input, false);
        createPatterns();
    }

    /**
     * Initializes the tokenizer by creating all the token patterns.
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        TokenPattern  pattern;

        pattern = new TokenPattern(FluidConstants.UNDIRECTED,
                                   "UNDIRECTED",
                                   TokenPattern.STRING_TYPE,
                                   "u-");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.INCOMING,
                                   "INCOMING",
                                   TokenPattern.STRING_TYPE,
                                   "i-");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.OUTGOING,
                                   "OUTGOING",
                                   TokenPattern.STRING_TYPE,
                                   "o-");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.REL_OP,
                                   "REL_OP",
                                   TokenPattern.STRING_TYPE,
                                   "_");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.BISIM_OP,
                                   "BISIM_OP",
                                   TokenPattern.STRING_TYPE,
                                   "*");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.INSTANCE_OP,
                                   "INSTANCE_OP",
                                   TokenPattern.STRING_TYPE,
                                   "|");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.UNION,
                                   "UNION",
                                   TokenPattern.STRING_TYPE,
                                   " U ");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.INTERSECTION,
                                   "INTERSECTION",
                                   TokenPattern.STRING_TYPE,
                                   " I ");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.CSE_SEP,
                                   "CSE_SEP",
                                   TokenPattern.STRING_TYPE,
                                   ",");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.CSE_OPEN,
                                   "CSE_OPEN",
                                   TokenPattern.STRING_TYPE,
                                   "(");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.CSE_CLOSE,
                                   "CSE_CLOSE",
                                   TokenPattern.STRING_TYPE,
                                   ")");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.OBJECT_CLUSTER,
                                   "OBJECT_CLUSTER",
                                   TokenPattern.STRING_TYPE,
                                   "OC");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.PROPERTY_CLUSTER,
                                   "PROPERTY_CLUSTER",
                                   TokenPattern.STRING_TYPE,
                                   "PC");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.PROPERTYOBJECT_CLUSTER,
                                   "PROPERTYOBJECT_CLUSTER",
                                   TokenPattern.STRING_TYPE,
                                   "POC");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.TAUTOLOGY,
                                   "TAUTOLOGY",
                                   TokenPattern.STRING_TYPE,
                                   "T");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.IDENTITY,
                                   "IDENTITY",
                                   TokenPattern.STRING_TYPE,
                                   "=");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.TYPES,
                                   "TYPES",
                                   TokenPattern.STRING_TYPE,
                                   "type");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.RELATIONS,
                                   "RELATIONS",
                                   TokenPattern.STRING_TYPE,
                                   "rel");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.RDFS,
                                   "RDFS",
                                   TokenPattern.STRING_TYPE,
                                   "rdfs");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.SAME_AS,
                                   "SAME_AS",
                                   TokenPattern.STRING_TYPE,
                                   "~");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.RELATED_PROPERTY,
                                   "RELATED_PROPERTY",
                                   TokenPattern.STRING_TYPE,
                                   "p");
        addPattern(pattern);

        pattern = new TokenPattern(FluidConstants.NUMBER,
                                   "NUMBER",
                                   TokenPattern.REGEXP_TYPE,
                                   "[0-9]+");
        addPattern(pattern);
    }
}
