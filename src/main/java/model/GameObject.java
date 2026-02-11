package model;

import java.awt.Graphics;
import util.Vec2;

public abstract class GameObject {

  private Vec2 position;

  public GameObject(double x, double y) {
    this.position = new Vec2(x, y);
  }

  public Vec2 getPosition() {
    return this.position;
  }

  public void setPosition(double x, double y) {
    position.x = x;
    position.y = y;
  }

  public abstract void update();

  public abstract void draw(Graphics g);
}
