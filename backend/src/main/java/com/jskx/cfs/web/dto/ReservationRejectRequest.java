package com.jskx.cfs.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservationRejectRequest(@NotBlank String comment) {}

