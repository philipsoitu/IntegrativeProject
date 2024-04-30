package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Vector3D;

/**
 * Represents a node in the Barnes-Hut tree used for the gravitational simulation.
 */
public class TreeNode {
    double x, y, z, w;
    TreeNode[] children; // Children
    boolean leaf;
    Planet planet;

    Vector3D centerOfMassTimesTotalMass; // http://hyperphysics.phy-astr.gsu.edu/hbase/cm.html
    double totalMass; // Total mass
    Vector3D centerOfMass; //calculated after
    int count; // Number of planets (for debugging)

    /**
     * Constructs a TreeNode object with specified properties.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param z The z-coordinate of the node.
     * @param w The w of the node.
     */
    public TreeNode(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.leaf = true;
        this.planet = null;
        this.children = new TreeNode[8];

        this.centerOfMassTimesTotalMass = new Vector3D(0, 0, 0);
        this.totalMass = 0;
        this.centerOfMass = null;
        this.count = 0;
    }

    /**
     * Splits the node into 8 children.
     */
    void split() {
        double newWidth = w * 0.5;
        children[0] = new TreeNode(x, y, z, newWidth); // front nw
        children[1] = new TreeNode(x + newWidth, y, z, newWidth); // front ne
        children[2] = new TreeNode(x, y + newWidth, z, newWidth); // front sw
        children[3] = new TreeNode(x + newWidth, y + newWidth, z, newWidth); // front se
        children[4] = new TreeNode(x, y, z + newWidth, newWidth); // back nw
        children[5] = new TreeNode(x + newWidth, y, z + newWidth, newWidth); // back ne
        children[6] = new TreeNode(x, y + newWidth, z + newWidth, newWidth); // back sw
        children[7] = new TreeNode(x + newWidth, y + newWidth, z + newWidth, newWidth); // back se
        this.leaf = false;
    }

    /**
     * Returns the index of the child that contains the given vector.
     *
     * @param v The vector to check.
     * @return The index of the child that contains the given vector.
     */
    int which(Vector3D v) {
        int quad;
        double halfWidth = w * 0.5;
        if (v.y < y + halfWidth) {
            quad = v.x < x + halfWidth ? 0 : 1;
        } else {
            quad = v.x < x + halfWidth ? 2 : 3;
        }
        quad += v.z < z + halfWidth ? 0 : 4; // z-axis
        return quad;
    }

    /**
     * Inserts a planet into the tree.
     *
     * @param newP The planet to insert.
     */
    void insert(Planet newP) {
        if (this.leaf) {
            // If leaf already contains another planet
            if (this.planet != null) {

                Planet a = this.planet;
                Planet b = newP; // redundant but for comprehension

                this.centerOfMassTimesTotalMass.add(b.position.scalarProduct(b.mass));
                this.totalMass += b.mass;
                this.count++;

                TreeNode cur = this;
                int qA = cur.which(a.position);
                int qB = cur.which(b.position);
                while (qA == qB) {
                    cur.split();
                    cur = cur.children[qA];
                    qA = cur.which(a.position);
                    qB = cur.which(b.position);

                    // Update total center and mass
                    cur.centerOfMassTimesTotalMass.add(a.position.scalarProduct(a.mass));
                    cur.centerOfMassTimesTotalMass.add(b.position.scalarProduct(b.mass));
                    cur.totalMass += b.mass + a.mass;
                    cur.count += 2;
                }

                cur.split();
                cur.children[qA].planet = a;
                cur.children[qB].planet = b;

                // Update center of mass and total for lowest-level child
                cur.children[qA].centerOfMassTimesTotalMass.add(a.position.scalarProduct(a.mass));
                cur.children[qB].centerOfMassTimesTotalMass.add(b.position.scalarProduct(b.mass));
                cur.children[qA].totalMass += a.mass;
                cur.children[qB].totalMass += b.mass;
                cur.children[qA].count++;
                cur.children[qB].count++;

                this.planet = null;
                return;
            }

            this.planet = newP;
            this.centerOfMassTimesTotalMass.add(newP.position.scalarProduct(newP.mass));
            this.totalMass += newP.mass;
            this.count++;
            return;
        }

        // Not a leaf
        this.centerOfMassTimesTotalMass.add(newP.position.scalarProduct(newP.mass));
        this.totalMass += newP.mass;
        this.count++;
        this.children[this.which(newP.position)].insert(newP); //recursive step
    }
}