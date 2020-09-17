package com.qubit.android.sdk.testapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.qubit.android.sdk.api.QubitSDK
import com.qubit.android.sdk.api.placement.Placement
import com.qubit.android.sdk.api.placement.PlacementMode
import com.qubit.android.sdk.api.placement.PlacementPreviewOptions
import com.qubit.android.sdk.api.tracker.event.QBEvents

class MainActivity : AppCompatActivity() {

  private var placement: Placement? = null

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

    findViewById<View>(R.id.get_example_placement).setOnClickListener {
      Log.i(TAG, "'Get example placement' button clicked")
      QubitSDK.getPlacement(
          SAMPLE_PLACEMENT_ID,
          PlacementMode.LIVE,
          PlacementPreviewOptions(SAMPLE_CAMPAIGN_ID, null),
          {
            placement = it
            Log.d(TAG, "Placement received: ${it?.content}")
            findViewById<View>(R.id.placement_impression).isEnabled = true
            findViewById<View>(R.id.placement_clickthrough).isEnabled = true
          },
          { throwable -> Log.e(TAG, "Error: ", throwable) }
      )
    }

    findViewById<View>(R.id.placement_impression).setOnClickListener {
      placement?.impression() ?: Toast.makeText(this, "No placement loaded", Toast.LENGTH_LONG).show()
    }

    findViewById<View>(R.id.placement_clickthrough).setOnClickListener {
      placement?.clickthrough() ?: Toast.makeText(this, "No placement loaded", Toast.LENGTH_LONG).show()
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
    private const val SAMPLE_PLACEMENT_ID = "tsOujouCSSKJGSCMUsmQRw"
    private const val SAMPLE_CAMPAIGN_ID = "1ybrhki9RvKWpA-9veLQSg"
  }
}
