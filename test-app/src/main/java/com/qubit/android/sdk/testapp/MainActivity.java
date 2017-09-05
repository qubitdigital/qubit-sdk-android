package com.qubit.android.sdk.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.qubit.android.sdk.api.QubitSDK;
import com.qubit.android.sdk.api.tracker.event.QBEvents;


public class MainActivity extends AppCompatActivity {

  public static final String TAG = "qb-testapp";

  @SuppressWarnings("checkstyle:magicnumber")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.send_event_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "Send event button clicked");
        // Example of sending event
        QubitSDK.tracker().sendEvent("eventType",
            QBEvents.fromJsonString("{ \"viewId\" : \"button\" }"));
      }
    });

    findViewById(R.id.send_20_events_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "Send 20 events button clicked");

        for (int i = 0; i < 20; i++) {
          QubitSDK.tracker().sendEvent("eventType2",
              QBEvents.fromJsonString("{ \"viewId\" : \"buttons\" }"));
        }
      }
    });
  }

}
