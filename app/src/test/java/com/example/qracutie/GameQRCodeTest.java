package com.example.qracutie;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the GameQRCode class which will execute on the
 * development machine
 */
public class GameQRCodeTest {

    private GameQRCode qrCode;

    @Before
    public void createPlayer() {
        qrCode = new GameQRCode("testHash1", 10);
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
        // assert that the qr code has a hash
        assertTrue(qrCode.getHash().equals("testHash1"));
    }

    /**
     * Asserts that setLatitude and getLatitude methods function as expected
     */
    @Test
    public void testSetGetLatitude() {
        // assert that the qr has 0 latitude to start
        assertEquals(0, qrCode.getLatitude(), 0.001);

        // set new latitude
        double latitude = 197.2310032;
        qrCode.setLatitude(latitude);

        // assert that the qr code has the new latitude
        assertEquals(latitude, qrCode.getLatitude(), 0.001);
    }

    /**
     * Asserts that setLatitude and getLatitude methods function as expected
     */
    @Test
    public void testSetGetLongitude() {
        // assert that the qr has 0 longitude to start
        assertEquals(0, qrCode.getLongitude(), 0.001);

        // set new longitude
        double longitude = 421.5618032;
        qrCode.setLongitude(longitude);

        // assert that the qr code has the new longitude
        assertEquals(longitude, qrCode.getLongitude(), 0.001);
    }
}
