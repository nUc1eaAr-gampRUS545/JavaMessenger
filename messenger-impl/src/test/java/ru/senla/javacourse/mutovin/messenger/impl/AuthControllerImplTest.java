package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.JwtAuthenticationResponse;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.controller.AuthControllerImpl;
import ru.senla.javacourse.mutovin.messenger.impl.service.AuthenticationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerImplTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthControllerImpl authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_shouldReturnCreatedResponse() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("password");

        JwtAuthenticationResponse jwtResponse = JwtAuthenticationResponse.builder()
                .token("mocked-jwt-token")
                .build();

        when(authenticationService.signUp(any())).thenReturn(jwtResponse);

        ResponseEntity<?> result = authController.signUp(request);

        assertEquals(201, result.getStatusCodeValue());

        SuccessResponse body = (SuccessResponse) result.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Пользователь успешно создан", body.getMessage());

        JwtAuthenticationResponse data = (JwtAuthenticationResponse) body.getData();
        assertEquals("mocked-jwt-token", data.getToken());
    }

    @Test
    void adminSignUp_shouldReturnCreatedResponse() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("admin");
        request.setEmail("admin@example.com");
        request.setPassword("adminpass");

        JwtAuthenticationResponse jwtResponse = JwtAuthenticationResponse.builder()
                .token("mocked-admin-jwt-token")
                .build();

        when(authenticationService.adminSignUp(any())).thenReturn(jwtResponse);

        ResponseEntity<?> response = authController.adminSignUp(request);

        assertEquals(201, response.getStatusCodeValue());

        SuccessResponse body = (SuccessResponse) response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Пользователь успешно создан", body.getMessage());

        JwtAuthenticationResponse data = (JwtAuthenticationResponse) body.getData();
        assertEquals("mocked-admin-jwt-token", data.getToken());
    }

    @Test
    void signIn_shouldReturnJwtResponse() {
        SignInRequest request = new SignInRequest();
        request.setUsername("john");
        request.setPassword("password");

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse();
        jwtResponse.setToken("mocked-jwt-token");

        when(authenticationService.signIn(any())).thenReturn(jwtResponse);

        ResponseEntity<?> response = authController.signIn(request);

        assertEquals(201,response.getStatusCodeValue());

        SuccessResponse body = (SuccessResponse) response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Аунтефикация прошла успешно",body.getMessage());

        JwtAuthenticationResponse data = (JwtAuthenticationResponse) body.getData();
        assertEquals("mocked-jwt-token",data.getToken());
    }
}