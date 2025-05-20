package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;

import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.JwtAuthenticationResponse;
import ru.senla.javacourse.mutovin.messenger.db.entity.Gender;
import ru.senla.javacourse.mutovin.messenger.db.entity.Role;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;
import ru.senla.javacourse.mutovin.messenger.impl.service.JwtService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.AuthenticationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void signUp_ShouldReturnJwtResponse_WhenValidRequest() {
        // Arrange
        SignUpRequest request = new SignUpRequest();
        request.setFirstname("Johnnnj");
        request.setLastname("Doennkki");
        request.setPhonenumber("1234567890");
        request.setUsername("johndoe");
        request.setGender(String.valueOf(Gender.MALE));
        request.setEmail("john@example.com");
        request.setPassword("password");

        User mockUser = User.builder()
                .id(1L)
                .firstname("Johnnj")
                .lastname("Doemjknj")
                .phoneNumber("1234567890")
                .age(20)
                .gender(Gender.MALE)
                .username("johndoe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("mockToken");

        JwtAuthenticationResponse response = authenticationService.signUp(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("mockToken", response.getToken());

        verify(passwordEncoder).encode("password");
        verify(userService).create(any(User.class));
        verify(jwtService).generateToken(mockUser);
    }

    @Test
    void signIn_ShouldReturnJwtResponse_WhenValidCredentials() {
        SignInRequest request = new SignInRequest();
        request.setUsername("johndoe");
        request.setPassword("password");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("johndoe");

        UserDetails userDetails = mock(UserDetails.class);

        when(userService.findByUsername("johndoe")).thenReturn(mockUser);
        when(userService.userDetailsService().loadUserByUsername("johndoe")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mockToken");

        JwtAuthenticationResponse response = authenticationService.signIn(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("mockToken", response.getToken());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("johndoe", "password")
        );
        verify(userService).findByUsername("johndoe");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void signIn_ShouldThrowException_WhenInvalidCredentials() {
        SignInRequest request = new SignInRequest();
        request.setUsername("johndoe");
        request.setPassword("wrongpassword");

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(
                        new UsernamePasswordAuthenticationToken("johndoe", "wrongpassword")
                );

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.signIn(request);
        });

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("johndoe", "wrongpassword")
        );
        verifyNoInteractions(jwtService);
    }

}
