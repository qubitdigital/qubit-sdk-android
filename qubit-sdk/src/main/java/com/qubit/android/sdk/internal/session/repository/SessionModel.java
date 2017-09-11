package com.qubit.android.sdk.internal.session.repository;

public class SessionModel {

  private Long id;

  public SessionModel() {
  }

  public SessionModel(Long id) {
    this.id = id;
  }


  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
