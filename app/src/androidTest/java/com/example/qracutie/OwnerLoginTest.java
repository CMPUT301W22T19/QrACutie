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

public class OwnerLoginTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<Account> rule =
            new ActivityTestRule(OwnerLogin.class, true, true){
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
    public void checkSuccessfulLogin() {
        // Asserts that the current activity is the Account. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", OwnerLogin.class);

        EditText emailText = (EditText) solo.getView(R.id.email);
        emailText.setText("gul@ualberta.ca");

        EditText passText = (EditText) solo.getView(R.id.password);
        passText.setText("qracutie1234");

        Button done = (Button) solo.getView(R.id.owner_login);
        solo.clickOnView(done);

        // Asserts that the current activity switched to ShareableQrActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", OwnerAllPlayers.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
