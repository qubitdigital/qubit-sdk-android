package com.qubit.android.sdk.internal.eventtracker.connector;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class EventRestModel {
  private final JsonObject eventBody;
  private final EventMeta meta;
  private final EventContext context;

  public EventRestModel(JsonObject eventBody, EventMeta meta, EventContext context) {
    this.eventBody = eventBody;
    this.meta = meta;
    this.context = context;
  }

  public JsonObject getEventBody() {
    return eventBody;
  }

  public EventMeta getMeta() {
    return meta;
  }

  public EventContext getContext() {
    return context;
  }

  public static class Serializer implements JsonSerializer<EventRestModel> {
    @Override
    public JsonElement serialize(EventRestModel src, Type typeOfSrc, JsonSerializationContext context) {

      return null;
//      JsonElement ctx = context.serialize(src.getContext());
//      JsonElement meta = context.serialize(src.getMeta());
//
//      JsonObject event = new JsonObject();
//
//      JsonObject ctx = new JsonObject();
//      ctx.addProperty(FieldKey_ID, QBConfiguration.getConfFromDB().getVisitorID());
//      ctx.addProperty(FieldKeyViewNumber, src.eventID.getViewNumber());
//      ctx.addProperty(FieldKeyViewTs, src.eventID.getViewTs());
//      ctx.addProperty(FieldKeySessionNumber, src.eventID.getSessionNumber());
//      ctx.addProperty(FieldKeySessionTs, src.eventID.getSessionTs());
//      ctx.addProperty(FieldKeySessionViewNumber, src.eventID.getSessionViewNumber());
//      object.add(FieldKeyContext, ctx);
//
//      JsonObject meta = new JsonObject();
//      meta.addProperty(FieldKey_CID, src.eventID.get_cid());
//      meta.addProperty(FieldKey_CTS, src.eventID.get_cts());
//      meta.addProperty(FieldKeyTrackingId, QBConfiguration.trackingID);
//      meta.addProperty(FieldKeyType, QBConfiguration.getConfFromDB().getEventTypeWithVertical(src.eventID.get_type()));
//      meta.addProperty(FieldKeySource, src.eventID.getLibName() + src.eventID.getLibVersion());
//      meta.addProperty(FieldKeySeq, src.eventID.getSequenceNumber());
//      object.add(FieldKeyMeta, meta);
//
//      if(src.objectType != QBEvent.class) {
//        JsonObject newObj = context.serialize(src, src.objectType).getAsJsonObject();
//        Iterator<Map.Entry<String, JsonElement>> it = newObj.entrySet().iterator();
//        while (it.hasNext()) {
//          Map.Entry<String, JsonElement> entry = it.next();
//          object.add(entry.getKey(), entry.getValue());
//        }
//      }
//      src.verifyJson(object);
//
//      return object;

    }
  }


}
