package com.qubit.android.sdk.testapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.qubit.android.sdk.api.QubitSDK
import com.qubit.android.sdk.api.tracker.event.QBEvents
import com.google.gson.JsonObject

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    Log.i(TAG, "Send view event Main activity")
    Log.i(TAG, QubitSDK.getTrackingId())
    Log.i(TAG, QubitSDK.getDeviceId())
    QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(EVENT_TYPE_VIEW, "{ \"type\" : \"Main\" }"))

    findViewById<View>(R.id.send_view_event_button).setOnClickListener {
      Log.i(TAG, "Send view event button clicked")
      // Example of sending event
      QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(EVENT_TYPE_VIEW, "{ \"type\" : \"button1\" }"))
      Log.i(TAG, QubitSDK.tracker().lookupData.ipAddress)
    }

    findViewById<View>(R.id.send_interaction_event_button).setOnClickListener {
      Log.i(TAG, "Send interaction event button clicked")
      // Example of sending event
      QubitSDK.tracker().sendEvent(
          QBEvents.fromJsonString(EVENT_TYPE_INTERACTION, "{ \"type\" : \"buttonInteraction\" }"))
    }

    findViewById<View>(R.id.send_20_events_button).setOnClickListener {
      Log.i(TAG, "Send 20 events button clicked")

      for (i in 0..19) {
        QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(EVENT_TYPE_VIEW, "{ \"type\" : \"buttons\" }"))
      }
    }

    findViewById<View>(R.id.get_experience).setOnClickListener {
      val experienceId = 144119
      Log.i(TAG, "Get experience with id $experienceId events button clicked")
      val list = arrayListOf(experienceId)

      QubitSDK.tracker().getExperiences(
          list,
          { experienceList -> experienceList.forEach { experience ->
            Log.d(TAG, "Experience receive ${experience.experiencePayload}")
            experience.shown()
          }},
          { throwable -> Log.e(TAG,"Error: ", throwable) },
          variation = null,
          preview = true,
          ignoreSegments = true
      )
    }

    findViewById<View>(R.id.get_experience_default).setOnClickListener {
      Log.i(TAG, "Get all experience events button clicked")
      val list = listOf<Int>()
      getExperienceWithIds(list)
    }
  }

  private fun getExperienceWithIds(list: List<Int>) {
    QubitSDK.tracker().getExperiences(
        list,
        { experienceList -> experienceList.forEach { experience ->
          Log.d(TAG, "Experience receive ${experience.experiencePayload}")
          Log.d(TAG, "Experience receive ${experience.experiencePayload.payload.get("free_shipping")}")
          experience.shown()
        }},
        { throwable -> Log.e(TAG,"Error: ", throwable) }
    )
  }

  companion object {

    private const val TAG = "qb-testapp"
    private const val EVENT_TYPE_VIEW = "ecView"
    private const val EVENT_TYPE_INTERACTION = "ecInteraction"
  }
}
