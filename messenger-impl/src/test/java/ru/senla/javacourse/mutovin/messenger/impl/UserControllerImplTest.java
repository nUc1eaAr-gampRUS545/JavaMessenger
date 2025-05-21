package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.UserUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.controller.UserControllerImpl;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerImplTest {

    @InjectMocks
    private UserControllerImpl userController;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn("username");
    }

    @Test
    void testGetUserById() {
        UserDto userDto = UserDto.builder().build();
        when(userService.findByIdWithFriendsAndPosts(5L)).thenReturn(userDto);

        ResponseEntity<?> response = userController.getUserById(5L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDto, response.getBody());
        verify(userService).findByIdWithFriendsAndPosts(5L);
    }

    @Test
    void testFilterUsers() {
        List<UserDto> users = List.of(UserDto.builder().build());
        when(userService.filterUsers("John", "Doe", 30, "M")).thenReturn(users);

        ResponseEntity<?> response = userController.filterUsers("John", "Doe", 30, "M");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(users, response.getBody());
        verify(userService).filterUsers("John", "Doe", 30, "M");
    }

    @Test
    void testGetProfile() {
        UserDto userDto = UserDto.builder().build();;
        when(userService.findByUsername("username")).thenReturn(
                org.mockito.Mockito.mock(ru.senla.javacourse.mutovin.messenger.db.entity.User.class, invocation -> {
                    if ("getId".equals(invocation.getMethod().getName())) {
                        return 7L;
                    }
                    return invocation.callRealMethod();
                }));
        when(userService.findByIdWithFriendsAndPosts(7L)).thenReturn(userDto);

        when(userService.findByUsername("username").getId()).thenReturn(7L);

        ResponseEntity<?> response = userController.getProfile(userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDto, response.getBody());
        verify(userService).findByUsername("username");
        verify(userService).findByIdWithFriendsAndPosts(7L);
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> users = List.of(UserDto.builder().build(), UserDto.builder().build());
        when(userService.findAll()).thenReturn(users);

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(users, response.getBody());
        verify(userService).findAll();
    }

    @Test
    void testRemoveFriend() {
        when(userService.findByUsername("username")).thenReturn(
                org.mockito.Mockito.mock(ru.senla.javacourse.mutovin.messenger.db.entity.User.class, invocation -> {
                    if ("getId".equals(invocation.getMethod().getName())) {
                        return 3L;
                    }
                    return invocation.callRealMethod();
                }));
        when(userService.findByUsername("username").getId()).thenReturn(3L);

        ResponseEntity<?> response = userController.removeFriend(userDetails, 8L);

        assertEquals(200, response.getStatusCodeValue());
        SuccessResponse body = (SuccessResponse) response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Друг успешно удален", body.getMessage());

        verify(userService).removeFriend(3L, 8L);
    }

    @Test
    void testGetAdmin() {
        ResponseEntity<?> response = userController.getAdmin();

        assertEquals(200, response.getStatusCodeValue());
        SuccessResponse body = (SuccessResponse) response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Права администратора получены", body.getMessage());

        verify(userService).getAdmin();
    }

    @Test
    void testUpdateMyProfile() {
        UserUpdateRequest request = new UserUpdateRequest();
        UserDto updatedUser = UserDto.builder().build();

        when(userService.findByUsername("username")).thenReturn(
                org.mockito.Mockito.mock(ru.senla.javacourse.mutovin.messenger.db.entity.User.class, invocation -> {
                    if ("getId".equals(invocation.getMethod().getName())) {
                        return 9L;
                    }
                    return invocation.callRealMethod();
                }));
        when(userService.findByUsername("username").getId()).thenReturn(9L);
        when(userService.updateUserProfile(9L, request)).thenReturn(updatedUser);

        ResponseEntity<?> response = userController.updateMyProfile(userDetails, request);

        assertEquals(200, response.getStatusCodeValue());
        SuccessResponse body = (SuccessResponse) response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Профиль успешно обновлен", body.getMessage());
        assertEquals(updatedUser, body.getData());

        verify(userService).updateUserProfile(9L, request);
    }
}

