package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Boid;
import model.GameObject;
import model.Obstacle;
import view.MainPanel;
import view.MainPanel.BoidParameters;

public class App extends JFrame {

  public static Toolkit tk = Toolkit.getDefaultToolkit();
  public static int screenHeight = (int) tk.getScreenSize().getHeight();
  public static int screenWidth = (int) tk.getScreenSize().getWidth();

  public App() {
    setName("Boids");
    setSize(screenWidth, screenHeight);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    BoidParameters p = new BoidParameters(5.0, 0.2, 50.0, 100.0, 1.1, 0.75, 0.5, 2.5, 0.5);

    List<GameObject> boids = new ArrayList<>();
    for (int i = 0; i < 200; i++) {
      boids.add(
          new Boid(
              (int) (Math.random() * screenWidth),
              (int) (Math.random() * screenHeight),
              p));
    }
    boids.add(new Obstacle(50, screenWidth / 2, screenHeight / 2));
    GameObject[] list = new GameObject[boids.size()];
    for (int i = 0; i < boids.size(); i++) {
      list[i] = boids.get(i);
    }
    MainPanel pane = new MainPanel(list);
    pane.setPreferredSize(new Dimension(screenWidth, screenHeight));
    add(pane, BorderLayout.CENTER);
    setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(App::new);
  }
}
