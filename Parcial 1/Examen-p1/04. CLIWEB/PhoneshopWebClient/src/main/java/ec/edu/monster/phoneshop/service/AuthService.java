package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.dto.AuthResponseDto;
import ec.edu.monster.phoneshop.dto.LoginRequestDto;
import ec.edu.monster.phoneshop.dto.UserDto;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto credentials);
    void logout();
    UserDto getCurrentUser();
}
