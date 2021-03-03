package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

internal class PlacementQueryAttributesBuilder {

  companion object {
    internal const val VISITOR_ATTRIBUTE_KEY = "visitor"
    internal const val USER_ATTRIBUTE_KEY = "user"
    internal val USER_ATTRIBUTE_SCHEMA: List<AttributeProperty> = listOf(
        AttributeProperty.StringType("id"),
        AttributeProperty.StringType("email")
    )
    internal const val VIEW_ATTRIBUTE_KEY = "view"
    internal val VIEW_ATTRIBUTE_SCHEMA: List<AttributeProperty> = listOf(
        AttributeProperty.StringType("currency"),
        AttributeProperty.StringType("type"),
        AttributeProperty.ArrayType("subtypes"),
        AttributeProperty.StringType("language")
    )
  }

  internal fun buildJson(
      deviceId: String,
      customAttributes: JsonObject?,
      cachedAttributes: Map<String, JsonObject>
  ) = JsonObject().apply {
    add(VISITOR_ATTRIBUTE_KEY, buildVisitorAttributesJson(deviceId))
    add(USER_ATTRIBUTE_KEY, USER_ATTRIBUTE_SCHEMA, customAttributes, cachedAttributes)
    add(VIEW_ATTRIBUTE_KEY, VIEW_ATTRIBUTE_SCHEMA, customAttributes, cachedAttributes)

    customAttributes?.keySet()
        ?.filter { !has(it) }
        ?.forEach { add(it, customAttributes.get(it)) }
  }

  private fun JsonObject.add(
      key: String,
      schema: List<AttributeProperty>,
      customAttributes: JsonObject?,
      cachedAttributes: Map<String, JsonObject>
  ) {
    val value = (customAttributes?.get(key) as? JsonObject)
        // custom attributes passed by SDK user - should contain all the fields from schema and nothing more
        ?.let { convertToAttribute(schema, it) }
    // cached attributes - skipped if set by SDK user
        ?: cachedAttributes[key]
        // if expected attribute is missing, then we should add empty ones
        ?: buildEmptyAttribute(schema)
    add(key, value)
  }

  internal fun buildVisitorAttributesJson(deviceId: String) = JsonObject().apply {
    addProperty("id", deviceId)
//    addProperty("userAgentString", "")  // TODO set value
  }

  private fun convertToAttribute(
      schema: List<AttributeProperty>,
      sourceJsonObject: JsonObject
  ) = JsonObject().apply {
    schema.forEach {
      when (it) {
        is AttributeProperty.StringType -> addStringValueOrDefault(sourceJsonObject, it.name)
        is AttributeProperty.ArrayType -> addArrayValueOrDefault(sourceJsonObject, it.name)
      }
    }
  }

  private fun buildEmptyAttribute(
      schema: List<AttributeProperty>
  ) = JsonObject().apply {
    schema.forEach {
      when (it) {
        is AttributeProperty.StringType -> addProperty(it.name, "")
        is AttributeProperty.ArrayType -> add(it.name, JsonArray())
      }
    }
  }

  private fun JsonObject.addStringValueOrDefault(sourceJsonObject: JsonObject, propertyName: String) {
    val value = (sourceJsonObject.get(propertyName) as? JsonPrimitive)
        ?.takeIf { it.isString }
        ?: JsonPrimitive("")
    add(propertyName, value)
  }

  private fun JsonObject.addArrayValueOrDefault(sourceJsonObject: JsonObject, propertyName: String) {
    val value = (sourceJsonObject.get(propertyName) as? JsonArray) ?: JsonArray()
    add(propertyName, value)
  }

  internal sealed class AttributeProperty(
      val name: String
  ) {

    class StringType(name: String) : AttributeProperty(name)

    class ArrayType(name: String) : AttributeProperty(name)
  }
}
