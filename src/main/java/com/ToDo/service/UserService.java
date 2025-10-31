package com.ToDo.service;

import com.ToDo.domain.Role;
import com.ToDo.domain.User;
import com.ToDo.dto.UserCreateRequest;
import com.ToDo.dto.UserResponse;
import com.ToDo.dto.UserUpdateRequest;
import com.ToDo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 (비밀번호 암호화 포함)
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // ID 중복 체크
        if (userRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다");
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // User 생성
        User user = new User();
        user.setId(request.getId());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화 포함
        user.setEmail(request.getEmail());
        user.setRole(Role.MEMBER);

        // 저장
        User savedUser = userRepository.save(user);

        // Entity -> DTO 변환 후 반환
        return UserResponse.from(savedUser);
    }

    // 로그인 (암호화된 비밀번호 확인)
    public UserResponse login(String id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인 (암호화 상태 비교)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return UserResponse.from(user);
    }

    // 특정 사용자 조회
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        return UserResponse.from(user);
    }

    // 모든 사용자 조회
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 이메일 중복 체크 (다른 사용자가 같은 이메일을 사용하고 있는지 확인)
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 사용자 정보 업데이트
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // 비밀번호가 입력되었을 때만 변경 (암호화 포함)
        if(request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.from(updatedUser);
    }
    
    // 사용자 정보 삭제
    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // 사용자 역할 변경 (관리자 기능)
    @Transactional
    public UserResponse updateUserRole(String id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        user.setRole(Role.valueOf(role));
        User updatedUser = userRepository.save(user);
        return UserResponse.from(updatedUser);
    }
}
