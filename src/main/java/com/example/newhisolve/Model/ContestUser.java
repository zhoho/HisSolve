package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "contest_user")
public class ContestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contest와의 ManyToOne 관계
    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    // User와의 ManyToOne 관계
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 추가된 end_time 필드
    private LocalDateTime endTime;
}
