package ec.edu.monster.banquito.controller;

import ec.edu.monster.banquito.dto.AuthCredentialsDto;
import ec.edu.monster.banquito.dto.AuthResponseDto;
import ec.edu.monster.banquito.dto.UserDto;
import ec.edu.monster.banquito.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthCredentialsDto credentials) {
        return ResponseEntity.ok(authService.login(credentials));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Validated({ UserDto.Create.class }) UserDto userDto) {
        return ResponseEntity.ok(authService.register(userDto));
    }
}
