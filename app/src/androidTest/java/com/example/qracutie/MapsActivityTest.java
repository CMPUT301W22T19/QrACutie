package com.example.qracutie;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.Button;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for MapsActivity. All the UI tests are written here. Robotium test framework is
 used
 */
public class MapsActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MapsActivity> rule =
            new ActivityTestRule<>(MapsActivity.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if activity correctly switches when Back button is pressed
     */
    @Test
    public void checkMainActivitySwitch() {
        // Asserts that the current activity is the MapsActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MapsActivity.class);

        // click the back button
        assertTrue(solo.waitForText("Back", 1, 2000));
        Button button = (Button) solo.getView(R.id.back);
        solo.clickOnView(button);

        // Asserts that the current activity switched to MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
