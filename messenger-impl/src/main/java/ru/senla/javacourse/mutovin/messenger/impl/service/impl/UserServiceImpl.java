package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Friendship;
import ru.senla.javacourse.mutovin.messenger.db.entity.Role;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.FriendshipRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.UserRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        return userRepository.save(user).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username = " + username + " не найден!"));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::findByUsername;
    }

    @Override
    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с id = " + id + " не найден!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userRepository.findAll()
                .orElseThrow(() -> new UsernameNotFoundException("Пользователи не найдены!"));
        users.forEach(user -> {
            UserDto userDto = userMapper.map(user);
            usersDto.add(userDto);
        });
        return usersDto;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }

    @Override
    @Transactional
    public UserDto addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (friendshipRepository.findByUserIdAndFriendId(userId, friendId).isPresent()) {
            throw new IllegalStateException("Дружба уже существует");
        }

        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setCreatedAt(LocalDateTime.now());

        friendshipRepository.save(friendship).orElseThrow(
                () -> new IllegalStateException("Не удалось сохранить дружбу")
        );
        return findByIdWithFriends(userId);
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        friendshipRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByIdWithFriends(Long id) {
        UserDto userDto = userMapper.map(findById(id));

        List<UserDto> friends = friendshipRepository.findByUserId(id)
                .stream()
                .map(Friendship::getFriend)
                .map(userMapper::map)
                .collect(Collectors.toList());

        userDto.setFriends(friends);
        return userDto;
    }
}
