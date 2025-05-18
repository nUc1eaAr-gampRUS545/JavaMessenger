package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Role;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.UserRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.UserServiceImpl;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ROLE_USER);

        testUserDto = new UserDto();
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
    }

    @Test
    void save_ShouldReturnSavedUser_WhenUserIsValid() {
        when(repository.save(testUser)).thenReturn(Optional.of(testUser));

        User savedUser = userService.save(testUser);

        assertNotNull(savedUser);
        assertEquals(testUser, savedUser);
        verify(repository).save(testUser);
    }

    @Test
    void save_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        when(repository.save(testUser)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.save(testUser));
        verify(repository).save(testUser);
    }

    @Test
    void create_ShouldReturnCreatedUser_WhenUsernameAndEmailAreUnique() {
        when(repository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(repository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(repository.save(testUser)).thenReturn(Optional.of(testUser));

        User createdUser = userService.create(testUser);

        assertNotNull(createdUser);
        assertEquals(testUser, createdUser);
        verify(repository).existsByUsername(testUser.getUsername());
        verify(repository).existsByEmail(testUser.getEmail());
        verify(repository).save(testUser);
    }

    @Test
    void create_ShouldThrowRuntimeException_WhenUsernameExists() {
        when(repository.existsByUsername(testUser.getUsername())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.create(testUser));

        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(repository).existsByUsername(testUser.getUsername());
        verify(repository, never()).existsByEmail(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    void create_ShouldThrowRuntimeException_WhenEmailExists() {
        when(repository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(repository.existsByEmail(testUser.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.create(testUser));

        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        verify(repository).existsByUsername(testUser.getUsername());
        verify(repository).existsByEmail(testUser.getEmail());
        verify(repository, never()).save(any());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        String username = "testuser";
        when(repository.findByUsername(username)).thenReturn(Optional.of(testUser));

        User foundUser = userService.findByUsername(username);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
        verify(repository).findByUsername(username);
    }

    @Test
    void findByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        String username = "nonexistent";
        when(repository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.findByUsername(username));

        assertEquals("Пользователь с username = " + username + " не найден!", exception.getMessage());
        verify(repository).findByUsername(username);
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        User foundUser = userService.findById(userId);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
        verify(repository).findById(userId);
    }

    @Test
    void findById_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        Long userId = 99L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.findById(userId));

        assertEquals("Пользователь с id = " + userId + " не найден!", exception.getMessage());
        verify(repository).findById(userId);
    }

    @Test
    void findAll_ShouldReturnListOfUserDtos_WhenUsersExist() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(testUserDto);

        when(repository.findAll()).thenReturn(Optional.of(users));
        when(userMapper.map(testUser)).thenReturn(testUserDto);

        List<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDto, result.get(0));
        verify(repository).findAll();
        verify(userMapper).map(testUser);
    }

    @Test
    void findAll_ShouldThrowUsernameNotFoundException_WhenNoUsersExist() {
        when(repository.findAll()).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.findAll());

        assertEquals("Пользователи не найдены!", exception.getMessage());
        verify(repository).findAll();
        verify(userMapper, never()).map(any());
    }

    @Test
    void userDetailsService_ShouldReturnUserDetailsService() {
        assertNotNull(userService.userDetailsService());
    }

}
