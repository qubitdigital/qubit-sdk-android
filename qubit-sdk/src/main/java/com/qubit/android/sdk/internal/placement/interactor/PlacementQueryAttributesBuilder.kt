package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

internal class PlacementQueryAttributesBuilder {

  companion object {
    internal const val VISITOR_ATTRIBUTE_KEY = "visitor"
    internal const val USER_ATTRIBUTE_KEY = "user"
    private val USER_ATTRIBUTE_SCHEMA: List<AttributeProperty> = listOf(
        AttributeProperty.StringType("id"),
        AttributeProperty.StringType("email")
    )
    internal const val VIEW_ATTRIBUTE_KEY = "view"
    private val VIEW_ATTRIBUTE_SCHEMA: List<AttributeProperty> = listOf(
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
    addCustomAttributes(customAttributes)
  }

  internal fun buildVisitorAttributesJson(deviceId: String) = JsonObject().apply {
    addProperty("id", deviceId)
//    addProperty("userAgentString", "")  // TODO set value
  }

  private fun JsonObject.add(
      key: String,
      schema: List<AttributeProperty>,
      customAttributes: JsonObject?,
      cachedAttributes: Map<String, JsonObject>
  ) {
    val valueJson = JsonObject().apply {
      addCustomProperties(schema, customAttributes?.get(key) as? JsonObject)
      addCachedProperties(schema, cachedAttributes[key])
      addMissingEmptySchemaProperties(schema)
    }
    add(key, valueJson)
  }

  private fun JsonObject.addCustomProperties(
      schema: List<AttributeProperty>,
      properties: JsonObject?
  ) {
    properties?.keySet()
        ?.forEach { addWithTypeCheck(schema, it, properties.get(it)) }
  }

  private fun JsonObject.addCachedProperties(
      schema: List<AttributeProperty>,
      properties: JsonObject?
  ) {
    val schemaPropertyNames = schema.map { it.name }
    properties?.keySet()
        ?.filter { schemaPropertyNames.contains(it) }
        ?.filter { !keySet().contains(it) }
        ?.forEach { addWithTypeCheck(schema, it, properties.get(it)) }
  }

  private fun JsonObject.addWithTypeCheck(
      schema: List<AttributeProperty>,
      name: String,
      property: JsonElement
  ) {
    val validType = when (schema.firstOrNull { it.name == name }) {
      is AttributeProperty.StringType -> property is JsonPrimitive && property.isString
      is AttributeProperty.ArrayType -> property is JsonArray
      null -> true
    }
    if (validType) {
      add(name, property)
    }
  }

  private fun JsonObject.addMissingEmptySchemaProperties(schema: List<AttributeProperty>) {
    schema.forEach {
      if (!keySet().contains(it.name)) {
        when (it) {
          is AttributeProperty.StringType -> addProperty(it.name, "")
          is AttributeProperty.ArrayType -> add(it.name, JsonArray())
        }
      }
    }
  }

  private fun JsonObject.addCustomAttributes(customAttributes: JsonObject?) {
    customAttributes?.keySet()
        ?.filter { !has(it) }
        ?.forEach { add(it, customAttributes.get(it)) }
  }

  internal sealed class AttributeProperty(
      val name: String
  ) {

    class StringType(name: String) : AttributeProperty(name)

    class ArrayType(name: String) : AttributeProperty(name)
  }
}
