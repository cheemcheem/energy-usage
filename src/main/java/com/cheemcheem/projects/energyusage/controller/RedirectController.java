package com.cheemcheem.projects.energyusage.controller;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
public class RedirectController {

  @GetMapping
  public ResponseEntity<Object> redirectToRoot() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
  }
}
