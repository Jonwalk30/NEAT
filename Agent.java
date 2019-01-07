import java.util.*;

public class Agent {

  private Genome genome;
  private float fitness;

  public Agent() {
    this.genome = new Genome();
    this.fitness = 0;
  }

  // TODO: Add NEAT as an input and adjust fitness based on the number of agents in the species
  public void setFitness(float fitness) {
    this.fitness = fitness;
  }

  public float getFitness() {
    return this.fitness;
  }

  public void setGenome(Genome genome) {
    this.genome = genome.copy();
  }

  public Genome getGenome() {
    return this.genome;
  }

  public Agent copy() {
    Agent a = new Agent();
    a.genome = this.genome.copy();
    //a.fitness = this.fitness;
    return a;
  }

}
