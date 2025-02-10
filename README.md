# A Whiskered Thief - FoP WiSe 2024/25, Team GitItDone
<img width="1322" alt="Screenshot 2025-02-10 at 19 24 38" src="https://github.com/user-attachments/assets/db26108b-bd2d-4fde-8295-d866e0c86d81" />

## Project Structure

The project is organized into several packages, each containing classes that serve specific purposes within the game.
Below is an overview of the main packages and classes:

### Packages and Classes

- `de.tum.cit.fop.maze`
    - `MazeMap`: Handles the maze generation and map-related functionalities.
    - `MazeRunnerGame`: Main class to initialize and start the game.
    - `HUD`: Displays the player's lives, collected fish count, and closest exit using an arrow.

- `de.tum.cit.fop.maze.objects`
    - `GameObject`: Base class for all game objects.
    - `Ability`: Represents collectible abilities within the maze.
    - `Wall`: Represents walls that block the player and enemies using Box2D collision boxes.
    - `EntryPoint`: Represents the entry point of the maze.
    - `ExitPoint`: Represents the exit point of the maze.
    - `Fish`: Represents the fish that the player needs to collect to unlock exits and win the game.
    - `Heart`: Represents the heart that the player can collect to gain extra lives.
    - `LaserTrap`: Represents the laser trap that the player needs to avoid, as it can damage the player.
    - `Path`: Represents the path that the player and enemies can move on.
    - `SlowTile`: Represents the slow tile that slows down only the player.

- `de.tum.cit.fop.maze.entities`
    - `Player`: Represents the player character.
    - `GameEntity`: Base class for all game entities.
    - `Enemy`: Represents enemy characters with pathfinding and random movement capabilities.

- `de.tum.cit.fop.maze.screens`
    - `GameScreen`: Represents the main game screen where the game is played.
    - `MenuScreen`: Represents the main menu screen where the player can start the game or exit.
    - `GameOverScreen`: Represents the game over screen that is displayed when the player loses all lives.
    - `VictoryScreen`: Represents the victory screen that is displayed when the player wins the game.
    - `PauseMenuScreen`: Represents the pause menu screen that is displayed when the player pauses the game.
    - `MapSelectionScreen`: Represents the map selection screen where the player can choose the maze to play.

- `de.tum.cit.fop.maze.pathfinding`
    - `Algorithm`: The Algorithm class implements the A* pathfinding algorithm and all related functionalities.
    - `Node`: Represents a node in the A* pathfinding algorithm.

## UML Diagrams

Each package contains in its folder a UML diagram in PNG format that shows the classes and their relationships within the package.

Additionally, the UML diagrams contain the methods and fields of each class.

## Game Mechanics

### Player

- The player can move around the maze using the arrow keys.
- The player can collect abilities, fish, and hearts.
- The player can unlock the exits by collecting at least 1 fish.

### Enemies

- Enemies patrol the maze and can follow the player if they come too close.
- Enemies have a pathfinding algorithm to navigate the maze and can also move randomly.

### Abilities

- Abilities are collectible items that provide the special "OIIAOIIA" super move that makes all dogs dizzy for a certain
  duration, during which they stop moving and turn gray.
- Also, the player can use the "OIIAOIIA" super move to kickback enemies that are too close.

### Additional Features

- **Dizziness**: Enemies can become dizzy for a certain duration, during which they stop moving and turn gray.
- **Pathfinding**: Enemies use the A* algorithm to chase the player when in proximity.

## Instructions to run the game

1. Clone the repository to your local machine.
2. Open the project in IntelliJ IDEA.
3. Run the `Run Game` pre-configured task.

### Possible issues when running the game

- If the game does not run, make sure that you also check these points:
    - The project is imported correctly.
    - You may have to adapt the classpath setting in the run configuration: choose the one ending in '.desktop.main'.
    - Make sure that Gradle JVM is set to use the same JDK version as the project.
    - If running on Mac, make sure to add the following VM option to the run configuration: `-XstartOnFirstThread`. On Windows/Linux, this option must not be set.
