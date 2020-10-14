package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.qubit.android.sdk.internal.placement.interactor.PlacementQueryAttributesBuilder.Companion.USER_ATTRIBUTE_KEY
import com.qubit.android.sdk.internal.placement.interactor.PlacementQueryAttributesBuilder.Companion.VIEW_ATTRIBUTE_KEY
import com.qubit.android.sdk.internal.placement.interactor.PlacementQueryAttributesBuilder.Companion.VISITOR_ATTRIBUTE_KEY
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class PlacementQueryAttributesBuilderTest {

  companion object {
    private const val DEVICE_ID_1 = "device_id_1"
    private const val DEVICE_ID_2 = "device_id_2"
    private const val USER_NAME_1 = "user_name_1"
    private const val USER_NAME_2 = "user_name_2"
    private const val EMAIL = "email@email.com"
    private const val CURRENCY = "currency"
    private const val VIEW_TYPE_1 = "view_type_1"
    private const val VIEW_TYPE_2 = "view_type_2"
    private val SUBTYPES = listOf("subtype1", "subtype2")
    private const val SUBTYPES_STRING = """["subtype1", "subtype2"]"""
    private const val LANGUAGE = "language"
  }

  private lateinit var builder: PlacementQueryAttributesBuilder

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    builder = PlacementQueryAttributesBuilder()
  }

  /********** visitor attributes **********/

  @Test
  fun `visitor attributes should be set by SDK ignoring cached value`() {
    // 'DEVICE_ID_1' value cached
    val cachedAttributes = mapOf(
        VISITOR_ATTRIBUTE_KEY to builder.buildVisitorAttributesJson(DEVICE_ID_1)
    )
    // no value passed by user
    val userAttributes = null

    // 'DEVICE_ID_2' set by SDK
    val result = execute(cachedAttributes, userAttributes, DEVICE_ID_2)

    // 'DEVICE_ID_2' is expected, default (empty) user&view attributes
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_2"
          },
          "user": {
            "name": "",
            "email": ""
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": [],
            "language": ""
          }
        }""", result)
  }

  @Test
  fun `visitor attributes should be set by SDK ignoring value passed by user`() {
    //  value cached
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'DEVICE_ID_1' passed by SDK user
    val userAttributes = JsonObject().apply {
      add(VISITOR_ATTRIBUTE_KEY, builder.buildVisitorAttributesJson(DEVICE_ID_1))
    }

    // 'DEVICE_ID_2' set by SDK
    val result = execute(cachedAttributes, userAttributes, DEVICE_ID_2)

    // 'DEVICE_ID_2' is expected, default (empty) user&view attributes
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_2"
          },
          "user": {
            "name": "",
            "email": ""
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": [],
            "language": ""
          }
        }""", result)
  }

  /********** user-event attributes **********/

  @Test
  fun `if user-event attribute is cached and no value is passed by user, then cached value should be used`() {
    // 'USER_NAME_1' value cached
    val cachedAttributes = mapOf(
        USER_ATTRIBUTE_KEY to buildUserEventAttributes(USER_NAME_1)
    )
    // no value passed by user
    val userAttributes = null

    val result = execute(cachedAttributes, userAttributes)

    // 'USER_NAME_1' is expected, default (empty) view attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "name": "$USER_NAME_1",
            "email": "$EMAIL"
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": [],
            "language": ""
          }
        }""", result)
  }

  @Test
  fun `if user-event attribute is passed by user, then cached value should be skipped`() {
    // 'USER_NAME_1' value cached
    val cachedAttributes = mapOf(
        USER_ATTRIBUTE_KEY to buildUserEventAttributes(USER_NAME_1)
    )
    // 'USER_NAME_2' passed by SDK user
    val userAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, buildUserEventAttributes(USER_NAME_2))
    }

    val result = execute(cachedAttributes, userAttributes)

    // 'USER_NAME_2' is expected, default (empty) view attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "name": "$USER_NAME_2",
            "email": "$EMAIL"
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": [],
            "language": ""
          }
        }""", result)
  }

  @Test
  fun `if user-event attribute is missing 'name' property, then it should be added with empty value`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'name' property is missing
    val userAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("email", EMAIL)
      })
    }

    val result = execute(cachedAttributes, userAttributes)

    // empty 'name' property should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "name": "",
            "email": "$EMAIL"
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": [],
            "language": ""
          }
        }""", result)
  }

  @Test
  fun `if user-event attribute is missing 'email' property, then it should be added with empty value`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'email' property is missing
    val userAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("name", USER_NAME_2)
      })
    }

    val result = execute(cachedAttributes, userAttributes)

    // empty 'email' property should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "name": "$USER_NAME_2",
            "email": ""
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": [],
            "language": ""
          }
        }""", result)
  }

  @Test
  fun `if user-event attribute has some properties outside the schema, then they should be skipped`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // unexpected 'surname' and 'size' properties
    val userAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("name", USER_NAME_2)
        addProperty("surname", "Brown")
        addProperty("email", EMAIL)
        addProperty("size", "33")
      })
    }

    val result = execute(cachedAttributes, userAttributes)

    // only properties from schema should be passed
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "name": "$USER_NAME_2",
            "email": "$EMAIL"
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": [],
            "language": ""
          }
        }""", result)
  }

  /********** view-event attributes **********/

  @Test
  fun `if view-event attribute is cached and no value is passed by user, then cached value should be used`() {
    // 'VIEW_TYPE_1' value cached
    val cachedAttributes = mapOf(
        VIEW_ATTRIBUTE_KEY to buildViewEventAttributes(VIEW_TYPE_1)
    )
    // no value passed by user
    val userAttributes = null

    val result = execute(cachedAttributes, userAttributes)

    // 'VIEW_TYPE_1' is expected, default (empty) user attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
          },
          "user": {
            "name": "",
            "email": ""
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute is passed by user, then cached value should be skipped`() {
    // 'VIEW_TYPE_1' value cached
    val cachedAttributes = mapOf(
        VIEW_ATTRIBUTE_KEY to buildViewEventAttributes(VIEW_TYPE_1)
    )
    // 'VIEW_TYPE_2' passed by SDK user
    val userAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, buildViewEventAttributes(VIEW_TYPE_2))
    }

    val result = execute(cachedAttributes, userAttributes)

    // 'VIEW_TYPE_2' is expected, default (empty) user attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_2",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
          },
          "user": {
            "name": "",
            "email": ""
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute is missing 'subtypes' property, then it should be added with empty value`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'subtypes' property is missing
    val userAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("currency", CURRENCY)
        addProperty("type", VIEW_TYPE_1)
        addProperty("language", LANGUAGE)
      })
    }

    val result = execute(cachedAttributes, userAttributes)

    // empty missing property should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": [],
            "language": "$LANGUAGE"
          },
          "user": {
            "name": "",
            "email": ""
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute is missing some string properties, then they should be added with empty value`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'currency', 'type' and 'language' properties are missing
    val userAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, JsonObject().apply {
        add("subtypes", buildSubtypesArray(SUBTYPES))
      })
    }

    val result = execute(cachedAttributes, userAttributes)

    // empty missing properties should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": $SUBTYPES_STRING,
            "language": ""
          },
          "user": {
            "name": "",
            "email": ""
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute has some properties outside the schema, then they should be skipped`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // unexpected 'animal' and 'fruit' properties
    val userAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("currency", CURRENCY)
        addProperty("type", VIEW_TYPE_1)
        addProperty("animal", "dog")
        add("subtypes", buildSubtypesArray(SUBTYPES))
        addProperty("language", LANGUAGE)
        addProperty("fruit", "watermelon")
      })
    }

    val result = execute(cachedAttributes, userAttributes)

    // only properties from schema should be passed
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
          },
          "user": {
            "name": "",
            "email": ""
          }
        }""", result)
  }

  /********** custom attributes **********/

  @Test
  fun `custom user attributes (non visitor, user-event nor view-event) should be passed as they are`() {
    // 'VIEW_TYPE_1' value cached
    val cachedAttributes = mapOf(
        VIEW_ATTRIBUTE_KEY to buildViewEventAttributes(VIEW_TYPE_1)
    )
    // some custom attributes passed by user
    val userAttributes = JsonObject().apply {
      add("address", JsonObject().apply {
        addProperty("street", "Main")
        addProperty("city", "Brighton")
        addProperty("country", "UK")
      })
      add("dimensions", JsonObject().apply {
        addProperty("height", 18)
        addProperty("width", 12)
      })
    }

    val result = execute(cachedAttributes, userAttributes)

    // custom attributes are expected along with default ones
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "address": {
            "street": "Main",
            "city": "Brighton",
            "country": "UK"
          },
          "dimensions": {
            "height": 18,
            "width": 12
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
          },
          "user": {
            "name": "",
            "email": ""
          }
        }""", result)
  }

  /************* helper methods *************/

  private fun buildUserEventAttributes(
      name: String?,
      email: String? = EMAIL
  ) = JsonObject().apply {
    addProperty("name", name)
    addProperty("email", email)
  }

  private fun buildViewEventAttributes(
      type: String?,
      subtypes: List<String>? = SUBTYPES,
      currency: String? = CURRENCY,
      language: String? = LANGUAGE
  ): JsonObject {
    return JsonObject().apply {
      addProperty("currency", currency)
      addProperty("type", type)
      add("subtypes", buildSubtypesArray(subtypes))
      addProperty("language", language)
    }
  }

  private fun buildSubtypesArray(subtypes: List<String>?) = JsonArray().apply {
    if (subtypes != null) {
      for (subtype in subtypes) {
        add(JsonPrimitive(subtype))
      }
    }
  }

  private fun execute(
      cachedAttributes: Map<String, JsonObject>,
      userAttributes: JsonObject?,
      deviceId: String = DEVICE_ID_1
  ): JsonObject {
    return builder.buildJson(deviceId, userAttributes, cachedAttributes)
  }

  private fun verifyJson(expectedString: String, json: JsonObject) {
    assertEquals(
        expectedString.trimIndent().replace("\n", "").replace(" ", ""),
        json.toString()
    )
  }
}
