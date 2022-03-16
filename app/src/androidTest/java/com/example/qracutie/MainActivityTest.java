package com.example.qracutie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
public class MainActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if activity correctly switches when account button is pressed
     */
    @Test
    public void checkAccountActivitySwitch() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click the account button
        assertTrue(solo.waitForText("Account", 1, 2000));
        Button button = (Button) solo.getView(R.id.user_account_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to AccountActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", Account.class);
    }

    /**
     * checks if activity correctly switches when collection button is pressed
     */
    @Test
    public void checkCollectionActivitySwitch() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click the account button
        assertTrue(solo.waitForText("Collection", 1, 2000));
        Button button = (Button) solo.getView(R.id.collection_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to AccountActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", PlayerCollectionActivity.class);
    }

    /**
     * checks if username is same in collection activity as it is in the main activity
     */
    @Test
    public void checkConsistentCollectionUsername() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // store username
        TextView usernameView = (TextView) solo.getView(R.id.display_name);
        String username = usernameView.getText().toString();

        // click the account button
        assertTrue(solo.waitForText("Collection", 1, 2000));
        Button button = (Button) solo.getView(R.id.collection_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to AccountActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", PlayerCollectionActivity.class);

        // assert that username appears in collection activity
        assertTrue(solo.waitForText(username, 1, 2000));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}