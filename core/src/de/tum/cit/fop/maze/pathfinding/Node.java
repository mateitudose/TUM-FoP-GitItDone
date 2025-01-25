package de.tum.cit.fop.maze.pathfinding;

public class Node {
    private int x, y;
    // fCost = gCost + hCost, which means it would be the total path
    // gCost is the cost from the enemy to a current tile
    // hCost is the cost from the current tile to the target, which in our case is the Player
    private float fCost, gCost, hCost;
    private Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void calculate_fCost() {
        fCost = gCost + hCost;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getfCost() {
        return fCost;
    }

    public void setfCost(float fCost) {
        this.fCost = fCost;
    }

    public float getgCost() {
        return gCost;
    }

    public void setgCost(float gCost) {
        this.gCost = gCost;
    }

    public float gethCost() {
        return hCost;
    }

    public void sethCost(float hCost) {
        this.hCost = hCost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

}

