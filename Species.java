import java.util.*;

public class Species implements CompatibilityDistanceCalculator {

  // See http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf on determining species
  private static float c1 = 1f;
  private static float c2 = 1f;
  private static float c3 = 1f;

  // Compatibility Distance Cutoff
  private static float delta = 3.0f;

  private Agent mascot;
  private ArrayList<Agent> members;

  public Species() {
    this.mascot = new Agent();
    this.members = new ArrayList<Agent>();
  }

  public ArrayList<Agent> getMembers() {
    return this.members;
  }

  public void addMember(Agent a) {
    this.members.add(a.copy());
  }

  public Agent getMascot() {
    return this.mascot;
  }

  public void setMascot(Agent a) {
    this.mascot = a.copy();
  }

  public boolean shouldContain(Agent a) {
    // TODO: compare the mascot to the agent using the equation

    Agent a1;
    Agent a2;

    if (a.getFitness() > mascot.getFitness()) {
      a1 = a;
      a2 = mascot;
    } else {
      a1 = mascot;
      a2 = a;
    }

    int D = disjointGeneCount(a1.getGenome(), a2.getGenome());
    int E = excessGeneCount(a1.getGenome(), a2.getGenome());
    float W = averageWeightDifference(a1.getGenome(), a2.getGenome());
    int N = genomeSize(a1.getGenome());
    float compatibilityDifference = (c1 * (float) E) / ((float) N) + (c2 * (float) D) / ((float) N) + (c3*W);

    System.out.println(compatibilityDifference);

    return compatibilityDifference < delta;

  }

  public Agent getStrongestMember() {
    Agent bestAgent = new Agent();
    for (Agent a : this.members) {
      if (a.getFitness() > bestAgent.getFitness()) {
        bestAgent = a;
      }
    }
    return bestAgent;
  }

}
