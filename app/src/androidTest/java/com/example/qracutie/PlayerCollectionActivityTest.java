package com.example.qracutie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
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
public class PlayerCollectionActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<PlayerCollectionActivity> rule =
            new ActivityTestRule<PlayerCollectionActivity>(PlayerCollectionActivity.class, true, true){
                @Override
                protected Intent getActivityIntent (){
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.EXTRA_PLAYER_COLLECTION_USERNAME,"user864321");
                    intent.putExtra(MainActivity.EXTRA_PLAYER_USERNAME, "user864321");
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
     * checks if QR codes are displayed as list items within the activity
     */
    @Test
    public void checkQRCodeList() {
        // Asserts that the current activity is PlayerCollectionActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", PlayerCollectionActivity.class);

        // select a QR codes from the list, and assert they are the right value
        ListView qrCodeList = (ListView) solo.getView(R.id.qr_code_list);

        GameQRCode qrCode1 = (GameQRCode) qrCodeList.getItemAtPosition(0);
        assertEquals(34, qrCode1.getPoints());

        GameQRCode qrCode2 = (GameQRCode) qrCodeList.getItemAtPosition(1);
        assertEquals(42, qrCode2.getPoints());
    }

    /**
     * checks if activity correctly switches when qr code is selected from list
     */
    @Test
    public void checkCommentActivitySwitch() {
        // Asserts that the current activity is PlayerCollectionActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", PlayerCollectionActivity.class);

        // select a QR code from the list
        assertTrue(solo.waitForText("Points", 1, 2000));
        ListView qrCodeList = (ListView) solo.getView(R.id.qr_code_list);
        solo.clickOnView(qrCodeList.getChildAt(0));

        // Asserts that the current activity switched to AccountActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", CommentsPage.class);
    }

    /**
     * checks if the page statistics are appropriate for the mocked data
     */
    @Test
    public void checkStatistics() {
        // Asserts that the current activity is PlayerCollectionActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", PlayerCollectionActivity.class);

        // check value of total points
        TextView totalPoints = (TextView) solo.getView(R.id.collection_total_points_val);
        assertEquals("76", totalPoints.getText());

        // check value for total codes
        TextView totalCodes = (TextView) solo.getView(R.id.collection_total_codes_val);
        assertEquals("2", totalCodes.getText());

        // check value of highest points
        TextView highestScore = (TextView) solo.getView(R.id.collection_highest_score_val);
        assertEquals("42", highestScore.getText());

        // check value of lowest points
        TextView lowestScore = (TextView) solo.getView(R.id.collection_lowest_score_val);
        assertEquals("34", lowestScore.getText());
    }

    /**
     * checks if the options button on the QR code pops out a window that lets the code be deleted
     */
    @Test
    public void checkDeleteQRCode() {
        // Asserts that the current activity is PlayerCollectionActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", PlayerCollectionActivity.class);

        // select the more options button on a QR code from the list
        assertTrue(solo.waitForText("Points", 1, 2000));
        ListView qrCodeList = (ListView) solo.getView(R.id.qr_code_list);
        View view = qrCodeList.getChildAt(0);
        solo.clickOnView(view.findViewById(R.id.options_button));

        // Check that the more options fragment appears
        assertTrue(solo.waitForText("Would you like to delete this QR Code?", 1, 2000));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}