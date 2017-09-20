package com.qubit.android.sdk.internal.eventtracker.connector;

import java.math.BigDecimal;

public class LifetimeValue {

  private BigDecimal value;
  private String currency;

  public LifetimeValue(BigDecimal value, String currency) {
    this.value = value;
    this.currency = currency;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
