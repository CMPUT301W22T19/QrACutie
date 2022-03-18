package com.example.qracutie;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CommentsActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<CommentsPage> rule =
            new ActivityTestRule<>(CommentsPage.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if activity correctly switches when done button is pressed
     */
    @Test
    public void checkMainActivitySwitch() {
        // Asserts that the current activity is the CommentsPage. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", CommentsPage.class);

        // click the account button
        assertTrue(solo.waitForText("Back", 1, 2000));
        Button button = (Button) solo.getView(R.id.done_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * checks if Comment added
     */
    @Test
    public void checkCommentAdded() {
        // Asserts that the current activity is the CommentsPage. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", CommentsPage.class);

        // click the add comment button
        assertTrue(solo.waitForText("Add Comment", 1, 2000));
        CheckBox checkBox = (CheckBox) solo.getView(R.id.location);
        solo.clickOnView(checkBox);

        // Asserts that the current activity switched to MainActivity. Otherwise, show “Wrong Activity”
        ListView listview = (ListView) rule.getActivity().findViewById(R.id.comment_list);

        assertThat(listview.getCount(), greaterThan(0));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
