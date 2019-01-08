import java.util.*;
import java.lang.*;
import javax.imageio.ImageIO;

public class Test {

  public static void main(String[] args) {

    Random r = new Random();

    NEAT test = new NEAT(100, 2, 1);

    for (int i = 0; i < 100; i++) {
      System.out.println("Generation " + (i+1));
      for (Agent a : test.getAgents()) {
        a.setFitness(10);
        for (int j = 0; j < 10; j++) {

          float highestNumber = 1f;
          float lowestNumber = 0f;
          float guess = 0f;

          float randomNumber = r.nextFloat();

          //System.out.println("The random number is " + randomNumber);

          for (int k = 0; k < 3; k++) {

            ArrayList<Float> inputs = new ArrayList<Float>();
            inputs.add(highestNumber);
            inputs.add(lowestNumber);

            guess = a.getGenome().calculateOutputs(inputs).get(0);
            //System.out.println("Round " + (k+1) + ", Guessed " + guess);

            if (guess > randomNumber) {
              highestNumber = guess;
            } else {
              lowestNumber = guess;
            }

          }

          //System.out.println("Off by " + Math.abs(guess - randomNumber));
          a.setFitness(a.getFitness() - Math.abs(guess - randomNumber));

        }
        //System.out.println("Had a fitness of " + a.getFitness());
        //System.out.println(" ");
      }
      test.printBestGenome();
      //test.printGeneration();
      test.generateNextGeneration();
    }
//
//     EvolutionTracker nodeInnovation = new EvolutionTracker();
//     EvolutionTracker conInnovation = new EvolutionTracker();
//
//     Genome genome = new Genome();
//
//     genome.addNodeGene(new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation()));
//     genome.addNodeGene(new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation()));
// //    genome.addNodeGene(new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation()));
//     genome.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation()));
//
//     genome.addConnectionGene(new ConnectionGene(0, 2, 0.5f, true, conInnovation.getInnovation()));
//     genome.addConnectionGene(new ConnectionGene(1, 2, 0.5f, true, conInnovation.getInnovation()));
//     //genome.addConnectionGene(new ConnectionGene(0, 1, 0.5f, true, conInnovation.getInnovation()));
//
//     printGenome(genome);
//
//     Genome genome1 = genome.copy();
//     Genome genome2 = genome.copy();
//
//      //genome1.weightMutation(r, 0.5f);
//      //genome1.weightMutation(r, 0.5f);
//     // genome2.weightMutation(r, 0.5f);
//     // genome2.weightMutation(r, 0.5f);
//
//      genome1.addNodeMutation(r, nodeInnovation, conInnovation);
//      //genome1.addNodeMutation(r, nodeInnovation, conInnovation);
//      genome2.addNodeMutation(r, nodeInnovation, conInnovation);
//      genome2.addNodeMutation(r, nodeInnovation, conInnovation);
//      //genome2.addNodeMutation(r, nodeInnovation, conInnovation);
//
//     //genome1.addConnectionMutation(r, conInnovation);
//     //genome2.addConnectionMutation(r, conInnovation);
//
//     //printGenome(genome1);
//     //printGenome(genome2);
//
//     Agent a1 = new Agent();
//     a1.setGenome(genome1);
//     Agent a2 = new Agent();
//     a2.setGenome(genome2);
//
//     Species s = new Species();
//     s.setMascot(a1);
//     //s.shouldContain(a1);
//     //s.shouldContain(a2);

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
