import java.util.*;
import java.lang.*;

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

// TODO: Prevent explosion of innovation numbers by keeping track of whether a connection / node has already been added
public class Genome {

  private static int maxAttempts = 10000;
  private static float shiftAmplitude = 0.1f;
  private static float sigmoidModifier = 5.0f;

  private HashMap<Integer, ConnectionGene> connections;
  private HashMap<Integer, NodeGene> nodes;

  public Genome() {
    this.connections = new HashMap<Integer, ConnectionGene>();
    this.nodes = new HashMap<Integer, NodeGene>();
  }

  public HashMap<Integer, ConnectionGene> getConnectionGenes() {
    return this.connections;
  }

  public HashMap<Integer, NodeGene> getNodeGenes() {
    return this.nodes;
  }

  public void addConnectionGene(ConnectionGene gene) {
    this.connections.put(gene.getInnovationNumber(), gene);
  }

  public void addNodeGene(NodeGene gene) {
    this.nodes.put(gene.getInnovationNumber(), gene);
  }

  // Adds a new node by splitting an existing connection
  // TODO: Can squares happen? If so we in troubleeee
  // i.e. can...
  //      O
  //     / \
  //    O  O
  //     \/
  //     O
  // ever exist?
  public void addNodeMutation(Random r, EvolutionTracker nodeInnovation , EvolutionTracker conInnovation, ArrayList<ConnectionGene> allConnectionGenes) {
    //ConnectionGene connection = connections.get(r.nextInt(connections.size()));

    Collection<ConnectionGene> connectionsAsCollection = connections.values();
    ArrayList<ConnectionGene> connectionsAsArrayList = new ArrayList<ConnectionGene>(connectionsAsCollection);

    int r1 = r.nextInt(connectionsAsArrayList.size());

    ConnectionGene connection = connectionsAsArrayList.get(r1);

    NodeGene inNode = nodes.get(connection.getInNode());
    NodeGene outNode = nodes.get(connection.getOutNode());

    //System.out.println("Trying to add a node on connection " + connection.getInnovationNumber());

    connection.disable();

    boolean isANewNode = true;
    Integer newNodeInnovationNumber = 0;
    Integer inConInnovationNumber = 0;
    Integer outConInnovationNumber = 0;

    if (allConnectionGenes != null) {
      ArrayList<ConnectionGene> possibleInputCons = new ArrayList<ConnectionGene>();
      ArrayList<ConnectionGene> possibleOutputCons = new ArrayList<ConnectionGene>();
      for (ConnectionGene con : allConnectionGenes) {
        if (con.getInNode() == inNode.getInnovationNumber()) {
          possibleInputCons.add(con);
        }
      }
      for (ConnectionGene inCon : possibleInputCons) {
        //System.out.println("PossInputCon = " + inCon.getInnovationNumber());
      }
      for (ConnectionGene con : allConnectionGenes) {
        if (con.getOutNode() == outNode.getInnovationNumber()) {
          possibleOutputCons.add(con);
        }
      }
      for (ConnectionGene outCon : possibleOutputCons) {
        //System.out.println("PossOutputCon = " + outCon.getInnovationNumber());
      }
      for (ConnectionGene inCon : possibleInputCons) {
        for (ConnectionGene outCon : possibleOutputCons) {
          if (inCon.getOutNode() == outCon.getInNode()) {
            isANewNode = false;
            newNodeInnovationNumber = inCon.getOutNode();
            inConInnovationNumber = inCon.getInnovationNumber();
            outConInnovationNumber = outCon.getInnovationNumber();
          }
        }
      }
    }

    if (isANewNode) {
      newNodeInnovationNumber = nodeInnovation.getInnovation();
      inConInnovationNumber = conInnovation.getInnovation();
      outConInnovationNumber = conInnovation.getInnovation();
    }

    NodeGene newNode = new NodeGene(NodeGene.TYPE.HIDDEN, newNodeInnovationNumber);
    ConnectionGene inToNew = new ConnectionGene(inNode.getInnovationNumber(), newNode.getInnovationNumber(), 1f, true, inConInnovationNumber);
    ConnectionGene newToOut = new ConnectionGene(newNode.getInnovationNumber(), outNode.getInnovationNumber(), connection.getWeight(), true, outConInnovationNumber);

    addNodeGene(newNode);
    addConnectionGene(inToNew);
    addConnectionGene(newToOut);

    //nodes.put(newNode.getInnovationNumber(), newNode);
    //connections.put(inToNew.getInnovationNumber(), inToNew);
    //connections.put(newToOut.getInnovationNumber(), newToOut);

  }

  // Adds a new connection between two random nodes (See mutation (2) above)
  public void addConnectionMutation(Random r, EvolutionTracker innovation, ArrayList<ConnectionGene> allConnectionGenes) {

    int attempts = 0;
    boolean success = false;

    while(!success && attempts < maxAttempts) {

      attempts++;

      // Pick two random nodes
      Collection<NodeGene> nodesAsCollection = nodes.values();
      ArrayList<NodeGene> nodesAsArrayList = new ArrayList<NodeGene>(nodesAsCollection);

      int r1 = r.nextInt(nodesAsArrayList.size());
      int r2 = r.nextInt(nodesAsArrayList.size());

      NodeGene node1 = nodesAsArrayList.get(r1);
      NodeGene node2 = nodesAsArrayList.get(r2);

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
      if(!connectionExists(node1, node2)
        && node1.getInnovationNumber() != node2.getInnovationNumber()
        && (node1.getType() != NodeGene.TYPE.INPUT || node2.getType() != NodeGene.TYPE.INPUT)
        && node1.getType() != NodeGene.TYPE.OUTPUT) {

        Integer newConInnovationNumber = 0;
        boolean isANewCon = true;

        for (ConnectionGene con : allConnectionGenes) {
          if (con.getInNode() == node1.getInnovationNumber() && con.getOutNode() == node2.getInnovationNumber()) { // Exists
            isANewCon = false;
            newConInnovationNumber = con.getInnovationNumber();
          } else if (con.getInNode() == node2.getInnovationNumber() && con.getOutNode() == node1.getInnovationNumber()) { // Exists reversed
            isANewCon = false;
            newConInnovationNumber = con.getInnovationNumber();
          }
        }

        if (isANewCon) {
          newConInnovationNumber = innovation.getInnovation();
        }

        // Make a new one and add it to the list
        ConnectionGene newConnection = new ConnectionGene(node1.getInnovationNumber(), node2.getInnovationNumber(), weight, true, newConInnovationNumber);
        addConnectionGene(newConnection);
        //connections.put(newConnection.getInnovationNumber(), newConnection);
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
  public void flipConnectionMutation(Random r) {
    ConnectionGene connection = connections.get(r.nextInt(connections.size()));
    if (connection.isExpressed()) {
      connection.disable();
    } else {
      connection.enable();
    }
  }

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
      if (con.getInNode() == node1.getInnovationNumber() && con.getOutNode() == node2.getInnovationNumber()) { // Exists
        return true;
      } else if (con.getInNode() == node2.getInnovationNumber() && con.getOutNode() == node1.getInnovationNumber()) { // Exists reversed
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
      child.addNodeGene(node.copy());
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

  // TODO: Be able to calculate outputs, given inputs
  // TODO: Add a check that the right amount of inputs have been given
  // TODO: Make this depth first by swapping the order of the for loops (so that you find the value of all nodes i steps from an input)
  public ArrayList<Float> calculateOutputs(ArrayList<Float> inputs) {

    //ArrayList<ConnectionGene> coveredConnections = new ArrayList<ConnectionGene>();
    HashMap<Integer, Float> nodeValues = new HashMap<Integer, Float>();
    ArrayList<Float> outputValues = new ArrayList<Float>();

    for (NodeGene node : nodes.values()) {
      nodeValues.put(node.getInnovationNumber(), 0f);
    }

    for (NodeGene node : nodes.values()) {
      if (node.getType() == NodeGene.TYPE.INPUT) {
        // Set the node's value to input
        nodeValues.put(node.getInnovationNumber(), inputs.get(node.getInnovationNumber()));
      } else {
        // Sigmoid calculation
        float oldValue = nodeValues.get(node.getInnovationNumber());
        float newValue = (float) 1 / (float) (1 + Math.pow((double) Math.E, (double) (-sigmoidModifier * oldValue)));
        nodeValues.put(node.getInnovationNumber(), newValue);
      }
      if (node.getType() == NodeGene.TYPE.OUTPUT) {
        // If the type is output, add its value to the output list
        outputValues.add(nodeValues.get(node.getInnovationNumber()));
      } else {
        for (ConnectionGene c : this.connections.values()) {
          // Check if the connection has the current node as an input
          if (c.getInNode() == node.getInnovationNumber()) {
            // If so make the value of the output node equal to the value that we assigned to the input node * the weight
            // Also make the key equal to the innovationNumber of the node
            NodeGene toNode = nodes.get(c.getOutNode());
            //System.out.println(nodeValues);
            //System.out.println(toNode.getInnovationNumber());
            float value = nodeValues.get(toNode.getInnovationNumber());
            value = value + (nodeValues.get(toNode.getInnovationNumber()) * c.getWeight());
            nodeValues.put(toNode.getInnovationNumber(), value);
          }
        }
      }
    }
    System.out.println(outputValues);
    return outputValues;
  }

  public Genome copy() {
    Genome g = new Genome();
    for (NodeGene node : this.nodes.values()) {
      g.addNodeGene(node.copy());
    }
    for (ConnectionGene con : this.connections.values()) {
      g.addConnectionGene(con.copy());
    }
    return g;
  }

}
