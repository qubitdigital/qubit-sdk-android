package com.qubit.android.sdk.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.qubit.android.sdk.api.QubitSDK;
import com.qubit.android.sdk.api.tracker.event.QBEvents;


public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.send_event_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i("qb-testapp", "Send event button clicked");
        // Example of sending event
        QubitSDK.tracker().sendEvent("eventType",
            QBEvents.fromJsonString("{ \"viewId\" : \"button\" }"));
      }
    });
  }

}
