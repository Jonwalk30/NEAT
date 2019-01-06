import java.util.*;

public class NodeGene {

  enum TYPE {
    INPUT,
    HIDDEN,
    OUTPUT,
    ;
  }

  private TYPE type;
  private int innovationNumber;

  public NodeGene(TYPE type, int innovationNumber) {
    this.type = type;
    this.innovationNumber = innovationNumber;
  }

  public TYPE getType() {
    return this.type;
  }

  public int getInnovationNumber() {
    return this.innovationNumber;
  }

  public NodeGene copy() {
    return new NodeGene(this.type, this.innovationNumber);
  }

}
