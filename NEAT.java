import java.util.*;

public class NEAT {

  private Integer memberCnt;
  private ArrayList<Species> generation;
  private EvolutionTracker nodeInnovationNumber;
  private EvolutionTracker connectionInnovationNumber;
  private Random r;
  //private ArrayList<ArrayList<Species>> previousGenerations;

  public NEAT(Integer memberCnt, Integer speciesCnt, Integer inputs, Integer outputs) {
    this.memberCnt = memberCnt;
    this.generation = new ArrayList<Species>();
    nodeInnovationNumber = new EvolutionTracker();
    connectionInnovationNumber = new EvolutionTracker();
    r = new Random();
    this.generation = generateFirstGeneration(speciesCnt, inputs, outputs);
  }

  private ArrayList<Species> generateFirstGeneration(Integer speciesCnt, Integer inputs, Integer outputs) {

    ArrayList<Agent> agents = generateStartingNetworks(inputs, outputs);

    for (Agent a : agents) {
      Test.printGenome(a.getGenome());
    }

    return new ArrayList<Species>();

  }

  private ArrayList<Agent> generateStartingNetworks(Integer inputs, Integer outputs) {
    Genome g = new Genome();
    ArrayList<NodeGene> inputNodes = new ArrayList<NodeGene>();
    ArrayList<NodeGene> outputNodes = new ArrayList<NodeGene>();
    ArrayList<ConnectionGene> connections = new ArrayList<ConnectionGene>();

    // Create the input nodes
    for (int i = 0; i < inputs; i++) {
      NodeGene inputNode = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovationNumber.getInnovation());
      inputNodes.add(inputNode);
    }

    // Create the output nodes
    for (int i = 0; i < outputs; i++) {
      NodeGene outputNode = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovationNumber.getInnovation());
      outputNodes.add(outputNode);
    }

    // Create the connection genes
    for (int i = 0; i < inputNodes.size(); i++) {
      for (int o = 0; o < outputNodes.size(); o++) {
        // TODO: Random weight? Any other differences between them?
        ConnectionGene connection = new ConnectionGene(inputNodes.get(i).getInnovationNumber(), outputNodes.get(o).getInnovationNumber(), 1, true, connectionInnovationNumber.getInnovation());
        connections.add(connection);
      }
    }

    // Create the list of agents
    ArrayList<Agent> agents = new ArrayList<Agent>();
    for (int member = 0; member < memberCnt; member++) {
      Agent agent = new Agent();
      for (int i = 0; i < inputNodes.size(); i++) {
        agent.getGenome().addNodeGene(inputNodes.get(i).copy());
      }
      for (int i = 0; i < outputNodes.size(); i++) {
        agent.getGenome().addNodeGene(outputNodes.get(i).copy());
      }
      for (int i = 0; i < connections.size(); i++) {
        agent.getGenome().addConnectionGene(connections.get(i).copy());
      }
      agents.add(agent);
    }

    // Make the genomes at least slightly unique
    float weight;
    for (Agent a : agents) {
      for (ConnectionGene con : a.getGenome().getConnectionGenes().values()) {
        weight = (r.nextFloat() * 2f) - 1f;
        con.setWeight(weight);
      }
    }

    return agents;
  }


  public ArrayList<Agent> getAgents() {
    ArrayList<Agent> agents = new ArrayList<Agent>();
    for (Species s : generation) {
      for (Agent a  : s.getMembers()) {
        agents.add(a);
      }
    }
    return agents;
  }

}
