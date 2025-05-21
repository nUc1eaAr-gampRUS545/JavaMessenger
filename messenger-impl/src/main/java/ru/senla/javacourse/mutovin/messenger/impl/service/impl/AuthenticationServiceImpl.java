package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.JwtAuthenticationResponse;
import ru.senla.javacourse.mutovin.messenger.db.entity.Role;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.kafka.KafkaProducer;
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
    private final KafkaProducer kafkaProducer;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Transactional
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User createUser = userService.create(user);
        var jwt = jwtService.generateToken(createUser);
        kafkaProducer.send(request.getEmail(),
                request.getFirstname() + " " + request.getLastname() + " вы успешно зарегистрировались на нашем сервисе.");
        return new JwtAuthenticationResponse(createUser.getId(),jwt);
    }

    @Transactional
    public JwtAuthenticationResponse adminSignUp(SignUpRequest request) {
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_ADMIN);
        User createUser = userService.create(user);
        var jwt = jwtService.generateToken(createUser);
        kafkaProducer.send(request.getEmail(),
                request.getFirstname() + " " + request.getLastname() + " вы успешно зарегистрировались на нашем сервисе.");
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
        User person = userService.findByUsername(request.getUsername());
        var jwt = jwtService.generateToken(user);
        kafkaProducer.send(person.getEmail(),
                person.getFirstname() + " " + person.getLastname() + " вы успешно авторизировались в нашем сервисе.");
        return new JwtAuthenticationResponse(person.getId(),jwt);
    }

}

