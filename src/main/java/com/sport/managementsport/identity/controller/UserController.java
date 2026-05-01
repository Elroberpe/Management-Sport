package com.sport.managementsport.identity.controller;

import com.sport.managementsport.identity.dto.CreateUserRequest;
import com.sport.managementsport.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
