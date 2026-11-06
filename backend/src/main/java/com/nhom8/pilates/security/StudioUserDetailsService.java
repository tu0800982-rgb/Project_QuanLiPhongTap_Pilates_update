package com.nhom8.pilates.security;

import com.nhom8.pilates.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StudioUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public StudioUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username)
                .map(StudioUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Thông tin đăng nhập không hợp lệ"));
    }
}
