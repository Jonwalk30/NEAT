import java.util.*;
 
public class ConnectionGene {

  private int inNode;
  private int outNode;
  private float weight;
  private boolean expressed;
  private int innovationNumber;

  public ConnectionGene(int inNode, int outNode, float weight, boolean expressed, int innovationNumber) {
    super();
    this.inNode = inNode;
    this.outNode = outNode;
    this.weight = weight;
    this.expressed = expressed;
    this.innovationNumber = innovationNumber;
  }

  public int getInNode() {
    return this.inNode;
  }

  public int getOutNode() {
    return this.outNode;
  }

  public float getWeight() {
    return this.weight;
  }

  public boolean isExpressed() {
    return this.expressed;
  }

  public void disable() {
    expressed = false;
  }

  public void enable() {
    expressed = true;
  }

  public int getInnovationNumber() {
    return this.innovationNumber;
  }

  public void setWeight(float newWeight) {
    this.weight = newWeight;
  }

  public ConnectionGene copy() {
    return new ConnectionGene(this.inNode, this.outNode, this.weight, this.expressed, this.innovationNumber);
  }

}
