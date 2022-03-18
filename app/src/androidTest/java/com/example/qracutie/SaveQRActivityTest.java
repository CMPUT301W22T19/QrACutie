package com.example.qracutie;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

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
    public ActivityTestRule<SaveQR> rule =
            new ActivityTestRule<SaveQR>(SaveQR.class, true, true){
                @Override
                protected Intent getActivityIntent (){
                    Intent intent = new Intent();
                    intent.putExtra("qrcode","11");
                    return intent;
                }
            };
//    public ActivityTestRule<SaveQR> rule =
//            new ActivityTestRule<>(SaveQR.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if activity correctly switches when Done button is pressed
     */
    @Test
    public void DoneButton() {


        // Asserts that the current activity is switched too SaveQR. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", SaveQR.class);

        // click the Done button
        assertTrue(solo.waitForText("Done", 1, 2000));
        Button button = (Button) solo.getView(R.id.done_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * checks if activity correctly switches when Capture button is pressed
     */
    @Test
    public void checkCaptureButton() {


        // Asserts that the current activity is the SaveQR Activity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", SaveQR.class);

        // click the capture button
        assertTrue(solo.waitForText("Capture", 1, 2000));
        Button button = (Button) solo.getView(R.id.CapturePic);
        solo.clickOnView(button);

        // Asserts that the current activity stays in SaveQR activity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", SaveQR.class);
    }


    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
