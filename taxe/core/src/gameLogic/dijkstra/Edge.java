package gameLogic.dijkstra;

/** Class that represents an edge in dijkstra's algorithm*/
class Edge {
	/** The vertex that the edge ends at*/
    private final Vertex target;
    
    /** How long the edge is in pixels to target*/
    private final double weight;

    public Edge(Vertex target, double weight) {
        this.target = target;
        this.weight = weight;
    }

    public Vertex getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }
}
