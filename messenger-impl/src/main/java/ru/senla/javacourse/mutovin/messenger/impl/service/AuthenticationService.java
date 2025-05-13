package ru.senla.javacourse.mutovin.messenger.impl.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.JwtAuthenticationResponse;


public interface AuthenticationService {

    JwtAuthenticationResponse signUp(SignUpRequest request);

    JwtAuthenticationResponse signIn(SignInRequest request);

    UserDto checkVerifyToken(HttpServletRequest request);
}
