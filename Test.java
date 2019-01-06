import java.util.*;
import javax.imageio.ImageIO;

public class Test {

  public static void main(String[] args) {

    Random r = new Random();

    NEAT test = new NEAT(3, 2, 1);

  }

  public static void printGenome(Genome genome) {
    System.out.println("");
    System.out.println("Nodes:");
    for (NodeGene node : genome.getNodeGenes().values()) {
        System.out.println(node.getInnovationNumber() + " " + node.getType());
    }
    System.out.println("");
    System.out.println("Connections");
    for (ConnectionGene con : genome.getConnectionGenes().values()) {
      if (con.isExpressed()) {
        System.out.println(con.getInnovationNumber() + " " + con.getInNode() + "-" + con.getOutNode() + " " + con.getWeight());
      }
    }
    System.out.println("");
    return;
  }

}
