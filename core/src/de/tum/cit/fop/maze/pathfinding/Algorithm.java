package de.tum.cit.fop.maze.pathfinding;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.MazeMap;

import java.util.*;

public class Algorithm {
    private final MazeMap mazeMap;
    private final Node[][] grid;

    public Algorithm(MazeMap mazeMap) {
        this.mazeMap = mazeMap;

        if (mazeMap.getMazeWidth() <= 0 || mazeMap.getMazeHeight() <= 0) {
            throw new IllegalStateException("Maze dimensions are invalid. Load the maze first!");
        }

        this.grid = new Node[mazeMap.getMazeWidth()][mazeMap.getMazeHeight()];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < mazeMap.getMazeWidth(); i++)
            for (int j = 0; j < mazeMap.getMazeHeight(); j++)
                grid[i][j] = new Node(i, j);
    }

    public List<Vector2> A_Star(Vector2 startWorldPos, Vector2 targetWorldPos) {
        Node startNode = worldToNode(startWorldPos);
        Node targetNode = worldToNode(targetWorldPos);
        List<Node> openSet = new ArrayList<>();
        Set<Node> closedSet = new HashSet<>();
        openSet.add(startNode);
        while (!openSet.isEmpty()) {
            Node current_node = getLowestFCostNode(openSet);
            openSet.remove(current_node);
            closedSet.add(current_node);
            // If we found the Player
            if (current_node.equals(targetNode)) {
                return wayBack(startNode, targetNode);
            }
            for (Node neighbor : getNeighbors(current_node)) {

                // If we have been through that tile already or if on that tile we find an obstacle we skip it
                if (closedSet.contains(neighbor) || mazeMap.isWall(neighbor.getX(), neighbor.getY()))
                    continue;
                // Calculate the distance Enemy -> current_node -> neighbor
                float newMovementCost = current_node.getgCost() + ManhattanDistance(current_node, neighbor);

                // If the path Enemy -> current_node -> neighbor is shorter then the path Enemy -> neighbor then we found a better way
                if (newMovementCost < neighbor.getgCost() || !openSet.contains(neighbor)) {

                    neighbor.setgCost(newMovementCost);
                    float new_hCost = ManhattanDistance(neighbor, targetNode);
                    neighbor.sethCost(new_hCost);
                    float new_fCost = neighbor.getgCost() + neighbor.gethCost();
                    neighbor.setfCost(new_fCost);
                    neighbor.setParent(current_node);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        // No path was found
        return new ArrayList<>();
    }

    private List<Vector2> wayBack(Node startNode, Node endNode) {

        List<Vector2> path = new ArrayList<>();
        Node current_node = endNode;

        while (current_node != startNode) {
            path.add(nodeToWorld(current_node));
            current_node = current_node.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private float ManhattanDistance(Node a, Node b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private Node getLowestFCostNode(List<Node> nodes) {
        Node lowest = nodes.get(0);
        for (Node node : nodes) {
            if (node.getfCost() < lowest.getfCost())
                lowest = node;
        }
        return lowest;
    }

    private Vector2 nodeToWorld(Node node) {
        return new Vector2(
                node.getX() + 0.5f,
                node.getY() + 0.5f
        );
    }

    private Node worldToNode(Vector2 worldPos) {
        int x = (int) Math.floor(worldPos.x);
        int y = (int) Math.floor(worldPos.y);

        x = Math.max(0, Math.min(x, mazeMap.getMazeWidth() - 1));
        y = Math.max(0, Math.min(y, mazeMap.getMazeHeight() - 1));

        return grid[x][y];
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // If we get a tile by diagonal movement we skip it / or our tile itself
                if (i == 0 && j == 0 || (i != 0 && j != 0))
                    continue;

                int checkX = node.getX() + i;
                int checkY = node.getY() + j;

                // If the tile is within the maze add it to neighbors
                if (checkX >= 0 && checkX < mazeMap.getMazeWidth() && checkY >= 0 && checkY < mazeMap.getMazeHeight()) {
                    neighbors.add(grid[checkX][checkY]);
                }
            }
        }
        return neighbors;
    }
}
