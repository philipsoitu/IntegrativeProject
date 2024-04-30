package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Constants;
import com.coolawesome.integrativeproject.utils.JsonPlanetManager;
import com.coolawesome.integrativeproject.utils.Vector3D;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Class that represents the simulation of the planets.
 */
public class Simulation {

    static int collisionCount = 0;
    Map<String, Planet> planetMap = new HashMap<>();
    TreeNode root;
    SimulationView simulationView;
    boolean isPaused = false;

    /**
     * Constructor for the Simulation class.
     *
     * @param viewport   The viewport for the simulation.
     * @param controller The controller for the simulation.
     */
    public Simulation(AnchorPane viewport, MainController controller) {
        simulationView = new SimulationView(viewport, this, controller);
        initialize(100);
    }

    /**
     * Initializes the simulation with a given number of bodies.
     *
     * @param numOfBodies The number of bodies to initialize the simulation with.
     */
    public void initialize(int numOfBodies) {
        for (int i = 0; i < numOfBodies; i++) {
            String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");
            Planet randPlanet = createRandomPlanet(uniqueID);

            // Add the Planet to the Map
            planetMap.put(uniqueID, randPlanet);
        }
        String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");
        planetMap.put(uniqueID, new Planet("sun", new Vector3D(5000, 0, 0), new Vector3D(), 1000, 100000, true));
    }

    /**
     * Creates a random planet with a given unique ID.
     *
     * @param uniqueID The unique ID for the planet.
     * @return The randomly generated planet.
     */
    public Planet createRandomPlanet(String uniqueID) {
        Vector3D randPos = Vector3D.generateRandomVector();
        Vector3D randVel = new Vector3D();

//       boolean sun = Math.random() * 4 < 1;
        boolean sun = false;

        return new Planet(uniqueID, randPos, randVel, getRandomRadius(), Constants.defaultMass, sun);
    }

    /**
     * Creates a random planet with a given unique ID and position.
     *
     * @param uniqueID The unique ID for the planet.
     * @param pos      The position of the planet.
     * @return The randomly generated planet.
     */
    public Planet createRandomPlanet(String uniqueID, Vector3D pos) {
        boolean sun = Math.random() * 4 < 1;
        return new Planet(uniqueID, pos, new Vector3D(), getRandomRadius(), Constants.defaultMass, sun);
    }

    /**
     * Gets a random radius for a planet.
     *
     * @return The random radius for the planet.
     */
    private double getRandomRadius() {
        return 1 + Math.random() * 2;
    }

    /**
     * Updates the simulation with a given time step.
     *
     * @param dt The time step for the update.
     */
    public void update(double dt) {
        // handle physics and collisions
        if (!isPaused) {
            planetMap.forEach((id, p) -> p.update(dt));
            collision();
            constructTree();
            gravity();

        }

        simulationView.update();
    }

    /**
     * Constructs the octree for the simulation.
     */
    private void constructTree() {
        double[] boundingSquare = getBoundingSquare();
        root = new TreeNode(boundingSquare[0], boundingSquare[1], boundingSquare[2], boundingSquare[3]);

        planetMap.forEach((id, p) -> root.insert(p));
    }

    /**
     * Gets the bounding square for the simulation.
     *
     * @return The bounding square for the simulation.
     */
    public double[] getBoundingSquare() {
        // Indices
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (Map.Entry<String, Planet> entry : planetMap.entrySet()) {
            Planet p = entry.getValue();
            minX = Math.min(minX, p.position.x);
            maxX = Math.max(maxX, p.position.x);
            minY = Math.min(minY, p.position.y);
            maxY = Math.max(maxY, p.position.y);
            minZ = Math.min(minZ, p.position.z);
            maxZ = Math.max(maxZ, p.position.z);

        }


        return new double[]{minX, minY, minZ, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ))};
    }

    /**
     * Applies gravity to the planets in the simulation.
     */
    private void gravity() {
        planetMap.forEach((id, p) -> gravitate(p, root));
    }

    /**
     * Applies gravity to a given planet.
     *
     * @param p  The planet to apply gravity to.
     * @param tn The tree node to apply gravity from.
     */
    private void gravitate(Planet p, TreeNode tn) {
        if (tn.leaf) {
            if (tn.planet == null || p == tn.planet) return;
            p.velocity.add(gravityAcc(tn.planet.position, p.position, tn.planet.mass, p.mass));
        } else {

            if (tn.centerOfMass == null) {
                tn.centerOfMass = tn.centerOfMassTimesTotalMass.scalarProduct(1.0 / tn.totalMass);
            }
            if (tn.w / Vector3D.distance(p.position, tn.centerOfMass) < MainController.theta) {
                p.velocity.add(gravityAcc(tn.centerOfMass, p.position, tn.totalMass, p.mass));
            } else {

                for (TreeNode child : tn.children) gravitate(p, child);
            }
        }
    }

    /**
     * Calculates the acceleration due to gravity between two planets.
     *
     * @param posA  The position of the first planet.
     * @param posB  The position of the second planet.
     * @param massA The mass of the first planet.
     * @param massB The mass of the second planet.
     * @return The acceleration due to gravity between the two planets.
     */
    private Vector3D gravityAcc(Vector3D posA, Vector3D posB, double massA, double massB) {

        Vector3D distance = Vector3D.difference(posA, posB);
        double dist = distance.magnitude();

        Vector3D direction = Vector3D.unitVector(distance);
        double force = (MainController.g * massA * massB) / (dist * dist);

        return direction.scalarProduct(force / massB);
    }

    /**
     * Handles collisions between planets in the simulation.
     */
    public void collision() {
        planetMap.forEach((id1, p1) -> {
            planetMap.forEach((id2, p2) -> {
                if (!Objects.equals(p1, p2)) {

                    // collision detection
                    Vector3D collisionAxis = Vector3D.difference(p1.position, p2.position);
                    double dist = collisionAxis.magnitude();
                    double minDist = p1.radius + p2.radius;

                    // collision handle
                    if (dist < minDist) {

                        collisionCount++;

                        Vector3D normal = Vector3D.multiplication(1 / dist, collisionAxis);
                        double delta = minDist - dist;

                        // Calculate masses
                        double totalMass = p1.mass + p2.mass;
                        double p1Ratio = p1.mass / totalMass;
                        double p2Ratio = p2.mass / totalMass;

                        // Calculate displacement based on mass ratio
                        Vector3D p1Displacement = Vector3D.multiplication(delta * p2Ratio, normal);
                        Vector3D p2Displacement = Vector3D.multiplication(delta * p1Ratio, normal);

                        p1.position.add(p1Displacement);
                        p1.velocity.add(p1Displacement);
                        p2.position.subtract(p2Displacement);
                        p2.velocity.subtract(p2Displacement);
                    }
                }
            });
        });
    }


    /**
     * Saves the simulation to a JSON file.
     *
     * @param filePath The file path to save the simulation to.
     */
    public void saveToJson(String filePath) {
        JsonPlanetManager.saveToJson(planetMap, filePath);
    }

    /**
     * Loads the simulation from a JSON file.
     *
     * @param filePath The file path to load the simulation from.
     */
    public void loadFromJson(String filePath) {
        planetMap = JsonPlanetManager.loadFromJson(filePath);
    }
}
