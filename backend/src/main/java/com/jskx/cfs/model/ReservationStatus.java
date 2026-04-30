package com.jskx.cfs.model;

public enum ReservationStatus {
  PENDING(0),
  APPROVED(1),
  REJECTED(2),
  CANCELLED(3);

  private final int code;

  ReservationStatus(int code) {
    this.code = code;
  }

  public int code() {
    return code;
  }

  public static ReservationStatus fromCode(int code) {
    for (ReservationStatus s : values()) {
      if (s.code == code) {
        return s;
      }
    }
    throw new IllegalArgumentException("Unknown reservation status: " + code);
  }
}

