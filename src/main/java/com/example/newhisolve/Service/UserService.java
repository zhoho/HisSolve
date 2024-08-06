package com.example.newhisolve.Service;
import com.example.newhisolve.Model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface UserService {
    User register(User user);
    User findByUsername(String username);
    UserDetails loadUserByuniqueId(String uniqueId) throws UsernameNotFoundException;
    void updateUser(User user);
    User getCurrentUser();
}

