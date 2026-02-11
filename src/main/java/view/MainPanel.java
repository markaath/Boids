package view;

import app.App;
import java.awt.Graphics;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import model.Boid;
import model.GameObject;
import util.SimpleQuadTree;
import util.SimpleQuadTree.Rectangle;

public class MainPanel extends JPanel {

  public record BoidParameters(
    double MAX_SPEED,
    double MAX_FORCE,
    double DETECTION_RADIUS,
    double AVOID_RADIUS,
    double sepWeight,
    double aliWeight,
    double centerWeight,
    double avoidWeight,
    double mutateWeight
  ) {
    private static final Random random = new Random();

    public BoidParameters mutate() {
      int nbOfMutation = random.nextInt(3) + 1;

      int[] index = new int[9];
      for (int i = 0; i < 9; i++) {
        index[i] = i;
      }

      for (int i = 0; i < 9; i++) {
        int j = random.nextInt(9);
        int temp = index[i];
        index[i] = index[j];
        index[j] = index[i];
      }

      double[] newParameters = new double[9];
      System.arraycopy(
        new double[] {
          MAX_SPEED,
          MAX_FORCE,
          DETECTION_RADIUS,
          AVOID_RADIUS,
          sepWeight,
          aliWeight,
          centerWeight,
          avoidWeight,
          mutateWeight,
        }, 0, newParameters, 0, 9
      );

      for(int i = 0; i < nbOfMutation; i++){
        int indice = index[i];
        double delta = (Math.random() - 0.5)*2 * newParameters[indice];
        newParameters[indice] += delta; 
      }

      return new BoidParameters(newParameters[0], newParameters[1], newParameters[2], newParameters[3], newParameters[4], newParameters[5], newParameters[6], newParameters[7], newParameters[8]);
    }

    public double MAX_SPEED() {
      return MAX_SPEED;
    }

    public double MAX_FORCE() {
      return MAX_FORCE;
    }

    public double DETECTION_RADIUS() {
      return DETECTION_RADIUS;
    }

    public double AVOID_RADIUS() {
      return AVOID_RADIUS;
    }

    public double sepWeight() {
      return sepWeight;
    }

    public double aliWeight() {
      return aliWeight;
    }

    public double centerWeight() {
      return centerWeight;
    }

    public double avoidWeight() {
      return avoidWeight;
    }

    public double mutateWeight() {
      return mutateWeight;
    }

    
};

  private SimpleQuadTree quadTree;
  private List<GameObject> entities;

  public MainPanel(GameObject... initialEntities) {
    int screenWidth = App.screenWidth;
    int screenHeight = App.screenHeight;

    // Créer le QuadTree avec les dimensions de l'écran
    Rectangle bounds = new Rectangle(0, 0, screenWidth, screenHeight);
    this.quadTree = new SimpleQuadTree(0, bounds);

    // Insérer les entités initiales
    for (GameObject entity : initialEntities) {
      quadTree.insert(entity);
    }

    // Timer pour rafraîchir l'affichage (60 FPS = ~16ms)
    new Timer(16, e -> {
      updateEntities();
      repaint();
    })
      .start();
  }

  private void updateEntities() {
    // Récupérer tous les objets de l'arbre
    entities = quadTree.getAllObjects();

    // Mettre à jour chaque objet en lui passant le quadTree
    for (GameObject obj : entities) {
      if (obj instanceof Boid) {
        ((Boid) obj).update(quadTree);
      } else {
        obj.update();
      }
    }

    // Reconstruire le QuadTree avec les nouvelles positions
    quadTree.clear();
    for (GameObject obj : entities) {
      quadTree.insert(obj);
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (entities != null) {
      for (GameObject obj : entities) {
        obj.draw(g);
      }
    }
  }

  public SimpleQuadTree getQuadTree() {
    return quadTree;
  }
}
