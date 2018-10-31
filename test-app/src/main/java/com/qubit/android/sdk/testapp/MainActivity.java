package com.qubit.android.sdk.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.qubit.android.sdk.api.QubitSDK;
import com.qubit.android.sdk.api.tracker.event.QBEvents;
import com.qubit.android.sdk.internal.experience.ExperienceListener;
import com.qubit.android.sdk.internal.experience.ExperienceObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

  public static final String TAG = "qb-testapp";
  public static final String EVENT_TYPE_VIEW = "ecView";
  public static final String EVENT_TYPE_INTERACTION = "ecInteraction";

  @SuppressWarnings("checkstyle:magicnumber")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Log.i(TAG, "Send view event Main activity");
    Log.i(TAG, QubitSDK.getTrackingId());
    Log.i(TAG, QubitSDK.getDeviceId());
    QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(EVENT_TYPE_VIEW, "{ \"type\" : \"Main\" }"));

    findViewById(R.id.send_view_event_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "Send view event button clicked");
        // Example of sending event
        QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(EVENT_TYPE_VIEW, "{ \"type\" : \"button1\" }"));
        Log.i(TAG, QubitSDK.tracker().getLookupData().getIpAddress());
      }
    });

    findViewById(R.id.send_interaction_event_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "Send interaction event button clicked");
        // Example of sending event
        QubitSDK.tracker().sendEvent(
            QBEvents.fromJsonString(EVENT_TYPE_INTERACTION, "{ \"type\" : \"buttonInteraction\" }"));
      }
    });

    findViewById(R.id.send_20_events_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "Send 20 events button clicked");

        for (int i = 0; i < 20; i++) {
          QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(EVENT_TYPE_VIEW, "{ \"type\" : \"buttons\" }"));
        }
      }
    });

    findViewById(R.id.get_experience).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "Get experience events button clicked");

        List<String> list = new ArrayList<>();
        list.add("139731");

        QubitSDK.tracker().getExperiences(list, null, null, null, new ExperienceListener() {
          @Override
          public void onExperienceReceive(ExperienceObject experienceObject) {
            Log.d(TAG, experienceObject.getExperienceModel().toString());
          }

          @Override
          public void onError() {

          }
        });
      }
    });
  }

}
