package com.example.qracutie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for PlayerCollectionActivity. All the UI tests are written here. Robotium test
 * framework is used
 */
public class CommentsActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<CommentsPage> rule =
            new ActivityTestRule<CommentsPage>(CommentsPage.class, true, true){
                @Override
                protected Intent getActivityIntent (){
                    Intent intent = new Intent();
                    intent.putExtra(PlayerCollectionActivity.EXTRA_COMMENTS_USERNAME,"user160366");
                    intent.putExtra(PlayerCollectionActivity.EXTRA_COMMENTS_QRCODE, "11");
                    return intent;
                }
            };

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if comment can be added to a qr code and then viewed
     */
    @Test
    public void testAddComment() {
        // Asserts that the current activity is CommentsActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", CommentsPage.class);

        // add a comment to the list
        EditText textField = (EditText) solo.getView(R.id.add_comment_field);
        solo.enterText(textField, "Rebecca is a cool TA");

        Button addCommentButton = (Button) solo.getView(R.id.add_comment_button);
        solo.clickOnView(addCommentButton);

        // assert that the comment has been added to the list
        assertTrue(solo.waitForText("Rebecca is a cool TA", 1, 2000));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}