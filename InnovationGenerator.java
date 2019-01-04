public class InnovationGenerator {

  private int currentInnovation = 0;

  public InnovationGenerator() {
    super();
  }

  public int getInnovation() {
    return this.currentInnovation++;
  }

}
