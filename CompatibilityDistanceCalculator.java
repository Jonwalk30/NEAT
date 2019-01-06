import java.util.*;
import java.lang.*;

// TODO: Resolve the fact that there is a lot of repeated code in here
public interface CompatibilityDistanceCalculator {

  default public Integer disjointGeneCount(Genome g1, Genome g2) {

    int counter = 0;

    // For nodes
    // Figure out the iV of them both
    int g1HighestInnovation = 0;
    int g2HighestInnovation = 0;
    for (NodeGene node : g1.getNodeGenes().values()) {
      if (node.getInnovationNumber() > g1HighestInnovation) {
        g1HighestInnovation = node.getInnovationNumber();
      }
    }
    for (NodeGene node : g2.getNodeGenes().values()) {
      if (node.getInnovationNumber() > g2HighestInnovation) {
        g2HighestInnovation = node.getInnovationNumber();
      }
    }

    // Take the largest
    int loopUpTo = g1HighestInnovation > g2HighestInnovation ? g1HighestInnovation : g2HighestInnovation;
    // Count to the highest one of the two
    for (int i = 0; i <= loopUpTo; i++) {
      // Check if disjoint
      if (g1.getNodeGenes().get(i) != null && g2.getNodeGenes().get(i) == null) {
        if (i < g2HighestInnovation) {
          counter++;
        }
      } else if (g1.getNodeGenes().get(i) == null && g2.getNodeGenes().get(i) != null) {
        if (i < g1HighestInnovation) {
          counter++;
        }
      }
    }

    // For connections
    // Figure out the iV of them both
    g1HighestInnovation = 0;
    g2HighestInnovation = 0;
    for (ConnectionGene con : g1.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g1HighestInnovation) {
        g1HighestInnovation = con.getInnovationNumber();
      }
    }
    for (ConnectionGene con : g2.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g2HighestInnovation) {
        g2HighestInnovation = con.getInnovationNumber();
      }
    }
    // Take the largest
    loopUpTo = g1HighestInnovation > g2HighestInnovation ? g1HighestInnovation : g2HighestInnovation;
    // Count to the highest one of the two
    for (int i = 0; i <= loopUpTo; i++) {
      // Check if disjoint
      if (g1.getConnectionGenes().get(i) != null && g2.getConnectionGenes().get(i) == null) {
        if (i < g2HighestInnovation) {
          counter++;
        }
      } else if (g1.getConnectionGenes().get(i) == null && g2.getConnectionGenes().get(i) != null) {
        if (i < g1HighestInnovation) {
          counter++;
        }
      }
    }

    return counter;
  }

  default public Integer excessGeneCount(Genome g1, Genome g2) {

    int counter = 0;

    // For nodes
    // Figure out the iV of them both
    int g1HighestInnovation = 0;
    int g2HighestInnovation = 0;
    for (NodeGene node : g1.getNodeGenes().values()) {
      if (node.getInnovationNumber() > g1HighestInnovation) {
        g1HighestInnovation = node.getInnovationNumber();
      }
    }
    for (NodeGene node : g2.getNodeGenes().values()) {
      if (node.getInnovationNumber() > g2HighestInnovation) {
        g2HighestInnovation = node.getInnovationNumber();
      }
    }
    // Take the largest
    int loopUpTo = g1HighestInnovation > g2HighestInnovation ? g1HighestInnovation : g2HighestInnovation;
    // Count to the highest one of the two
    for (int i = 0; i <= loopUpTo; i++) {
      // Check if excess
      if (g1.getNodeGenes().get(i) != null && g2.getNodeGenes().get(i) == null) {
        if (i > g2HighestInnovation) {
          counter++;
        }
      } else if (g1.getNodeGenes().get(i) == null && g2.getNodeGenes().get(i) != null) {
        if (i > g1HighestInnovation) {
          counter++;
        }
      }
    }

    // For connections
    // Figure out the iV of them both
    g1HighestInnovation = 0;
    g2HighestInnovation = 0;
    for (ConnectionGene con : g1.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g1HighestInnovation) {
        g1HighestInnovation = con.getInnovationNumber();
      }
    }
    for (ConnectionGene con : g2.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g2HighestInnovation) {
        g2HighestInnovation = con.getInnovationNumber();
      }
    }
    // Take the largest
    loopUpTo = g1HighestInnovation > g2HighestInnovation ? g1HighestInnovation : g2HighestInnovation;
    // Count to the highest one of the two
    for (int i = 0; i <= loopUpTo; i++) {
      // Check if excess
      if (g1.getConnectionGenes().get(i) != null && g2.getConnectionGenes().get(i) == null) {
        if (i > g2HighestInnovation) {
          counter++;
        }
      } else if (g1.getConnectionGenes().get(i) == null && g2.getConnectionGenes().get(i) != null) {
        if (i > g1HighestInnovation) {
          counter++;
        }
      }
    }

    return counter;
  }

  default public Integer matchingGeneCount(Genome g1, Genome g2) {

    int counter = 0;

    // For connections
    // Figure out the iV of them both
    int g1HighestInnovation = 0;
    int g2HighestInnovation = 0;
    for (ConnectionGene con : g1.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g1HighestInnovation) {
        g1HighestInnovation = con.getInnovationNumber();
      }
    }
    for (ConnectionGene con : g2.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g2HighestInnovation) {
        g2HighestInnovation = con.getInnovationNumber();
      }
    }
    // Take the smallest
    int loopUpTo = g1HighestInnovation < g2HighestInnovation ? g1HighestInnovation : g2HighestInnovation;
    // Count to the lowest one of the two
    for (int i = 0; i <= loopUpTo; i++) {
      // Check if matching
      if (g1.getConnectionGenes().get(i) != null && g2.getConnectionGenes().get(i) != null) {
        counter++;
      }
    }

    return counter;
  }

  default public float averageWeightDifference(Genome g1, Genome g2) {

    float weightDifference = 0;

    // For connections
    // Figure out the iV of them both
    int g1HighestInnovation = 0;
    int g2HighestInnovation = 0;
    for (ConnectionGene con : g1.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g1HighestInnovation) {
        g1HighestInnovation = con.getInnovationNumber();
      }
    }
    for (ConnectionGene con : g2.getConnectionGenes().values()) {
      if (con.getInnovationNumber() > g2HighestInnovation) {
        g2HighestInnovation = con.getInnovationNumber();
      }
    }
    // Take the smallest
    int loopUpTo = g1HighestInnovation < g2HighestInnovation ? g1HighestInnovation : g2HighestInnovation;
    // Count to the lowest one of the two
    for (int i = 0; i <= loopUpTo; i++) {
      // Check if matching
      if (g1.getConnectionGenes().get(i) != null && g2.getConnectionGenes().get(i) != null) {
        weightDifference = weightDifference + Math.abs(g1.getConnectionGenes().get(i).getWeight() - g2.getConnectionGenes().get(i).getWeight());
      }
    }

    return weightDifference / matchingGeneCount(g1, g2);
  }

  default public Integer genomeSize(Genome g) {
    return 1;
  }

}
