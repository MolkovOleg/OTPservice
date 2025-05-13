package com.molkov.otpservice.service;


import com.molkov.otpservice.dto.UserDto;
import com.molkov.otpservice.model.user.User;
import com.molkov.otpservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public String register(UserDto userDto) {
        User user = userService.create(userDto);
        log.info("User created: {}", user);
        return jwtTokenProvider.generateToken(user);
    }

    public String login(UserDto userDto) {
        User user = userService.findByUsername(userDto.username());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.username(),
                        userDto.password()
                )
        );

        log.info("Authentication successful: {}", user.getUsername());
        return jwtTokenProvider.generateToken(user);
    }

}
