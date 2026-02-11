package util;

public class Vec2 {

  public double x;
  public double y;

  public Vec2(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Vec2 normalized() {
    double len = Math.hypot(x, y);
    return new Vec2(x / len, y / len);
  }

  public Vec2 rotate(double angle) {
    //angle en radian
    double newX = this.x * Math.cos(angle) - this.y * Math.sin(angle);
    double newY = this.y * Math.cos(angle) + this.x * Math.sin(angle);

    return new Vec2(newX, newY);
  }

  public Vec2 add(Vec2 other) {
    return new Vec2(this.x + other.x, this.y + other.y);
  }

  public Vec2 subtract(Vec2 other) {
    return new Vec2(this.x - other.x, this.y - other.y);
  }

  public Vec2 multiply(double scalar) {
    return new Vec2(this.x * scalar, this.y * scalar);
  }

  public Vec2 divide(double scalar) {
    if (scalar == 0) return new Vec2(0, 0); // Sécurité
    return new Vec2(this.x / scalar, this.y / scalar);
  }

  public double magnitude() {
    return Math.hypot(x, y);
  }

  public double magnitudeSquared() {
    return x * x + y * y;
  }

  public double distance(Vec2 other) {
    double dx = this.x - other.x;
    double dy = this.y - other.y;
    return Math.hypot(dx, dy);
  }

  public double distanceSquared(Vec2 other) {
    double dx = this.x - other.x;
    double dy = this.y - other.y;
    return dx * dx + dy * dy;
  }

  public Vec2 limit(double max) {
    double mag = magnitude();
    if (mag > max) {
      return this.normalized().multiply(max);
    }
    return new Vec2(this.x, this.y); // copie
  }

  public Vec2 setMagnitude(double newMag) {
    return this.normalized().multiply(newMag);
  }

  public double dot(Vec2 other) {
    // Produit scalaire
    return this.x * other.x + this.y * other.y;
  }

  public double cross(Vec2 other) {
    // Produit vectoriel 
    return this.x * other.y - this.y * other.x;
  }

  public double heading() {
    // Angle en radians
    return Math.atan2(y, x);
  }

  public Vec2 copy() {
    return new Vec2(this.x, this.y);
  }

  public static Vec2 fromAngle(double angle) {
    // Créer un vecteur unitaire à partir d'un angle
    return new Vec2(Math.cos(angle), Math.sin(angle));
  }

  public static Vec2 random() {
    // Vecteur aléatoire normalisé
    double angle = Math.random() * 2 * Math.PI;
    return fromAngle(angle);
  }

  public static Vec2 lerp(Vec2 start, Vec2 end, double t) {
    return new Vec2(
      start.x + (end.x - start.x) * t,
      start.y + (end.y - start.y) * t
    );
  }

  public double angleWith(Vec2 other){
    double dot = this.dot(other);
    double magnitudeSum = this.magnitude() + other.magnitude();
    double result = Math.acos(dot/magnitudeSum);
    return result;
  }
}
