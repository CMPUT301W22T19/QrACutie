package com.example.qracutie;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AccountTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<Account> rule =
            new ActivityTestRule(Account.class, true, true){
                @Override
                protected Intent getActivityIntent (){
                    Intent intent = new Intent();
                    intent.putExtra("username","userTest");
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
     * checks if activity correctly switches when Back button is pressed
     */
    @Test
    public void checkShareableQrActivitySwitch() {
        // Asserts that the current activity is the Account. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", Account.class);

        // click the back button
        ImageButton button = (ImageButton) solo.getView(R.id.user_qr_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to ShareableQrActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ShareableQrActivity.class);
    }

    @Test
    public void checkDataInput(){
        EditText emailText = (EditText) solo.getView(R.id.new_email);
        emailText.setText("testEmail");

        EditText phoneText = (EditText) solo.getView(R.id.new_phonenumber);
        phoneText.setText("000000000");

        Button done = (Button) solo.getView(R.id.done);
        solo.clickOnView(done);
        String newEmail = emailText.getText().toString();
        String newPhone = phoneText.getText().toString();

        assertEquals("testEmail", newEmail);
        assertEquals("000000000", newPhone);
    }

    /**
     * checks if activity correctly switches when the qr button is pressed
     */
    @Test
    public void checkLoginQrActivitySwitch(){
        // Asserts that the current activity is the Account. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", Account.class);

        // click the qr button
        ImageButton button = (ImageButton) solo.getView(R.id.user_qr_button);
        solo.clickOnView(button);

        // Asserts that the current activity switched to ShareableQrActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ShareableQrActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
