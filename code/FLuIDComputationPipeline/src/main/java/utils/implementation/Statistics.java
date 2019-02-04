package utils.implementation;

public class Statistics {


//    public static void main(String[] args) {
//        //Repository repository = new HTTPRepository("http://localhost:8080/rdf4j-server/", "testing");
//        Repository repository = new SailRepository(new NativeStore(new File("testing2")));
//
//        repository.initialize();
//        ValueFactory valueFactory = repository.getValueFactory();
//        RepositoryConnection connection = repository.getConnection();
//        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
//        ParserConfig config = parser.getParserConfig();
//        config.addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
//        connection.setParserConfig(config);
//
//        RepositoryResult<Statement> complexSchemaElements = connection.getStatements(null,
//                valueFactory.createIRI(RDF_TYPE), valueFactory.createIRI(FLuIDVocabulary.CLASS_COMPLEX_SCHEMA_ELEMENT));
//
//
//        //start with CSE since we actually want to track what happens when 1 instance changes.
//        //1 instance change, makes for example 2 simple schema elements change, which then can requiere different CSEs to
//        //change. Thus, we aggregate into buckets of Complex Schema Elements and then into simple schema elements.
//        HashMap<String, HashMap<String, Integer>> subgraphStatistic = new HashMap<>();
//        HashMap<String, Integer> referenceCountsMerged = new HashMap<>();
//
//        while (complexSchemaElements.hasNext()) {
//            HashMap<String, Integer> cseStatistic = new HashMap<>();
//            Statement complexSchemaElement = complexSchemaElements.next();
//            RepositoryResult<Statement> creatingSchemaElements = connection.getStatements(complexSchemaElement.getSubject(),
//                    valueFactory.createIRI(FLuIDVocabulary.HAS_SUBJECT_EQUIVALENCE), null);
//            while (creatingSchemaElements.hasNext()) {
//                Statement creatingElement = creatingSchemaElements.next();
//                //buckets for each cse separately
//                cseStatistic.merge(creatingElement.getObject().stringValue(), 1, (OLD, NEW) -> OLD + NEW);
//                //buckets for each schema element
//                referenceCountsMerged.merge(creatingElement.getObject().stringValue(), 1, (OLD, NEW) -> OLD + NEW);
//
//                RepositoryResult<Statement> otherAffectedElements = connection.getStatements(null,
//                        valueFactory.createIRI(FLuIDVocabulary.GET_LINK_OBJECT), creatingElement.getObject());
//
//                while (otherAffectedElements.hasNext()) {
//                    otherAffectedElements.next();
//                    cseStatistic.merge(creatingElement.getObject().stringValue(), 1, (OLD, NEW) -> OLD + NEW);
//                    referenceCountsMerged.merge(creatingElement.getObject().stringValue(), 1, (OLD, NEW) -> OLD + NEW);
//                }
//            }
//            subgraphStatistic.put(complexSchemaElement.getSubject().toString(), cseStatistic);
//        }
//
//        //subgraphStatistic.entrySet().forEach(E -> System.out.println(E.getKey() + ": " + E.getValue()));
//
//        int min = Integer.MAX_VALUE;
//        int max = Integer.MIN_VALUE;
//        int sum = 0;
//        int count = 0;
//        for (HashMap<String, Integer> map : subgraphStatistic.values()) {
//            int tmpK = 0;
//            for (Integer i : map.values())
//                tmpK += i;
//
//            //this is the maximum sub-graph width for one instance change that was summarized here
//            min = Math.min(min, tmpK);
//            max = Math.max(max, tmpK);
//            sum += tmpK;
//            count++;
//        }
//
//        double avg = (double) sum / (double) count;
//
//        //variance
//        double varianceSum = 0.0;
//        for (HashMap<String, Integer> map : subgraphStatistic.values()) {
//            double tmpK = 0.0;
//            for (Integer i : map.values())
//                tmpK += (double) i;
//
//            varianceSum += Math.pow(tmpK - avg, 2);
//        }
//        double variance = varianceSum / (double) count;
//
//        System.out.println("Cnt: " + count);
//        System.out.println("Min: " + min);
//        System.out.println("Max: " + max);
//        System.out.println("Avg: " + avg);
//        System.out.println("Var: " + variance);
//        System.out.println("_________");
////        referenceCountsMerged.entrySet().forEach(E -> System.out.println(E.getKey() + ": " + E.getValue()));
//
//
//        min = Integer.MAX_VALUE;
//        max = Integer.MIN_VALUE;
//        sum = 0;
//        count = 0;
//        for (Integer i : referenceCountsMerged.values()) {
//            int tmpK = i;
//
//            //this is the maximum sub-graph width for one instance change that was summarized here
//            min = Math.min(min, tmpK);
//            max = Math.max(max, tmpK);
//            sum += tmpK;
//            count++;
//        }
//
//        avg = (double) sum / (double) count;
//
//        //variance
//        varianceSum = 0.0;
//        for (Integer i : referenceCountsMerged.values()) {
//            double tmpK = (double) i;
//
//
//            varianceSum += Math.pow(tmpK - avg, 2);
//        }
//        variance = varianceSum / (double) count;
//
//        System.out.println("Cnt: " + count);
//        System.out.println("Min: " + min);
//        System.out.println("Max: " + max);
//        System.out.println("Avg: " + avg);
//        System.out.println("Var: " + variance);
//        System.out.println("_________");
//    }


    /**
     * TODO Double check problems with beans framework
     *         RDFBeanManager beanManager = new RDFBeanManager(connection);
     *
     *
     *         int isSubject = 0;
     *         int isPrediacte = 0;
     *         int isObject = 0;
     *         try {
     *             CloseableIteration<ObjectCluster, Exception> iterator = beanManager.getAll(ObjectCluster.class);
     *             while (iterator.hasNext()) {
     *                 ObjectCluster oc = iterator.next();
     *                 int tmp;
     *
     *                 System.out.println("OC: " + oc);
     * //                oc.subjectElements().values().forEach(X -> System.out.println(X));
     *                 tmp = oc.getIsSubjectEquivalenceOfInternal().size();
     *                 System.out.println(tmp);
     * //                System.out.println("<<<<");
     * //                isSubject += tmp;
     *
     *             }
     *         } catch (RDFBeanException e) {
     *             e.printStackTrace();
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *         }
     */
}
