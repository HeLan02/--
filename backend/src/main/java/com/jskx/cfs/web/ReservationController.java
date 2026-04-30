package com.jskx.cfs.web;

import com.jskx.cfs.service.ReservationService;
import com.jskx.cfs.web.dto.ReservationCreateRequest;
import com.jskx.cfs.web.dto.ReservationDto;
import com.jskx.cfs.web.dto.ReservationRejectRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReservationController {
  private final ReservationService service;

  public ReservationController(ReservationService service) {
    this.service = service;
  }

  @PostMapping("/reservations")
  public ReservationDto create(
      HttpServletRequest httpReq,
      @Valid @RequestBody ReservationCreateRequest req,
      @RequestParam(required = false) Integer week
  ) {
    String userId = firstNonBlank(req.userId(), httpReq.getHeader("X-User-Id"), httpReq.getParameter("userId"));
    String userName = firstNonBlank(req.userName(), httpReq.getHeader("X-User-Name"), httpReq.getParameter("userName"));
    if (isBlank(userId) || isBlank(userName)) {
      throw new IllegalArgumentException("userId/userName are required (request body preferred; or X-User-Id/X-User-Name headers; or userId/userName params)");
    }
    return ReservationDto.from(service.create(userId, userName, req, week));
  }

  @GetMapping("/reservations/my")
  public List<ReservationDto> my(
      HttpServletRequest httpReq,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) Integer status
  ) {
    String uid = firstNonBlank(userId, httpReq.getHeader("X-User-Id"));
    return service.myReservations(uid, status).stream().map(ReservationDto::from).toList();
  }

  @PutMapping("/reservations/{id}/cancel")
  public ReservationDto cancel(
      HttpServletRequest httpReq,
      @PathVariable Long id,
      @RequestParam(required = false) String userId
  ) {
    String uid = firstNonBlank(userId, httpReq.getHeader("X-User-Id"));
    if (isBlank(uid)) {
      throw new IllegalArgumentException("userId or X-User-Id is required");
    }
    return ReservationDto.from(service.cancel(id, uid));
  }

  @GetMapping("/reservations/pending")
  public List<ReservationDto> pending() {
    return service.pending().stream().map(ReservationDto::from).toList();
  }

  @PutMapping("/reservations/{id}/approve")
  public ReservationDto approve(@PathVariable Long id, Authentication admin, @RequestParam(required = false) Integer week) {
    return ReservationDto.from(service.approve(id, admin, week));
  }

  @PutMapping("/reservations/{id}/reject")
  public ReservationDto reject(@PathVariable Long id, Authentication admin, @Valid @RequestBody ReservationRejectRequest req) {
    return ReservationDto.from(service.reject(id, admin, req.comment()));
  }

  private static String firstNonBlank(String a, String b) {
    return !isBlank(a) ? a : b;
  }

  private static String firstNonBlank(String a, String b, String c) {
    return !isBlank(a) ? a : (!isBlank(b) ? b : c);
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}

