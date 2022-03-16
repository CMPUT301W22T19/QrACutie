package com.example.qracutie;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the Comment class which will execute on the
 * development machine
 */
public class CommentTest {

    private Comment comment;

    @Before
    public void createComment() {
        comment = new Comment("Test Comment Text", "User1", "04/12/2022");
    }

    /**
     * Asserts that Comment returns appropriate comment text when getCommentName is called
     */
    @Test
    public void testGetCommentName() {
        // assert that the comment can return the comment text
        assertTrue(comment.getCommentName().equals("Test Comment Text"));
    }

    /**
     * Asserts that Comment returns appropriate uid when getUserName is called
     */
    @Test
    public void testGetUserName() {
        // assert that the comment can return the uid
        assertTrue(comment.getUserName().equals("User1"));
    }

    /**
     * Asserts that Comment returns appropriate date when getDate is called
     */
    @Test
    public void testGetDate() {
        // assert that the comment can return the date
        assertTrue(comment.getDate().equals("04/12/2022"));
    }
}