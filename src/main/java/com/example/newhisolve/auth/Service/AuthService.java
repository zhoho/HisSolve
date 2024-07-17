package com.example.newhisolve.auth.Service;

import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public void createOrUpdateUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElse(null);

        if (existingUser == null) {
            userRepository.save(user);
        } else {
            existingUser.update(user);
            userRepository.save(existingUser);
        }
    }
}
