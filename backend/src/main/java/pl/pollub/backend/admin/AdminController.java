package pl.pollub.backend.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<AdminDto>> getUsers() {
        List<AdminDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/username")
    public ResponseEntity<String> updateUsername(@PathVariable Long userId, @RequestBody UserUsernameEditDto usernameEditDto) {
        String message = adminService.updateUserUsername(userId, usernameEditDto);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{userId}/email")
    public ResponseEntity<String> updateEmail(@PathVariable Long userId, @RequestBody UserEmailEditDto emailEditDto) {
        String message = adminService.updateUserEmail(userId, emailEditDto);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateRole(@PathVariable Long userId, @RequestBody UserRoleDto roleDto) {
        String message = adminService.updateUserRole(userId, roleDto);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{userId}/limit")
    public ResponseEntity<String> updateLimit(@PathVariable Long userId, @RequestBody UserLimitDto limitDto) {
        String message = adminService.updateUserLimit(userId, limitDto);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        String message = adminService.deleteUser(userId);
        return ResponseEntity.ok(message);
    }
}

