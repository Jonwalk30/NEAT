import java.util.*;
import javax.imageio.ImageIO;
 
public class Test {

  public static void main(String[] args) {

    Random r = new Random();

    InnovationGenerator innovation = new InnovationGenerator();

    Genome genome = new Genome();

    genome.addNodeGene(new NodeGene(NodeGene.TYPE.INPUT, 0));
    genome.addNodeGene(new NodeGene(NodeGene.TYPE.INPUT, 1));
    genome.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, 2));

    genome.addConnectionGene(new ConnectionGene(0, 2, 0.5f, true, innovation.getInnovation()));
    genome.addConnectionGene(new ConnectionGene(1, 2, 0.5f, true, innovation.getInnovation()));
    genome.addConnectionGene(new ConnectionGene(0, 1, 0.5f, true, innovation.getInnovation()));

    printGenome(genome);

    genome.weightMutation(r, 0.5f);

    printGenome(genome);

  }

  private static void printGenome(Genome genome) {
    System.out.println("");
    System.out.println("Nodes:");
    for (NodeGene node : genome.getNodeGenes().values()) {
        System.out.println(node.getId() + " " + node.getType());
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
