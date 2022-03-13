package com.example.qracutie;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the Player class which will execute on the
 * development machine
 */
public class PlayerTest {

    private Player player;

    @Before
    public void createPlayer() {
        player = new Player("TestPlayerHashCode1");
    }

    /**
     * Adds GameQRCode objects using the addGameQRCode function, and then
     * asserts that the getTotalCodes function returns the number of added
     * codes.
     */
    @Test
    public void testAddGameQRCode() {
        // assert that the player has 0 codes to start
        assertEquals(0, player.getTotalCodes());

        // add two game QR codes to the player
        GameQRCode gc1 = new GameQRCode();
        gc1.points = 50;
        GameQRCode gc2 = new GameQRCode();
        gc2.points = 30;
        player.addGameQRCode(gc1, null);
        player.addGameQRCode(gc2, null);

        // ensure the player has two codes now
        assertEquals(2, player.getTotalCodes());
    }

    /**
     * Adds GameQRCode objects using the addGameQRCode function, and then
     * asserts that the getPointTotal function returns the sum of qr code
     * points.
     */
    @Test
    public void testGetPointTotal() {
        // assert that the player has 0 total points to start
        assertEquals(0, player.getPointTotal());

        // add two game QR codes to the player
        GameQRCode gc1 = new GameQRCode();
        gc1.points = 50;
        GameQRCode gc2 = new GameQRCode();
        gc2.points = 30;
        player.addGameQRCode(gc1, null);
        player.addGameQRCode(gc2, null);

        // ensure the player has 80 total points now
        assertEquals(80, player.getPointTotal());
    }

    /**
     * Adds GameQRCode objects using the addGameQRCode function, and then
     * asserts that the getHighestQRCode function returns the highest-pointed
     * QR code's value
     */
    @Test
    public void testGetHighestQRCode() {
        // assert that the player returns 0 when no QR codes have been added
        assertEquals(0, player.getHighestQRCode());

        // add two game QR codes to the player
        GameQRCode gc1 = new GameQRCode();
        gc1.points = 50;
        GameQRCode gc2 = new GameQRCode();
        gc2.points = 30;
        player.addGameQRCode(gc1, null);
        player.addGameQRCode(gc2, null);

        // ensure the player returns 50 for the highest found qr code
        assertEquals(50, player.getHighestQRCode());
    }

    /**
     * Adds GameQRCode objects using the addGameQRCode function, and then
     * asserts that the getHighestQRCode function returns the highest-pointed
     * QR code's value
     */
    @Test
    public void testGetLowestQRCode() {
        // assert that the player returns 0 when no QR codes have been added
        assertEquals(0, player.getLowestQRCode());

        // add two game QR codes to the player
        GameQRCode gc1 = new GameQRCode();
        gc1.points = 50;
        GameQRCode gc2 = new GameQRCode();
        gc2.points = 30;
        player.addGameQRCode(gc1, null);
        player.addGameQRCode(gc2, null);

        // ensure the player returns 30 for the lowest found qr code
        assertEquals(30, player.getLowestQRCode());
    }
}
