package processing.computation.implementation.schema.schema_graph;


import utils.implementation.Hash;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Blume Till on 16.11.2016.
 */
public class TypeNode {

    private final String type;
    private Set<TypeNode> subClassOf;


    public TypeNode(String type) {
        this.type = type;
        subClassOf = new HashSet<>();
    }

    public String getType(){
        return type;
    }

    public Set<String> getSubClassOf(){
        Set<TypeNode> recursiveNodes = getSuperClasses(new HashSet<>());
        return recursiveNodes.stream().map(TypeNode::getType).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * added parameter to prevent circles
     * @return
     */
    private Set<TypeNode> getSuperClasses(Set<TypeNode> knownTypeNodes){
        Set<TypeNode> recursiveNodes = new HashSet<>();
        subClassOf.forEach(TN -> {
            if(!knownTypeNodes.contains(TN))    //only add TypeNodes which are new
                recursiveNodes.add(TN);
        });
        knownTypeNodes.addAll(recursiveNodes); //those are now known as well
        for(TypeNode typeNode : recursiveNodes) //iterate over all nodes added in this step
            knownTypeNodes.addAll(typeNode.getSuperClasses(knownTypeNodes));

        return knownTypeNodes;
    }

    public void addSubClassOf(TypeNode subClassOf) {
        this.subClassOf.add(subClassOf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeNode typeNode = (TypeNode) o;

        return type.equals(typeNode.type);

    }

    @Override
    public int hashCode() {
        return Hash.md5(type).hashCode();
    }
}
