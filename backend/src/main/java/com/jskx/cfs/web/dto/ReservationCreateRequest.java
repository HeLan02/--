package com.jskx.cfs.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationCreateRequest(
    String userId,
    String userName,
    @NotNull Long classroomId,
    @NotBlank String purpose,
    @NotNull LocalDate reservationDate,
    @NotNull @Min(1) @Max(30) Integer startPeriod,
    @NotNull @Min(1) @Max(30) Integer endPeriod,
    @NotNull @Min(1) Integer attendeeCount,
    @NotBlank String contact
) {}

