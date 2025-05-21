package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.UserUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.*;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ResourceNotFoundException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.PostMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.FriendshipRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.PostRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.UserRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FriendshipRepository friendshipRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public List<UserDto> filterUsers(String firstName,String lastName,Integer age,String gender) {
        List<User> result = userRepository.filterUsers(firstName,lastName,age,gender)
                .orElse(Collections.emptyList());

        return result.stream().map(userMapper::map).toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "UserService::findByUsername", key = "#user.username", condition = "#user.username != null"),
            @CacheEvict(value = "UserService::findByEmail", key = "#user.email", condition = "#user.email != null")
    }, put = {
            @CachePut(value = "UserService::findId", key = "#user.id")
    })
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
    @Caching(put = {
            @CachePut(value = "UserService::findId",key = "#userId")
    })
    public UserDto updateUserProfile(Long userId,UserUpdateRequest request) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new UsernameNotFoundException("Пользователь с id = " + userId + " не найден!"));

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setAge(request.getAge());
        user.setGender(Gender.valueOf(request.getGender()));
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhonenumber());
        User result = userRepository.save(user).orElseThrow(
                () -> new RuntimeException("Не удалось обновить профиль"));

        return userMapper.map(result);

    }

    @Override
    @Transactional
    @Cacheable(value = "UserService::findById", key = "#username")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username = " + username + " не найден!"));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::findByUsername;
    }

    @Override
    @Cacheable(value = "UserService::findById", key = "#id")
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
    public UserDto addFriend(Long userId,Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (friendshipRepository.findByUserIdAndFriendId(userId,friendId).isPresent()) {

            throw new IllegalStateException("Дружба уже существует");
        }

        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setCreatedAt(LocalDateTime.now());

        friendshipRepository.save(friendship).orElseThrow(
                () -> new IllegalStateException("Не удалось сохранить дружбу")
        );
        return findByIdWithFriendsAndPosts(userId);
    }

    @Override
    @Transactional
    public void removeFriend(Long userId,Long friendId) {
        friendshipRepository.deleteByUserIdAndFriendId(userId,friendId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByIdWithFriendsAndPosts(Long id) {
        UserDto userDto = userMapper.map(findById(id));

        List<UserDto> friends = friendshipRepository.findByUserId(id)
                .stream()
                .map(Friendship::getFriend)
                .map(userMapper::map)
                .toList();
        List<Post> posts = postRepository.findByCreatorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Посты не найдены"));
        List<PostDto> postDtos = posts.stream().map(postMapper::map).toList();

        userDto.setPosts(postDtos);
        userDto.setFriends(friends);
        return userDto;
    }


}
