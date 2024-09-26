package com.example.newhisolve.Service;

import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 현재 로그인된 사용자 가져오기
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    // 새로운 사용자를 등록
    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ADMIN");
        return userRepository.save(user);
    }

    // 사용자명으로 사용자 찾기
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // 로그인 시 사용자 정보 로드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())))
                .build();
    }

    // uniqueId로 사용자 찾기
    public UserDetails loadUserByuniqueId(String uniqueId) throws UsernameNotFoundException {
        User user = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + uniqueId));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())))
                .build();
    }

    // 사용자 정보 업데이트
    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    // 사용자 활동 상태를 업데이트하는 메서드
    public void updateUserActiveStatus(String username, boolean isActive) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        user.setActive(isActive);  // active 필드를 업데이트
        userRepository.save(user); // 변경사항을 DB에 저장
    }

}
