package ru.senla.javacourse.mutovin.messenger.impl.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.UserUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Friendship;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.db.entity.Gender;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    List<UserDto> filterUsers(String firstName,String lastName,Integer age,String gender);
    User save(User user);
    User create(User user);

    @Transactional
    UserDto updateUserProfile(Long userId,UserUpdateRequest request);

    User findByUsername(String username);
    UserDetailsService userDetailsService();
    User findById(Long id);
    List<UserDto> findAll();
    UserDto findByIdWithFriendsAndPosts(Long id);
    UserDto addFriend(Long userId,Long friendId);
    void removeFriend(Long userId, Long friendId);
    void getAdmin();
}
