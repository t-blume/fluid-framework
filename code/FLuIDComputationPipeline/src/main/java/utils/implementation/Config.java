package utils.implementation;

public class Config {
    public boolean useIncomingProperties;
    public boolean useOutgoingProperties = true;

    public int bisimulationDepth = 1;

    public boolean useSameAsInstances;
    public boolean useRelatedProperties;

    public boolean useTypeSets;
    public boolean useRelationSets;

    public Config clone() {
        Config config = new Config();
        config.useIncomingProperties = useIncomingProperties;
        config.useOutgoingProperties = useOutgoingProperties;
        config.bisimulationDepth = bisimulationDepth;
        config.useSameAsInstances = useSameAsInstances;
        config.useRelatedProperties = useRelatedProperties;
        config.useTypeSets = useTypeSets;
        config.useRelationSets = useRelationSets;
        return config;
    }
}
