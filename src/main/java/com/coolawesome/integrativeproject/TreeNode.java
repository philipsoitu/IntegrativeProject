package com.coolawesome.integrativeproject;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public // Quadtree!

class TreeNode {
    double x, y, w;
    TreeNode[] children; // Children
    boolean leaf;
    Particle particle;

    Vector centerOfMassTimesTotalMass; // http://hyperphysics.phy-astr.gsu.edu/hbase/cm.html
    double totalMass; // Total mass
    int count; // Number of particles

    public TreeNode(double x, double y, double w) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.leaf = true;
        this.particle = null;
        this.children = new TreeNode[4];

        this.centerOfMassTimesTotalMass = new Vector(0, 0);
        this.totalMass = 0;
        this.count = 0;
    }

    void split() {
        double newWidth = w * 0.5;
        children[0] = new TreeNode(x, y, newWidth); // nw
        children[1] = new TreeNode(x + newWidth, y, newWidth); // ne
        children[2] = new TreeNode(x, y + newWidth, newWidth); // sw
        children[3] = new TreeNode(x + newWidth, y + newWidth, newWidth); // se
        this.leaf = false;
    }

    // Return index of child that contains given vector
    int which(Vector v) {
        double halfWidth = w * 0.5;
        if (v.y < y + halfWidth) {
            return v.x < x + halfWidth ? 0 : 1;
        } else {
            return v.x < x + halfWidth ? 2 : 3;
        }
    }

        //TODO: fix center of mass!!
    void insert(Particle newP) {
        if (this.leaf) {
            // If leaf already contains another particle
            if (this.particle != null) {

                Particle a = this.particle;
                Particle b = newP;

                this.totalCenter.add(b.position);
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
                    cur.totalCenter.add(a.position);
                    cur.totalCenter.add(b.position);
                    cur.totalMass += b.mass + a.mass;
                    cur.count += 2;
                }

                cur.split();
                cur.children[qA].particle = a;
                cur.children[qB].particle = b;

                // Update center of mass and total for lowest-level child
                cur.children[qA].totalCenter.add(a.position);
                cur.children[qB].totalCenter.add(b.position);
                cur.children[qA].totalMass += b.mass;
                cur.children[qB].totalMass += b.mass;
                cur.children[qA].count++;
                cur.children[qB].count++;

                this.particle = null;
                return;
            }

            this.particle = newP;
            this.totalCenter.add(newP.position);
            this.totalMass += newP.mass;
            this.count++;
            return;
        }

        // Not a leaf
        this.totalCenter.add(newP.position);
        this.totalMass += newP.mass;
        this.count++;
        this.children[this.which(newP.position)].insert(newP);
    }

    void draw(GraphicsContext gc, double camX, double camY) {
        if (!this.leaf) {
            for (TreeNode tn : children) {
                tn.draw(gc, camX, camY);
            }
        }

        gc.setFill(Color.BLACK);
        gc.strokeRect(this.x + camX, this.y + camY, this.w, this.w);
    }

}
