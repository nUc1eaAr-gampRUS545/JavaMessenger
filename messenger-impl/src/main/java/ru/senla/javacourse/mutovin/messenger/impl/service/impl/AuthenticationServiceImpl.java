package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.JwtAuthenticationResponse;
import ru.senla.javacourse.mutovin.messenger.db.entity.Role;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;
import ru.senla.javacourse.mutovin.messenger.impl.service.AuthenticationService;
import ru.senla.javacourse.mutovin.messenger.impl.service.JwtService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Transactional
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user = request.toEntity();
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User createUser = userService.create(user);

        var jwt = jwtService.generateToken(createUser);
        return new JwtAuthenticationResponse(createUser.getId(),jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Transactional
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());
        Long id = userService.findByUsername(request.getUsername()).getId();
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(id,jwt);
    }
    /**
     * Проверка токена
     *
     * @param request токен
     * @return данные пользователя
     */
    @Transactional
    public UserDto checkVerifyToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid or missing Authorization header");
        }

        final String jwt = authHeader.substring(7);

        String username = jwtService.extractUserName(jwt);
        if (username == null) {
            throw new BadCredentialsException("Invalid token");
        }

        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);

        if (!jwtService.isTokenValid(jwt, userDetails)) {
            throw new BadCredentialsException("Invalid token");
        }
        return userMapper.map(userService.findByUsername(username));
    }
}

