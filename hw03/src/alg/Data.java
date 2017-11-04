package alg;

import java.util.*;

public class Data {
    private final int numberOfNodes;
    private final int numberOfConnections;
    private final Map<Integer, Set<Integer>> connections;

    public Data(int numberOfNodes, int numberOfConnections, Map<Integer, Set<Integer>> connections) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfConnections = numberOfConnections;
        this.connections = connections;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getNumberOfConnections() {
        return numberOfConnections;
    }

    public Map<Integer, Set<Integer>> getConnections() {
        return connections;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(numberOfNodes).append(" ").append(numberOfConnections).append("\n");

        for (int key : connections.keySet()) {
            sb.append(key).append(": ");

            for (int connected : connections.get(key)) {
                sb.append(connected).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}

class DataBuilder {
    private int numberOfNodes;
    private int numberOfConnections;
    private Map<Integer, Set<Integer>> connections = new LinkedHashMap<>();

    public Data build() {
        return new Data(numberOfNodes, numberOfConnections, connections);
    }

    public DataBuilder setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        return this;
    }

    public DataBuilder setnumberOfConnections(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
        return this;
    }

    private void addOneConnection(int node1, int node2){
        if (connections.containsKey(node1)) {
            connections.get(node1).add(node2);
        } else {
            Set<Integer> set = new HashSet<>();
            set.add(node2);
            connections.put(node1, set);
        }
    }

    public DataBuilder addConnection(int node1, int node2 ){
        addOneConnection(node1, node2);
        addOneConnection(node2, node1);

        return this;
    }
}
