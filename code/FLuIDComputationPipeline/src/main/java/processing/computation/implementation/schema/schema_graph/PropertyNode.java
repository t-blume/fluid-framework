package processing.computation.implementation.schema.schema_graph;

import utils.implementation.Hash;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Blume Till on 16.11.2016.
 */
public class PropertyNode {

    private final String property;

    private Set<PropertyNode> domain;
    private Set<PropertyNode> range;
    private Set<PropertyNode> subPropertyOf;


    public PropertyNode(String property){
        this.property = property;
        this.domain = new HashSet<>();
        this.range = new HashSet<>();
        this.subPropertyOf = new HashSet<>();
    }

    public String getProperty(){
        return property;
    }

    public Set<String> getDomain() {
        return domain.stream().map(PropertyNode::getProperty).collect(Collectors.toCollection(HashSet::new));
    }

    public void addDomain(PropertyNode domain) {
        this.domain.add(domain);
    }

    public Set<String> getRange() {
        return range.stream().map(PropertyNode::getProperty).collect(Collectors.toCollection(HashSet::new));
    }

    public void addRange(PropertyNode range) {
        this.range.add(range);
    }

    public Set<String> getSubPropertyOf() {
        Set<PropertyNode> recursiveNodes = getSuperProperties(new HashSet<>());
        return recursiveNodes.stream().map(PropertyNode::getProperty).collect(Collectors.toCollection(HashSet::new));
    }

    public void addSubPropertyOf(PropertyNode subPropertyOf) {
        this.subPropertyOf.add(subPropertyOf);
    }


    private Set<PropertyNode> getSuperProperties(Set<PropertyNode> knownPropertyNodes){
        Set<PropertyNode> recursiveNodes = new HashSet<>();
        subPropertyOf.forEach(PN -> {
            if(!knownPropertyNodes.contains(PN))    //only add PropertyNodes which are new
                recursiveNodes.add(PN);
        });
        knownPropertyNodes.addAll(recursiveNodes); //those are now known as well
        for(PropertyNode propertyNode : recursiveNodes) //iterate over all nodes added in this step
            knownPropertyNodes.addAll(propertyNode.getSuperProperties(knownPropertyNodes));

        return knownPropertyNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyNode that = (PropertyNode) o;

        return property.equals(that.property);
    }

    @Override
    public int hashCode() {
        return Hash.md5(property).hashCode();
    }
}
