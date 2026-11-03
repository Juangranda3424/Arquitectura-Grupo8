package ec.edu.monster.banquito.service;

import ec.edu.monster.banquito.dto.AuthCredentialsDto;
import ec.edu.monster.banquito.dto.AuthResponseDto;
import ec.edu.monster.banquito.dto.UserDto;

public interface AuthService {
    UserDto getCurrentUser();
    AuthResponseDto login(AuthCredentialsDto credentials);
    UserDto register(UserDto user);
}
