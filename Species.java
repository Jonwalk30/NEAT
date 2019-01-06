import java.util.*;

public class Species {

  // See http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf on determining species
  private static float c1 = 1f;
  private static float c2 = 1f;
  private static float c3 = 0.4f;

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
    // compare the mascot to the agent using the equation
    return true;
  }

}
