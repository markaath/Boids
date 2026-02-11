package model;

import app.App;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;
import util.SimpleQuadTree;
import util.Vec2;
import view.MainPanel.BoidParameters;

public class Boid extends GameObject {

  private static final int SIZE = 10;
  private Vec2 speed = new Vec2(Math.random() * 5, Math.random() * 5);
  private Vec2 acceleration = new Vec2(0, 0);
  private final BoidParameters parameters;

  public Boid(int x, int y, Vec2 speed, BoidParameters p) {
    super(x, y);
    this.speed = speed;
    this.parameters = p;
  }

  public Boid(int x, int y, BoidParameters p) {
    super(x, y);
    this.parameters = p;
  }

  @Override
  public void draw(Graphics g) {
    Vec2 normalized_speed = speed.normalized();
    double forwardX = this.getPosition().x + (normalized_speed.x * SIZE);
    double forwardY = this.getPosition().y + (normalized_speed.y * SIZE);

    Vec2 r1 = normalized_speed.rotate((5 * Math.PI) / 6);

    double leftX = this.getPosition().x + (r1.x * SIZE);
    double leftY = this.getPosition().y + (r1.y * SIZE);

    Vec2 r2 = normalized_speed.rotate((-5 * Math.PI) / 6);

    double rightX = this.getPosition().x + (r2.x * SIZE);
    double rightY = this.getPosition().y + (r2.y * SIZE);
    g.setColor(Color.BLUE);
    g.fillPolygon(
      new Polygon(
        new int[] { (int) forwardX, (int) leftX, (int) rightX },
        new int[] { (int) forwardY, (int) leftY, (int) rightY },
        3
      )
    );
  }

  @Override
  public void update() {
    Vec2 direction = speed.normalized();
    this.setPosition(
      this.getPosition().x + (direction.x * speed.x),
      this.getPosition().y + (direction.y * speed.y)
    );

    // Wrap-around horizontal
    if (this.getPosition().x < 0) {
      this.setPosition(App.screenWidth, this.getPosition().y);
    } else if (this.getPosition().x > App.screenWidth) {
      this.setPosition(0, this.getPosition().y);
    }

    // Wrap-around vertical
    if (this.getPosition().y < 0) {
      this.setPosition(this.getPosition().x, App.screenHeight);
    } else if (this.getPosition().y > App.screenHeight) {
      this.setPosition(this.getPosition().x, 0);
    }
  }

  public void update(SimpleQuadTree qTree) {
    acceleration = new Vec2(0, 0);

    List<GameObject> neighbours = qTree.getInRadius(
      getPosition().x,
      getPosition().y,
      parameters.DETECTION_RADIUS()
    );

    neighbours.remove(this);
    if (!neighbours.isEmpty()) {
      //Calcule de la force de séparation
      Vec2 sepForce = separate(neighbours);
      Vec2 aliForce = align(neighbours);
      Vec2 centerForce = center(neighbours);
      Vec2 avoidForce = avoid(neighbours);

      sepForce = sepForce.multiply(parameters.sepWeight());
      aliForce = aliForce.multiply(parameters.aliWeight());
      centerForce = centerForce.multiply(parameters.centerWeight());
      avoidForce = avoidForce.multiply(parameters.avoidWeight());

      acceleration = acceleration.add(sepForce);
      acceleration = acceleration.add(aliForce);
      acceleration = acceleration.add(centerForce);
      acceleration = acceleration.add(avoidForce);
    }
    speed = speed.add(acceleration);
    speed = speed.limit(parameters.MAX_SPEED());
    this.setPosition(
      this.getPosition().x + (speed.x),
      this.getPosition().y + (speed.y)
    );

    // Wrap-around horizontal
    if (this.getPosition().x < 0) {
      this.setPosition(App.screenWidth, this.getPosition().y);
    } else if (this.getPosition().x > App.screenWidth) {
      this.setPosition(0, this.getPosition().y);
    }

    // Wrap-around vertical
    if (this.getPosition().y < 0) {
      this.setPosition(this.getPosition().x, App.screenHeight);
    } else if (this.getPosition().y > App.screenHeight) {
      this.setPosition(this.getPosition().x, 0);
    }
  }

  public Vec2 separate(List<GameObject> neigbours) {
    //repousse le boid de ses voisins
    Vec2 steer = new Vec2(0, 0);
    int count = 0;

    for (GameObject go : neigbours) {
      if (!(go instanceof Boid)) continue;
      double dist = getPosition().distance(go.getPosition());

      if (dist > 0 && dist < 50) {
        Vec2 diff = getPosition().subtract(go.getPosition());
        diff = diff.normalized();
        diff = diff.divide(dist);
        steer = steer.add(diff);
        count++;
      }
    }

    if (count > 0) {
      steer = steer.divide(count);
    }

    if (steer.magnitude() > 0) {
      steer = steer.normalized();
      steer = steer.multiply(parameters.MAX_SPEED());
      steer = steer.subtract(speed);
      steer = steer.limit(parameters.MAX_FORCE());
    }

    return steer;
  }

  private Vec2 align(List<GameObject> neighbours) {
    //aligne la vitesse d'un boid sur celle de ces voisins
    Vec2 sum = new Vec2(0, 0);
    int count = 0;

    for (GameObject go : neighbours) {
      if (go instanceof Boid b) {
        sum = sum.add(b.getSpeed());
        count++;
      }
    }

    if (count > 0) {
      sum = sum.divide(count);
      sum = sum.setMagnitude(parameters.MAX_SPEED());
      Vec2 steer = sum.subtract(speed);
      steer.limit(parameters.MAX_FORCE());
      return steer;
    }

    return new Vec2(0, 0);
  }

  private Vec2 center(List<GameObject> neighbours) {
    Vec2 sum = new Vec2(0, 0);
    int count = 0;

    for (GameObject go : neighbours) {
      if (go instanceof Boid) {
        sum = sum.add(go.getPosition());
        count++;
      }
    }

    if (count > 0) {
      sum = sum.divide(count);
      return seek(sum);
    }
    return new Vec2(0, 0);
  }

  private Vec2 seek(Vec2 target) {
    Vec2 desired = target.subtract(getPosition());
    desired = desired.normalized();
    desired = desired.setMagnitude(parameters.MAX_SPEED());

    Vec2 steer = desired.subtract(speed);
    steer = steer.limit(parameters.MAX_FORCE());
    return steer;
  }

  private Vec2 avoid(List<GameObject> neighbours) {
    Vec2 steer = new Vec2(0, 0);
    int count = 0;

    for (GameObject go : neighbours) {
      if (go instanceof Obstacle o){ 

      double dist = getPosition().distance(o.getPosition());

      if (dist > 0 && dist < parameters.AVOID_RADIUS() + o.getSize()) {
        Vec2 diff = getPosition().subtract(o.getPosition());
        diff = diff.normalized();

        double strength = ((parameters.AVOID_RADIUS() - dist + o.getSize())) / parameters.AVOID_RADIUS();
        diff = diff.multiply(strength);

        steer = steer.add(diff);
        count++;}
      }
    }

    if (count > 0) {
      steer = steer.divide(count);
    }

    if (steer.magnitude() > 0) {
      steer = steer.setMagnitude(parameters.MAX_SPEED());
      steer = steer.subtract(speed);
      steer = steer.limit(10 * parameters.MAX_FORCE());
    }

    return steer;
  }

  public Vec2 getSpeed() {
    return speed;
  }
}
