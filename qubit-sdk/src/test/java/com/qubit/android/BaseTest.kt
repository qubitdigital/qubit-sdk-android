package com.qubit.android

import org.junit.After
import org.junit.Before
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

inline fun <reified T : Any> argumentCaptor() = ArgumentCaptor.forClass(T::class.java)

abstract class BaseTest {

  @Before
  open fun setup() {
    MockitoAnnotations.initMocks(this)
  }

  @After
  open fun tearDown() = Unit

  protected fun <T> any(): T? {
    Mockito.any<T>()
    return uninitialized()
  }

  protected fun <T> any(c: Class<T>): T {
    Mockito.any<T>(c)
    return uninitialized()
  }

  private fun <T> uninitialized(): T = null as T
}