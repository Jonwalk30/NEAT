import java.util.*;
 
public class NodeGene {

  enum TYPE {
    INPUT,
    HIDDEN,
    OUTPUT,
    ;
  }

  private TYPE type;
  private int id;

  public NodeGene(TYPE type, int id) {
    super();
    this.type = type;
    this.id = id;
  }

  public TYPE getType() {
    return this.type;
  }

  public int getId() {
    return this.id;
  }

  public NodeGene copy() {
    return new NodeGene(this.type, this.id);
  }

}
