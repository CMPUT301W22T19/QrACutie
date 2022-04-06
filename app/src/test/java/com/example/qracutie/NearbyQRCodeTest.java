package com.example.qracutie;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Unit tests for the NearbyQRCode class which will execute on the
 * development machine
 */
public class NearbyQRCodeTest {

    private NearbyQRCode qrCode;

    @Before
    public void createCode() {
        qrCode = new NearbyQRCode(10, "testHash1", "1000", null);
    }

    /**
     * Asserts that getPoints method returns the number of points specified in the constructor
     */
    @Test
    public void testGetPoints() {
        // assert that the qr code has 10 points
        assertEquals(10, qrCode.getPoints());
    }

    /**
     * Asserts that getHash method returns the qrCode hash specified in the constructor
     */
    @Test
    public void testGetHash() {
        // assert that the qr code has a hash set
        assertTrue(qrCode.getHash().equals("testHash1"));
    }

    /**
     * Asserts that getDistance methods returns the string distance
     */
    @Test
    public void testGetDistance() {
        // assert that the qr code has a distance set
        assertTrue(qrCode.getDistance().equals("1000"));
    }
}
