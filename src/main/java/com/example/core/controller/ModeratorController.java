package com.example.core.controller;

import com.example.core.dto.UserDto;
import com.example.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    @Autowired
    private UserService service;

    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> blockUser(@PathVariable Integer id) {
        try {
            service.blockedUser(id);
            return ResponseEntity.ok(String.format("User с %s - заблокирован", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при блокировке пользователя: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> unblockUser(@PathVariable Integer id) {
        try {
            service.unblockedUser(id);
            return ResponseEntity.ok(String.format("User с %s - разблокирован", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при разблокировке пользователя: " + e.getMessage());
        }
    }
}
