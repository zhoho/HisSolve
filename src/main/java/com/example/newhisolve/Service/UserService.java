package com.example.newhisolve.Service;

import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

public interface UserService {
    User register(User user);
    User findByUsername(String username);
    UserDetails loadUserByuniqueId(String uniqueId) throws UsernameNotFoundException;
    void updateUser(User user);
    User findByUniqueId(String uniqueId);
    User getCurrentUser();
}

