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
    private const val USER_ID_1 = "user_id_1"
    private const val USER_ID_2 = "user_id_2"
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
    val customAttributes = null

    // 'DEVICE_ID_2' set by SDK
    val result = execute(cachedAttributes, customAttributes, DEVICE_ID_2)

    // 'DEVICE_ID_2' is expected, default (empty) user&view attributes
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_2"
          },
          "user": {
            "id": "",
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
    val customAttributes = JsonObject().apply {
      add(VISITOR_ATTRIBUTE_KEY, builder.buildVisitorAttributesJson(DEVICE_ID_1))
    }

    // 'DEVICE_ID_2' set by SDK
    val result = execute(cachedAttributes, customAttributes, DEVICE_ID_2)

    // 'DEVICE_ID_2' is expected, default (empty) user&view attributes
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_2"
          },
          "user": {
            "id": "",
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
    // 'USER_ID_1' value cached
    val cachedAttributes = mapOf(
        USER_ATTRIBUTE_KEY to buildUserEventAttributes(USER_ID_1)
    )
    // no value passed by user
    val customAttributes = null

    val result = execute(cachedAttributes, customAttributes)

    // 'USER_ID_1' is expected, default (empty) view attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "$USER_ID_1",
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
    // 'USER_ID_1' value cached
    val cachedAttributes = mapOf(
        USER_ATTRIBUTE_KEY to buildUserEventAttributes(USER_ID_1)
    )
    // 'USER_ID_2' passed by SDK user
    val customAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, buildUserEventAttributes(USER_ID_2))
    }

    val result = execute(cachedAttributes, customAttributes)

    // 'USER_ID_2' is expected, default (empty) view attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "$USER_ID_2",
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
  fun `if user-event attribute is missing 'id' property, then it should be added with empty value`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'id' property is missing
    val customAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("email", EMAIL)
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // empty 'id' property should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
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
    val customAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("id", USER_ID_2)
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // empty 'email' property should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "$USER_ID_2",
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
    val customAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("id", USER_ID_2)
        addProperty("surname", "Brown")
        addProperty("email", EMAIL)
        addProperty("size", "33")
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // only properties from schema should be passed
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "$USER_ID_2",
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
  fun `if user-event attribute isn't JSON, then it doesn't follow the schema and should be skipped`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // unexpected non-JSON 'user' attribute
    val customAttributes = JsonObject().apply {
      addProperty(USER_ATTRIBUTE_KEY, "some_value")
    }

    val result = execute(cachedAttributes, customAttributes)

    // expected default (empty) user attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
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
  fun `if user-event attribute has some properties of unexpected type, then they should be skipped and default values used`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // user attribute properties are not string primitives as expected
    val customAttributes = JsonObject().apply {
      add(USER_ATTRIBUTE_KEY, JsonObject().apply {
        add("id", JsonArray())
        addProperty("email", 3)
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // default (empty) properties values should be used
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
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

  /********** view-event attributes **********/

  @Test
  fun `if view-event attribute is cached and no value is passed by user, then cached value should be used`() {
    // 'VIEW_TYPE_1' value cached
    val cachedAttributes = mapOf(
        VIEW_ATTRIBUTE_KEY to buildViewEventAttributes(VIEW_TYPE_1)
    )
    // no value passed by user
    val customAttributes = null

    val result = execute(cachedAttributes, customAttributes)

    // 'VIEW_TYPE_1' is expected, default (empty) user attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
            "email": ""
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
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
    val customAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, buildViewEventAttributes(VIEW_TYPE_2))
    }

    val result = execute(cachedAttributes, customAttributes)

    // 'VIEW_TYPE_2' is expected, default (empty) user attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
            "email": ""
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_2",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute is missing 'subtypes' property, then it should be added with empty value`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'subtypes' property is missing
    val customAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("currency", CURRENCY)
        addProperty("type", VIEW_TYPE_1)
        addProperty("language", LANGUAGE)
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // empty missing property should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
            "email": ""
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": [],
            "language": "$LANGUAGE"
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute is missing some string properties, then they should be added with empty value`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // 'currency', 'type' and 'language' properties are missing
    val customAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, JsonObject().apply {
        add("subtypes", buildSubtypesArray(SUBTYPES))
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // empty missing properties should be added
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
            "email": ""
          },
          "view": {
            "currency": "",
            "type": "",
            "subtypes": $SUBTYPES_STRING,
            "language": ""
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute has some properties outside the schema, then they should be skipped`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // unexpected 'animal' and 'fruit' properties
    val customAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, JsonObject().apply {
        addProperty("currency", CURRENCY)
        addProperty("type", VIEW_TYPE_1)
        addProperty("animal", "dog")
        add("subtypes", buildSubtypesArray(SUBTYPES))
        addProperty("language", LANGUAGE)
        addProperty("fruit", "watermelon")
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // only properties from schema should be passed
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
            "email": ""
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
          }
        }""", result)
  }

  @Test
  fun `if view-event attribute isn't JSON, then it doesn't follow the schema and should be skipped`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // unexpected non-JSON 'view' attribute
    val customAttributes = JsonObject().apply {
      addProperty(VIEW_ATTRIBUTE_KEY, "some_value")
    }

    val result = execute(cachedAttributes, customAttributes)

    // expected default (empty) view attribute
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
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
  fun `if view-event attribute has some properties of unexpected type, then they should be skipped and default values used`() {
    val cachedAttributes = emptyMap<String, JsonObject>()
    // view attribute properties are not string primitives/JSON array as expected
    val customAttributes = JsonObject().apply {
      add(VIEW_ATTRIBUTE_KEY, JsonObject().apply {
        add("currency", JsonObject().apply {
          addProperty("color", "yellow")
        })
        add("type", null)
        addProperty("subtypes", "should be JSON array")
        addProperty("language", false)
      })
    }

    val result = execute(cachedAttributes, customAttributes)

    // default (empty) properties values should be used if type is incorrect
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
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

  /********** custom attributes **********/

  @Test
  fun `custom user attributes (non visitor, user-event nor view-event) should be passed as they are`() {
    // 'VIEW_TYPE_1' value cached
    val cachedAttributes = mapOf(
        VIEW_ATTRIBUTE_KEY to buildViewEventAttributes(VIEW_TYPE_1)
    )
    // some custom attributes passed by user
    val customAttributes = JsonObject().apply {
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

    val result = execute(cachedAttributes, customAttributes)

    // custom attributes are expected along with default ones
    verifyJson("""
        {
          "visitor": {
            "id": "$DEVICE_ID_1"
          },
          "user": {
            "id": "",
            "email": ""
          },
          "view": {
            "currency": "$CURRENCY",
            "type": "$VIEW_TYPE_1",
            "subtypes": $SUBTYPES_STRING,
            "language": "$LANGUAGE"
          },
          "address": {
            "street": "Main",
            "city": "Brighton",
            "country": "UK"
          },
          "dimensions": {
            "height": 18,
            "width": 12
          }
        }""", result)
  }

  /************* helper methods *************/

  private fun buildUserEventAttributes(
      id: String?,
      email: String? = EMAIL
  ) = JsonObject().apply {
    addProperty("id", id)
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
      customAttributes: JsonObject?,
      deviceId: String = DEVICE_ID_1
  ): JsonObject {
    return builder.buildJson(deviceId, customAttributes, cachedAttributes)
  }

  private fun verifyJson(expectedString: String, json: JsonObject) {
    assertEquals(
        expectedString.trimIndent().replace("\n", "").replace(" ", ""),
        json.toString()
    )
  }
}
