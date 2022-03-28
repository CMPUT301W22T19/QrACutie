package com.example.qracutie;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CameraActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<CameraActivity> rule =
            new ActivityTestRule<>(CameraActivity.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if activity correctly switches to SaveQR activity when button is pressed
     */
    @Test
    public void checkCameraActivitySwitch() {
        // Asserts that the current activity is the Camera. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", CameraActivity.class);

        // click the QR button
        assertTrue(solo.waitForText("QR Code Found", 1, 2000));
        Button button = (Button) solo.getView(R.id.qrCodeFoundButton);
        solo.clickOnView(button);

        // Asserts that the current activity switched to SAVEQR Activity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", SaveQRActivity.class);
    }


    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
