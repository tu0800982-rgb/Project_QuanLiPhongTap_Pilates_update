package com.nhom8.pilates;

import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.RoleCode;
import com.nhom8.pilates.security.*;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test
    void accessAndRefreshTokensMustHaveDifferentTypes() {
        String secret=Base64.getEncoder().encodeToString("0123456789012345678901234567890123456789".getBytes());
        JwtService service=new JwtService(secret,30,7);
        User user=new User(); user.setId(8L); user.setEmail("member@pilates.com");
        user.setRole(new Role(RoleCode.MEMBER,"Hội viên"));
        StudioUserDetails principal=new StudioUserDetails(user);

        String access=service.createAccessToken(principal);
        String refresh=service.createRefreshToken(principal);

        assertFalse(service.isRefreshToken(access));
        assertTrue(service.isRefreshToken(refresh));
        assertEquals("member@pilates.com",service.extractUsername(refresh));
        assertNotEquals(service.extractTokenId(access),service.extractTokenId(refresh));
    }
}
