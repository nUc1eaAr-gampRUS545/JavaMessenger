package ru.senla.javacourse.mutovin.messenger.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;

public interface FriendRequestController {
    ResponseEntity<?> sendRequest(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long receiverId);

    ResponseEntity<?> acceptRequest(@PathVariable Long requestId);

    ResponseEntity<?> rejectRequest(@PathVariable Long requestId);

    ResponseEntity<?> getPendingRequests(@AuthenticationPrincipal UserDetails userDetails);

    ResponseEntity<?> getSentRequests(@AuthenticationPrincipal UserDetails userDetails);

    ResponseEntity<?> cancelRequest(@PathVariable Long requestId);
}
