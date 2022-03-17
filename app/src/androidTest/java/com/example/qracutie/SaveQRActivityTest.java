package com.example.qracutie;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SaveQRActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SaveQRActivity> rule =
            new ActivityTestRule<>(SaveQRActivity.class, true, true);


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
        // Asserts that the current activity is the SaveQRActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", SaveQRActivity.class);

        // click the account button
        assertTrue(solo.waitForText("Done", 1, 2000));
        Button button = (Button) solo.getView(R.id.done_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * checks if Check Box is checked
     */
    @Test
    public void checkCheckBoxChecked() {
        // Asserts that the current activity is the SaveQRActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", SaveQRActivity.class);

        // click the account button
        assertTrue(solo.waitForText("Save Location", 1, 2000));
        CheckBox checkBox = (CheckBox) solo.getView(R.id.location);
        solo.clickOnView(checkBox);

        // Asserts that the current activity switched to MainActivity. Otherwise, show “Wrong Activity”
        solo.isCheckBoxChecked("Save Location");
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
