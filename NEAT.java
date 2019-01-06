import java.util.*;

public class NEAT {

  private Integer memberCnt;
  private ArrayList<Species> generation;
  private EvolutionTracker nodeInnovationNumber;
  private EvolutionTracker connectionInnovationNumber;
  private Random r;
  //private ArrayList<ArrayList<Species>> previousGenerations;

  public NEAT(Integer memberCnt, Integer inputs, Integer outputs) {
    this.memberCnt = memberCnt;
    this.generation = new ArrayList<Species>();
    nodeInnovationNumber = new EvolutionTracker();
    connectionInnovationNumber = new EvolutionTracker();
    r = new Random();
    this.generation = generateFirstGeneration(inputs, outputs);
    printGeneration();
  }

  private ArrayList<Species> generateFirstGeneration(Integer inputs, Integer outputs) {

    // Generate the genomes
    ArrayList<Agent> agents = generateStartingGenomes(inputs, outputs);

    // Place the agents into species based on their genomes
    ArrayList<Species> generation = placeIntoStartingSpecies(agents);

    return generation;

  }

  private ArrayList<Species> placeIntoStartingSpecies(ArrayList<Agent> agents) {

    ArrayList<Species> generation = new ArrayList<Species>();
    Species startingSpecies = new Species();
    boolean wasInSpecies;

    startingSpecies.addMember(agents.get(0));
    startingSpecies.setMascot(agents.get(0));
    generation.add(startingSpecies);

    for (int i = 1; i < agents.size(); i++) {
      wasInSpecies = false;
      Agent a = agents.get(i);
      for (Species s : generation) {
        if (s.shouldContain(a)) { // Add the member to the species
          s.addMember(a);
          wasInSpecies = true;
        }
      }
      if (!wasInSpecies) { // Create a new species, add the member and add the species to the generation
        Species newSpecies = new Species();
        newSpecies.addMember(a);
        newSpecies.setMascot(a);
        generation.add(newSpecies);
      }
    }

    return generation;
  }

  private ArrayList<Agent> generateStartingGenomes(Integer inputs, Integer outputs) {
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

  private void printAgents() {
    for (Agent a : this.getAgents()) {
      Test.printGenome(a.getGenome());
    }
  }

  private void printGeneration() {
    for (Species s : this.generation) {
      for (Agent a : s.getMembers()) {
        Test.printGenome(a.getGenome());
      }
      System.out.println("*************************");
    }
  }

}
