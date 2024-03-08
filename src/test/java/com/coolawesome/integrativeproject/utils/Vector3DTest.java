package com.coolawesome.integrativeproject.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Vector3DTest {

    @Test
    public void vector3DAdditionProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        Vector3D result = Vector3D.sum(v1, v2);
        assertEquals(new Vector3D(5, 7, 9), result);
    }

    @Test
    public void vector3DDifferenceProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        Vector3D result = Vector3D.difference(v1, v2);
        assertEquals(new Vector3D(-3, -3, -3), result);
    }

    @Test
    public void vector3DMultiplicationProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D result = Vector3D.multiplication(2, v1);
        assertEquals(new Vector3D(2, 4, 6), result);
    }

    @Test
    public void vector3DDotProductProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        double result = Vector3D.dotProduct(v1, v2);
        assertEquals(32, result);
    }

    @Test
    public void vector3DUnitVectorProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D result = Vector3D.unitVector(v1);
        assertEquals(new Vector3D(0.2672612419124244, 0.5345224838248488, 0.8017837257372732), result);
    }

    @Test
    public void vector3DCrossProductProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        Vector3D result = Vector3D.crossProduct(v1, v2);
        assertEquals(new Vector3D(-3, 6, -3), result);
    }

    @Test
    public void vector3DMagnitudeProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        double result = v1.magnitude();
        assertEquals(3.7416573867739413, result);
    }

    @Test
    public void vector3DDistanceProducesCorrectResult() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        double result = v1.distance(v2);
        assertEquals(5.196152422706632, result);
    }
}
