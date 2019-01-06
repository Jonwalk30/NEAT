import java.util.*;

public class Species {

  private Agent mascot;
  private ArrayList<Agent> members;

  public Species() {
    this.mascot = new Agent();
    this.members = new ArrayList<Agent>();
  }

  public ArrayList<Agent> getMembers() {
    return this.members;
  }

}
