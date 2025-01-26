package de.tum.cit.fop.maze.pathfinding;

/**
 * Represents a node in the pathfinding grid.
 * Each node has coordinates, costs for pathfinding, and a parent node.
 */
public class Node {
    private int x, y;
    // fCost = gCost + hCost, which means it would be the total path
    // gCost is the cost from the enemy to a current tile
    // hCost is the cost from the current tile to the target, which in our case is the Player
    private float fCost, gCost, hCost;
    private Node parent;

    /**
     * Constructs a new Node object.
     *
     * @param x the x-coordinate of the node
     * @param y the y-coordinate of the node
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates the f-cost of the node.
     * fCost is the sum of gCost and hCost.
     */
    public void calculate_fCost() {
        fCost = gCost + hCost;
    }

    /**
     * Gets the x-coordinate of the node.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the node.
     *
     * @param x the new x-coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the node.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the node.
     *
     * @param y the new y-coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the f-cost of the node.
     *
     * @return the f-cost
     */
    public float getfCost() {
        return fCost;
    }

    /**
     * Sets the f-cost of the node.
     *
     * @param fCost the new f-cost
     */
    public void setfCost(float fCost) {
        this.fCost = fCost;
    }

    /**
     * Gets the g-cost of the node.
     *
     * @return the g-cost
     */
    public float getgCost() {
        return gCost;
    }

    /**
     * Sets the g-cost of the node.
     *
     * @param gCost the new g-cost
     */
    public void setgCost(float gCost) {
        this.gCost = gCost;
    }

    /**
     * Gets the h-cost of the node.
     *
     * @return the h-cost
     */
    public float gethCost() {
        return hCost;
    }

    /**
     * Sets the h-cost of the node.
     *
     * @param hCost the new h-cost
     */
    public void sethCost(float hCost) {
        this.hCost = hCost;
    }

    /**
     * Gets the parent node.
     *
     * @return the parent node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent node.
     *
     * @param parent the new parent node
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Checks if this node is equal to another object.
     * Two nodes are equal if they have the same x and y coordinates.
     *
     * @param obj the object to compare with
     * @return true if the nodes are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }
}
