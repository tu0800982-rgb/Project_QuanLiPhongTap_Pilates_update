package com.nhom8.pilates.service;

import com.nhom8.pilates.entity.User;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.UserRepository;
import com.nhom8.pilates.security.StudioUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;
    public CurrentUserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public User requireUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof StudioUserDetails details)) {
            throw new BusinessException("UNAUTHENTICATED", "Vui lòng đăng nhập để tiếp tục",
                    HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findById(details.getUser().getId())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Không tìm thấy tài khoản",
                        HttpStatus.UNAUTHORIZED));
    }
}
