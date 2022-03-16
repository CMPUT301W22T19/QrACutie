package com.example.qracutie;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the Comment class which will execute on the
 * development machine
 */
public class CommentsTest {

    private Comment comment;

    @Before
    public void createComment() {
        comment = new Comment("TestCommentText", "TestUID", "2022-03-16");
    }

    /**
     * Asserts that the string passed in the constructor is the same as what's returned by
     * getCommentName()
     */
    @Test
    public void testGetCommentName() {
        // assert that the comment has 0 codes to start
        assertEquals("TestCommentText", comment.getCommentName());

    }

    /**
     * Asserts that the string passed in the constructor is the same as what's returned by
     * getUserName()
     */
    @Test
    public void testGetUserName() {
        // assert that the comment has 0 codes to start
        assertEquals("TestUID", comment.getUserName());

    }

    /**
     * Asserts that the string passed in the constructor is the same as what's returned by
     * getDate()
     */
    @Test
    public void testGetDate() {
        // assert that the comment has 0 codes to start
        assertEquals("2022-03-16", comment.getDate());

    }
}
