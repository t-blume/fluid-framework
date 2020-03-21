# FLUID Framework

## Disclaimer
The source code of the FLUID framework is still under active development. Some features are still missing but will be added soon. Also, the documentation is quite limited, but we are working on more examples.

## About
The Formal schema-Level Index model for the web of Data (FLUID, short for: FLexible graph sUmmarIes for Data graphs) is a mathematical model based on equivalence relations to formulate schema-level indices [1,3]. In this project, we implemented the basic building blocks, i.e., the Schema Elements along with their parameterizations in a generic, modular processing pipeline. This enables users to easily configure and compute any schema-level index.
Furthermore, the framework comes with a generic FLUID query engine that is able to perform structural queries on any index modeled with FLUID.

We implemented the schema computation in a pipeline architecture following the basic principles of the SchemEX approach [2]. However, we redesigned the approach in a way that allows to abstract from the stream-based computation approach.

The figure below outlines the basic concept of the FLUID approach. All modules, e.g., the schema computation, can be changed and implemented differently.

![framework-architecture](documents/images/fluid-framework-concept.png)

### Set up

 - Add grammatica libray to local maven repository
 	``mvn install:install-file -Dfile=libs/grammatica-1.6.jar -DgroupId=percederberg.net -DartifactId=grammatica -Dversion=1.6 -Dpackaging=jar -DgeneratePom=true``
 - Get submodule schema-graph and add jar to local maven repository
 	+ ``git submodule update --init --recursive``
 	+ ``cd code/fluid-schema-graph``
 	+ ``mvn package``
 	+ ``mvn install:install-file -Dfile=target/schema-graph-1.0.jar -DgroupId=kd.informatik -DartifactId=schema-graph -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true``

The computation pipeline and the query engine are developed as Java 8 Maven projects. We rely on the Rdf4J triple stores interface since we reuse the RDF beans framework (https://github.com/cyberborean/rdfbeans).

#### Computation Pipeline

1. Setup a Tomcat with RDF4J running
2. Create a repository
3. Run the FLUID framework with host and repository parameters
4. Profit!


#### Query Engine

This component can translate structural queries to the FLUID model.

### Development

Simplified component diagram:

![framework-components-simplified](documents/images/fluid-framework-architecture.png)


### Experiment "Indexing Data on the Web: A Comparison of Schema-level Indices for Data Search"
The experimental evaluation of schema-level indices was conducted using an earlier version of the framework. One can find the source code of version in legacy/2019-SLI-Comparison-Experiment.zip along with sample queries and detailed statistics about TimBL-11M and DyLDO-127M datasets that where used in the experiment.

### References

1. Blume, T., Scherp, A.: Towards flexible indices for distributed graph data: The formal schema-level index model FLuID. In: 30th GI-Workshop on Foundations of Databases (Grundlagen von Datenbanken). CEUR Workshop Proceedings (2018), http://ceur-ws.org/Vol-2126/paper3.pdf.
2. Konrath, M., Gottron, T., Staab, S., Scherp, A.: SchemEX - efficient construction of a data catalogue by stream-based indexing of Linked Data. J. Web Sem. 16, 52–58 (2012)
3. Blume, T., Scherp, A.: FLuID: A Meta Model to Flexibly Define Schema-level Indices for the Web of Data. CoRR abs/1908.01528 (2019)

### Acknowledgments
This research was co-financed by the EU H2020 project [MOVING](http://www.moving-project.eu/) under contract no 693092.
