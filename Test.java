import java.util.*;
import javax.imageio.ImageIO;

public class Test {

  public static void main(String[] args) {

    Random r = new Random();

    EvolutionTracker nodeInnovation = new EvolutionTracker();
    EvolutionTracker conInnovation = new EvolutionTracker();

    Genome genome = new Genome();

    genome.addNodeGene(new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation()));
    genome.addNodeGene(new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation()));
    genome.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation()));

    genome.addConnectionGene(new ConnectionGene(0, 2, 0.5f, true, conInnovation.getInnovation()));
    genome.addConnectionGene(new ConnectionGene(1, 2, 0.5f, true, conInnovation.getInnovation()));
    genome.addConnectionGene(new ConnectionGene(0, 1, 0.5f, true, conInnovation.getInnovation()));

    printGenome(genome);

    genome.weightMutation(r, 0.5f);

    genome.addNodeMutation(r, nodeInnovation, conInnovation);

    genome.addConnectionMutation(r, conInnovation);

    printGenome(genome);

  }

  private static void printGenome(Genome genome) {
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
