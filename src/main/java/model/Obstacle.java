package model;

import java.awt.Color;
import java.awt.Graphics;
import util.Vec2;

public class Obstacle extends GameObject {

  private final int size;

  public Obstacle(int size, double x, double y) {
    super(x, y);
    this.size = size;
  }

  public Obstacle(double x, double y) {
    super(x, y);
    this.size = 20;
  }

  @Override
  public void update() {}

  @Override
  public void draw(Graphics g) {
    g.setColor(Color.BLACK);
    Vec2 position = super.getPosition();
    g.fillOval((int) position.x - size/2, (int) position.y - size/2, size, size);
  }

  public int getSize(){
     return this.size;
  }
}
