package ru.senla.javacourse.mutovin.messenger.db.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "subscribes")
public class Subscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
