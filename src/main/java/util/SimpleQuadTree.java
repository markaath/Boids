package util;

import java.util.ArrayList;
import java.util.List;
import model.GameObject;

public class SimpleQuadTree {
    private static final int MAX_OBJECTS = 10;
    private static final int MAX_LEVELS = 5;
    
    private int level;
    private List<GameObject> objects;
    private Rectangle bounds;
    private SimpleQuadTree[] nodes;
    
    public static class Rectangle {
        public double x, y, width, height;
        
        public Rectangle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public boolean contains(double px, double py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }
        
        public boolean intersects(Rectangle other) {
            return !(other.x > x + width || 
                     other.x + other.width < x || 
                     other.y > y + height || 
                     other.y + other.height < y);
        }
    }
    
    public SimpleQuadTree(int level, Rectangle bounds) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new SimpleQuadTree[4];
    }
    
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }
    
    private void split() {
        double subWidth = bounds.width / 2;
        double subHeight = bounds.height / 2;
        double x = bounds.x;
        double y = bounds.y;
        
        nodes[0] = new SimpleQuadTree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[1] = new SimpleQuadTree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[2] = new SimpleQuadTree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new SimpleQuadTree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }
    
    private int getIndex(GameObject obj) {
        Vec2 pos = obj.getPosition();
        double verticalMidpoint = bounds.x + bounds.width / 2;
        double horizontalMidpoint = bounds.y + bounds.height / 2;
        
        boolean topQuadrant = pos.y < horizontalMidpoint;
        boolean bottomQuadrant = pos.y >= horizontalMidpoint;
        
        if (pos.x < verticalMidpoint) {
            if (topQuadrant) return 0;
            if (bottomQuadrant) return 2;
        } else if (pos.x >= verticalMidpoint) {
            if (topQuadrant) return 1;
            if (bottomQuadrant) return 3;
        }
        
        return -1;
    }
    
    public void insert(GameObject obj) {
        if (nodes[0] != null) {
            int index = getIndex(obj);
            if (index != -1) {
                nodes[index].insert(obj);
                return;
            }
        }
        
        objects.add(obj);
        
        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }
            
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }
    
    public List<GameObject> retrieve(List<GameObject> returnObjects, GameObject obj) {
        int index = getIndex(obj);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, obj);
        }
        
        returnObjects.addAll(objects);
        return returnObjects;
    }
    
    public List<GameObject> query(Rectangle area, List<GameObject> result) {
        if (result == null) result = new ArrayList<>();
        
        if (!bounds.intersects(area)) {
            return result;
        }
        
        for (GameObject obj : objects) {
            Vec2 pos = obj.getPosition();
            if (area.contains(pos.x, pos.y)) {
                result.add(obj);
            }
        }
        
        if (nodes[0] != null) {
            for (SimpleQuadTree node : nodes) {
                node.query(area, result);
            }
        }
        
        return result;
    }
    
    public List<GameObject> getInRadius(double x, double y, double radius) {
        Rectangle searchArea = new Rectangle(
            x - radius, y - radius, 
            radius * 2, radius * 2
        );
        
        List<GameObject> candidates = query(searchArea, new ArrayList<>());
        List<GameObject> result = new ArrayList<>();
        
        for (GameObject obj : candidates) {
            Vec2 pos = obj.getPosition();
            double dx = pos.x - x;
            double dy = pos.y - y;
            if (dx * dx + dy * dy <= radius * radius) {
                result.add(obj);
            }
        }
        
        return result;
    }
    
    public List<GameObject> getAllObjects() {
        List<GameObject> result = new ArrayList<>(objects);
        if (nodes[0] != null) {
            for (SimpleQuadTree node : nodes) {
                result.addAll(node.getAllObjects());
            }
        }
        return result;
    }
}