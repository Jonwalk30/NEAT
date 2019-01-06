import java.util.*;


// TODO: Inter-species mating 0.001 chance
public class NEAT {

  private static float interSpeciesMating = 0.001f;
  private static float weightMutationProb = 0.8f;
  private static float shiftMutationProb = 0.9f;
  private static float flipMutationProb = 0.01f;
  private static float newNodeMutationProb = 0.03f;
  private static float newConnectionMutationProb = 0.05f;

  private Integer memberCnt;
  private ArrayList<Species> generation;
  private EvolutionTracker nodeInnovationNumber;
  private EvolutionTracker connectionInnovationNumber;
  private Random r;
  private ArrayList<ArrayList<Species>> previousGenerations;

  public NEAT(Integer memberCnt, Integer inputs, Integer outputs) {
    this.memberCnt = memberCnt;
    this.generation = new ArrayList<Species>();
    nodeInnovationNumber = new EvolutionTracker();
    connectionInnovationNumber = new EvolutionTracker();
    r = new Random();
    this.generation = generateFirstGeneration(inputs, outputs);
    previousGenerations = new ArrayList<ArrayList<Species>>();
    printGeneration();
  }

  public void generateNextGeneration() {

    ArrayList<Agent> nextAgents = new ArrayList<Agent>();

    // Crossover
    for (Species s : this.generation) {
      int toGenerate = s.getMembers().size();
      if (toGenerate >= 5) { // Really strong species so we want their best member still
        nextAgents.add(s.getStrongestMember().copy());
        toGenerate--;
      }
      if (toGenerate == 1) { // Can't mate, only 1 in species
        nextAgents.add(s.getStrongestMember().copy());
      } else { // Regular species
        while(toGenerate > 0) {
          // Pick 2 random parents
          Agent parent1 = s.getMembers().get(r.nextInt(s.getMembers().size()));
          Agent parent2 = s.getMembers().get(r.nextInt(s.getMembers().size()));
          while (parent1.getGenome() == parent2.getGenome()) {
            parent2 = s.getMembers().get(r.nextInt(s.getMembers().size()));
          }
          // Swap so that the fitter parent goes first
          if (parent2.getFitness() > parent1.getFitness()) {
            Agent temp = parent1;
            parent1 = parent2;
            parent2 = temp;
          }
          // Mate them
          Agent child = new Agent();
          child.setGenome(Genome.createChild(parent1.getGenome(), parent2.getGenome(), r));
          // Add to the list
          nextAgents.add(child);
          toGenerate--;
        }
        // TODO: Assign a mascot
      }
    }

    // Mutations
    for (Agent a : nextAgents) {
      if (r.nextFloat() < weightMutationProb) {
        a.getGenome().weightMutation(r, shiftMutationProb);
      }
      if (r.nextFloat() < newNodeMutationProb) {
        a.getGenome().addNodeMutation(r, nodeInnovationNumber, connectionInnovationNumber);
      }
      if (r.nextFloat() < newConnectionMutationProb) {
        a.getGenome().addConnectionMutation(r, connectionInnovationNumber);
      }
      // TODO: Flip mutation somehow
    }

    ArrayList<Species> nextGen = new ArrayList<Species>();

    nextGen = placeIntoSpecies(nextAgents);

    // Keep the old generation for posterity
    previousGenerations.add(this.generation);

    this.generation = nextGen;
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

  // TODO: Needs re-working s.t. the species stay the same between generations,
  // but the mascot from the last generation (randomly assigned before)
  // is used to determine who joins in this generation
  private ArrayList<Species> placeIntoSpecies(ArrayList<Agent> agents) {

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
        agent.getGenome().addNodeGene(inputNodes.get(i));
      }
      for (int i = 0; i < outputNodes.size(); i++) {
        agent.getGenome().addNodeGene(outputNodes.get(i));
      }
      for (int i = 0; i < connections.size(); i++) {
        agent.getGenome().addConnectionGene(connections.get(i));
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

  public Genome getBestGenome() {
    Agent bestAgent = new Agent();
    for (Agent a : getAgents()) {
      if (a.getFitness() > bestAgent.getFitness()) {
        bestAgent = a;
      }
    }
    return bestAgent.getGenome();
  }

  public float getMaxFitness() {
    float maxFitness = 0;
    for (Agent a : getAgents()) {
      if (a.getFitness() > maxFitness) {
        maxFitness = a.getFitness();
      }
    }
    return maxFitness;
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
