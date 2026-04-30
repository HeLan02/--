package com.jskx.cfs.service;

public final class TimeOverlap {
  private TimeOverlap() {}

  public static boolean overlaps(int qStart, int qEnd, int cStart, int cEnd) {
    if (qStart > qEnd) {
      throw new IllegalArgumentException("startPeriod must be <= endPeriod");
    }
    return qStart <= cEnd && qEnd >= cStart;
  }
}

