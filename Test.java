import java.util.*;
import java.lang.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
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
      //test.printBestGenome();
      //test.printGeneration();
      test.generateNextGeneration();
    }
    try {
      saveGenome(test.getAgents().get(0).getGenome(), 100, test.getAgents().get(0).getFitness());
    }
    catch(Exception e) {
      System.out.println("Coulnd't write to file.");
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

  public static void saveGenome(Genome genome, int generation, float fitness) throws Exception {

    int width = 1000;
    int height = 1000;
    Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);
    String s = "Hello";
    Random r = new Random();
    int xPos, yPos, diameter = 30;

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setFont(f);
    FontRenderContext frc = g2d.getFontMetrics().getFontRenderContext();
    Rectangle2D rect = f.getStringBounds(s, frc);
    g2d.setColor(Color.white);
    g2d.fillRect(0, 0, width, height);

    // Draw nodes
    ArrayList<NodeGene> inputNodes = new ArrayList<NodeGene>();
    HashMap<ArrayList<Integer>, NodeGene> hiddenNodes = new HashMap<ArrayList<Integer>, NodeGene>();
    ArrayList<NodeGene> outputNodes = new ArrayList<NodeGene>();
    for (NodeGene node : genome.getNodeGenes().values()) {
      if (node.getType() == NodeGene.TYPE.INPUT) {
        inputNodes.add(node);
      } else if (node.getType() == NodeGene.TYPE.OUTPUT) {
        outputNodes.add(node);
      } else {
        xPos = r.nextInt(width - (width*2/5)) + width/5;
        yPos = r.nextInt(height - (height*2/5)) + height/5;

        if (hiddenNodes.values().size() < 10) {
          boolean clashing = true;
          int attempts = 0;
          int cutoff = height/10;
          while (clashing && attempts < 1000) {
            clashing = false;
            for (ArrayList<Integer> position : hiddenNodes.keySet()) {
              if ((position.get(0) + cutoff > xPos && position.get(0) - cutoff < xPos) &&
                  (position.get(1) + cutoff > yPos && position.get(1) - cutoff < yPos)) {
                  clashing = true;
                  xPos = r.nextInt(width - (width*2/5)) + width/5;
                  yPos = r.nextInt(height - (height*2/5)) + height/5;
              }
            }
            attempts++;
          }
        }

        ArrayList<Integer> position = new ArrayList<Integer>();
        position.add(xPos);
        position.add(yPos);
        hiddenNodes.put(position, node);
      }
    }

    g2d.setColor(Color.BLACK);

    int xPos1 = 0, xPos2 = 0, yPos1 = 0, yPos2 = 0, weightX = 0, weightY = 0;

    // Draw connections
    for (ConnectionGene con : genome.getConnectionGenes().values()) {
      int compNode = con.getInNode();
      for (int i = 0; i < inputNodes.size(); i++) {
        if (compNode == inputNodes.get(i).getInnovationNumber()) {
          xPos1 = ((i+1) * width) / (inputNodes.size() + 1);
          yPos1 = height - (height/10);
        }
      }
      for (int i = 0; i < outputNodes.size(); i++) {
        if (compNode == outputNodes.get(i).getInnovationNumber()) {
          xPos1 = ((i+1) * width) / (outputNodes.size() + 1);
          yPos1 = height/10;
        }
      }
      for (ArrayList<Integer> position : hiddenNodes.keySet()) {
        if (compNode == hiddenNodes.get(position).getInnovationNumber()) {
          xPos1 = position.get(0);
          yPos1 = position.get(1);
        }
      }
      compNode = con.getOutNode();
      for (int i = 0; i < inputNodes.size(); i++) {
        if (compNode == inputNodes.get(i).getInnovationNumber()) {
          xPos2 = ((i+1) * width) / (inputNodes.size() + 1);
          yPos2 = height - (height/10);
        }
      }
      for (int i = 0; i < outputNodes.size(); i++) {
        if (compNode == outputNodes.get(i).getInnovationNumber()) {
          xPos2 = ((i+1) * width) / (outputNodes.size() + 1);
          yPos2 = height/10;
        }
      }
      for (ArrayList<Integer> position : hiddenNodes.keySet()) {
        if (compNode == hiddenNodes.get(position).getInnovationNumber()) {
          xPos2 = position.get(0);
          yPos2 = position.get(1);
        }
      }
      g2d.setColor(Color.BLACK);
      g2d.drawLine(xPos1 + diameter/2, yPos1 + diameter/2, xPos2 + diameter/2, yPos2 + diameter/2);
      weightX = ((xPos1 + xPos2 + diameter) / 2) + diameter;
      weightY = (yPos1 + yPos2 + diameter) / 2;
      s = String.format("%.2f", con.getWeight());
      //s = Float.toString(con.getWeight());
      g2d.setColor(Color.GRAY);
      g2d.drawString(s, weightX, weightY);
    }

    yPos = height - (height/10);
    g2d.setColor(Color.GREEN);
    for (int i = 0; i < inputNodes.size(); i++) {
      xPos = ((i+1) * width) / (inputNodes.size() + 1);
      g2d.fillOval(xPos, yPos, diameter, diameter);
      s = Integer.toString(inputNodes.get(i).getInnovationNumber());
      g2d.setColor(Color.BLACK);
      g2d.drawString(s, xPos - diameter/2, yPos);
      g2d.setColor(Color.GREEN);
    }
    g2d.setColor(Color.GRAY);
    for (ArrayList<Integer> position : hiddenNodes.keySet()) {
      xPos = position.get(0);
      yPos = position.get(1);
      g2d.fillOval(xPos, yPos, diameter, diameter);
      g2d.setColor(Color.BLACK);
      s = Integer.toString(hiddenNodes.get(position).getInnovationNumber());
      g2d.drawString(s, xPos - diameter/2, yPos);
      g2d.setColor(Color.GRAY);
    }
    yPos = height/10;
    g2d.setColor(Color.RED);
    for (int i = 0; i < outputNodes.size(); i++) {
      xPos = ((i+1) * width) / (outputNodes.size() + 1);
      g2d.fillOval(xPos, yPos, diameter, diameter);
      s = Integer.toString(outputNodes.get(i).getInnovationNumber());
      g2d.setColor(Color.BLACK);
      g2d.drawString(s, xPos - diameter/2, yPos);
      g2d.setColor(Color.RED);
    }

    g2d.setColor(Color.BLACK);
    xPos = width/10;
    yPos = height/10;
    s = "Fitness = " + String.format("%.2f", fitness);
    g2d.drawString(s, xPos, yPos);

    g2d.dispose();


    RenderedImage rendImage = bufferedImage;
    File file = new File("Genome" + generation + ".png");
    try {
      ImageIO.write(rendImage, "png", file);
    }
    catch(Exception e) {
      System.out.println("Coulnd't write to file.");
    }

  }

}
