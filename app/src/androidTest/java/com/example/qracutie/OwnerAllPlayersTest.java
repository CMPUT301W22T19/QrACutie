package com.example.qracutie;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class OwnerAllPlayersTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<Account> rule =
            new ActivityTestRule(OwnerAllPlayers.class, true, true){
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
    public void checkChangeRecyclerView() {
        // Asserts that the current activity is the Account. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", OwnerAllPlayers.class);

        LinearLayout playerView = (LinearLayout) solo.getView(R.id.L_player);
        LinearLayout qrView = (LinearLayout) solo.getView(R.id.L_Qr);
        int playerVisibility = playerView.getVisibility();
        int qrVisibility = qrView.getVisibility();
        assertEquals(VISIBLE, playerVisibility);
        assertEquals(GONE, qrVisibility);

        androidx.appcompat.widget.AppCompatButton switchButton = (androidx.appcompat.widget.AppCompatButton) solo.getView(R.id.to_qr_list);
        solo.clickOnView(switchButton);

        int newPlayerVisibility = playerView.getVisibility();
        int newQrVisibility = qrView.getVisibility();
        assertEquals(GONE, newPlayerVisibility);
        assertEquals(VISIBLE, newQrVisibility);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
