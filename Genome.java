import java.util.*;
 
/*

With help from:
  - http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf
  - https://www.youtube.com/watch?v=1I1eG-WLLrY&t=131s

* Mutations

  (1) Add Node by splitting a connection
  (2) Add Connection between two random nodes
  (3) Shift weight
  (4) Randomise weight
  (5) Turn connection on/off

*/
public class Genome {

  private static int maxAttempts = 10000;
  private static float shiftAmplitude = 0.1f;

  private Map<Integer, ConnectionGene> connections;
  private Map<Integer, NodeGene> nodes;

  public Genome() {
    this.connections = new HashMap<Integer, ConnectionGene>();
    this.nodes = new HashMap<Integer, NodeGene>();
  }

  public Map<Integer, ConnectionGene> getConnectionGenes() {
    return this.connections;
  }

  public Map<Integer, NodeGene> getNodeGenes() {
    return this.nodes;
  }

  public void addConnectionGene(ConnectionGene gene) {
    this.connections.put(gene.getInnovationNumber(), gene);
  }

  public void addNodeGene(NodeGene gene) {
    this.nodes.put(gene.getId(), gene);
  }

  // Adds a new node by splitting an existing connection
  public void addNodeMutation(Random r, InnovationGenerator innovation) {
    ConnectionGene connection = connections.get(r.nextInt(connections.size()));

    NodeGene inNode = nodes.get(connection.getInNode());
    NodeGene outNode = nodes.get(connection.getOutNode());

    connection.disable();

    NodeGene newNode = new NodeGene(NodeGene.TYPE.HIDDEN, nodes.size());
    ConnectionGene inToNew = new ConnectionGene(inNode.getId(), newNode.getId(), 1f, true, innovation.getInnovation());
    ConnectionGene newToOut = new ConnectionGene(newNode.getId(), outNode.getId(), connection.getWeight(), true, innovation.getInnovation());

    nodes.put(newNode.getId(), newNode);
    connections.put(inToNew.getInnovationNumber(), inToNew);
    connections.put(newToOut.getInnovationNumber(), newToOut);

  }

  // Adds a new connection between two random nodes (See mutation (2) above)
  public void addConnectionMutation(Random r, InnovationGenerator innovation) {

    int attempts = 0;
    boolean success = false;

    while(!success && attempts < maxAttempts) {

      attempts++;

      // Pick two random nodes
      NodeGene node1 = nodes.get(r.nextInt(nodes.size()));
      NodeGene node2 = nodes.get(r.nextInt(nodes.size()));

      // Pick a random weight s.t. -1 <= weight <= 1
      float weight = (r.nextFloat() * 2f) - 1f;

      // Swap them if we need to (can't have backwards connections)
      if(shouldReverse(node1, node2)) {
        NodeGene temp = node1;
        node1 = node2;
        node2 = temp;
        temp = null;
      }

      // If the connection doesn't already exist
      if(!connectionExists(node1, node2)) {
        // Make a new one and add it to the list
        ConnectionGene newConnection = new ConnectionGene(node1.getId(), node2.getId(), weight, true, innovation.getInnovation());
        connections.put(newConnection.getInnovationNumber(), newConnection);
        success = true;
      }
    }

  }

  // Quoted mutation chance of 80% and shift probability of 90% - no comment on shift amplitude
  public void weightMutation(Random r, float shiftProbability) {
    for (ConnectionGene con : this.connections.values()) {
      if (r.nextFloat() < shiftProbability) {
        shiftWeightMutation(r, con);
      } else {
        randomiseWeightMutation(r, con);
      }
    }
  }

  // Shifts the weight of a connection (See (3) above)
  public void shiftWeightMutation(Random r, ConnectionGene gene) {
    float shiftAmount = (r.nextFloat()*2f*shiftAmplitude) - shiftAmplitude;
    float newWeight = gene.getWeight() + shiftAmount;
    gene.setWeight(newWeight);
  }

  // Completely randomises the weight of a connection (See (4) above)
  public void randomiseWeightMutation(Random r, ConnectionGene gene) {
    float newWeight = (r.nextFloat()*2f) - 1f;
    gene.setWeight(newWeight);
  }

  // Flips whether a connection in on or off (See (5) above)
  // public void flipConnectionMutation() {
  //
  // }

  // Checks whether a connection between two genes should be reversed
  public boolean shouldReverse(NodeGene node1, NodeGene node2) {
    // Hidden -> Input
    if (node1.getType() == NodeGene.TYPE.HIDDEN && node2.getType() == NodeGene.TYPE.INPUT) {
      return true;
    } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.HIDDEN) {
      return true;
    } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.INPUT) {
      return true;
    } else {
      return false;
    }
  }

  // Checks whether a connection already exists between two nodes
  private boolean connectionExists(NodeGene node1, NodeGene node2) {
    for (ConnectionGene con : connections.values()) {
      if (con.getInNode() == node1.getId() && con.getOutNode() == node2.getId()) { // Exists
        return true;
      } else if (con.getInNode() == node2.getId() && con.getOutNode() == node1.getId()) { // Exists reversed
        return true;
      }
    }
    return false;
  }

  // Assumes parent 1 is the more fit parent
  public static Genome createChild(Genome parent1, Genome parent2, Random r) {

    Genome child = new Genome();

    // Add the nodes from the fitter parent to the child
    for (NodeGene node : parent1.getNodeGenes().values()) {
      child.addNodeGene(node
      .copy());
    }

    // Add the connections from the appropriate parent to the child
    for (ConnectionGene con : parent1.getConnectionGenes().values()) {
      if (parent2.getConnectionGenes().containsKey(con.getInnovationNumber())) { // Matching
        // Pick a random parent

        ConnectionGene childConGene;
        if (r.nextBoolean()) {
          // Copy parent 1's gene
          childConGene = con.copy();
        } else {
          // Copy parent 2's gene
          childConGene = parent2.getConnectionGenes().get(con.getInnovationNumber()).copy();
        }
        child.addConnectionGene(childConGene);
      } else { // Disjoint or Excess
        // Use the fitter parent1
        ConnectionGene childConGene = con.copy();
        child.addConnectionGene(childConGene);
      }
    }

    for (ConnectionGene con : parent2.getConnectionGenes().values()) {
      // Use the fitter parent1
      ConnectionGene childConGene = con.copy();
      child.addConnectionGene(childConGene);
    }
    return child;

  }

}
