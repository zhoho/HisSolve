package com.example.newhisolve.auth.Controller;

import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.UserService;
import com.example.newhisolve.auth.Service.AuthService;
import com.example.newhisolve.auth.Service.HisnetLoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final HisnetLoginService hisnetLoginService;
    private final UserService userService;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String createMemberForm(@RequestParam String token, Model model, HttpSession session) {
        System.out.println("Received token test: " + token);
        if (token == null || token.isEmpty()) {
            System.out.println("토큰이 없습니다.");
            return "redirect:/error";
        }

        try {
            User user = hisnetLoginService.callHisnetLoginApi(token);
            model.addAttribute("user", user);
            session.setAttribute("user", user);
            authService.createOrUpdateUser(user);

            System.out.println("tt" + user.getUniqueId());

            try {
                UserDetails userDetails = userService.loadUserByuniqueId(user.getUniqueId());
                System.out.println("user detail check " + userDetails);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                // 세션에 인증 정보 설정
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                logger.info("UserDetails: {}", userDetails);
                logger.info("Authentication: {}", auth);
            } catch (Exception e) {
                logger.error("Error loading user details: ", e);
                return "redirect:/error";
            }

            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            System.out.println("로그인 실패: " + e.getMessage());
            return "redirect:/error";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("로그아웃");
        session.invalidate();
        return "redirect:/";
    }
}
