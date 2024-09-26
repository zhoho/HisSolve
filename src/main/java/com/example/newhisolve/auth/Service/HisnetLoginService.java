package com.example.newhisolve.auth.Service;

import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.UserService;  // UserService를 사용하여 활동 상태 업데이트
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class HisnetLoginService {

    @Value("${hisnet.access-key}")
    private String accessKey;

    private final UserService userService;  // UserService 의존성 추가

    @Autowired
    public HisnetLoginService(UserService userService) {
        this.userService = userService;
    }

    public User callHisnetLoginApi(String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("token", token);
        requestBody.put("accessKey", accessKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://walab.info:8443/HisnetLogin/api/hisnet/login/validate";
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

        try {
            ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};
            ResponseEntity<Map<String, Object>> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, typeRef);
            Map<String, Object> result = resultMap.getBody();
            if (result == null) {
                throw new IllegalArgumentException("응답 결과가 null입니다.");
            }

            System.out.println("API 응답: " + result);

            String uniqueId = (String) result.get("uniqueId");
            String name = (String) result.get("name");
            String email = (String) result.get("email");
            String department = (String) result.get("department");

            if (uniqueId == null || name == null || email == null || department == null) {
                throw new IllegalArgumentException("응답 결과에 필요한 값이 없습니다.");
            }

            // 새로운 사용자 객체 생성
            User user = User.builder()
                    .username(name)
                    .uniqueId(uniqueId)
                    .email(email)
                    .department(department)
                    .role("USER")
                    .password("12345")
                    .hisnetToken(token)
                    .build();

            // 활동 상태를 true로 업데이트
            userService.updateUserActiveStatus(user.getUsername(), true);

            return user;
        } catch (HttpStatusCodeException e) {
            Map<String, Object> result = new HashMap<>();
            try {
                result = new ObjectMapper().readValue(e.getResponseBodyAsString(), new TypeReference<>() {});
            } catch (Exception ex) {
                throw new IllegalArgumentException("Hisnet login failed");
            }
            System.out.println("API 오류 응답: " + result);
            throw new IllegalArgumentException(result.get("message").toString());
        }
    }
}
