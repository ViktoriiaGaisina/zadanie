package com.otp.zadanie.web;

import com.otp.zadanie.dto.CreateUserRequest;
import com.otp.zadanie.dto.UpdateUserRequest;
import com.otp.zadanie.dto.UserResponse;
import com.otp.zadanie.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createNewUser")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/users")
    public UserResponse get(@RequestParam("userID") @NotNull UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/userDetailsUpdate")
    public UserResponse update(@Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam("userID") @NotNull UUID userId) {
        userService.deleteUser(userId);
    }
}
