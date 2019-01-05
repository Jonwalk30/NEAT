public class EvolutionTracker {

  private int currentInnovation = 0;

  public EvolutionTracker() {
    super();
  }

  public int getInnovation() {
    return this.currentInnovation++;
  }

}
