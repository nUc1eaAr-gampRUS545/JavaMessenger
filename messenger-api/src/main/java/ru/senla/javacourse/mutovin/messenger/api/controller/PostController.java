package ru.senla.javacourse.mutovin.messenger.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostUpdateRequest;

public interface PostController {
    ResponseEntity<?> createPost(@AuthenticationPrincipal UserDetails userDetails,@RequestBody PostCreateRequest request);

    ResponseEntity<?> getPostById(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long postId);

    ResponseEntity<?> getPostsByUser(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long userId);

    ResponseEntity<?> getAllPosts(@AuthenticationPrincipal UserDetails userDetails);

    ResponseEntity<?> updatePost(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long postId,@RequestBody PostUpdateRequest request);

    ResponseEntity<?> deletePost(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long postId);

    ResponseEntity<?> deleteMyPost(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long postId);
}
