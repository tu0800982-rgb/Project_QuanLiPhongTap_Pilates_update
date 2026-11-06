package com.nhom8.pilates;

import com.nhom8.pilates.dto.request.UpdateProfileRequest;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.RoleCode;
import com.nhom8.pilates.repository.*;
import com.nhom8.pilates.service.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {
    @Test
    void memberCanOnlyUpdateOwnResolvedProfile() throws Exception {
        CurrentUserService current=mock(CurrentUserService.class);UserRepository users=mock(UserRepository.class);
        MemberRepository members=mock(MemberRepository.class);EmployeeRepository employees=mock(EmployeeRepository.class);
        ProfileService service=new ProfileService(current,users,members,employees);
        Role role=new Role(RoleCode.MEMBER,"Hội viên");User user=new User();user.setId(5L);user.setRole(role);user.setEmail("member@pilates.com");user.setFullName("Old");
        Member member=new Member();setId(member,8L);member.setUser(user);member.setMemberCode("MB00001");member.setFullName("Old");member.setPhone("0900000000");
        when(current.requireUser()).thenReturn(user);when(members.findByUserId(5L)).thenReturn(Optional.of(member));when(employees.findByUserId(5L)).thenReturn(Optional.empty());
        UpdateProfileRequest request=new UpdateProfileRequest();request.setFullName("Tên mới");request.setPhone("0911111111");
        var result=service.update(request);
        assertEquals("Tên mới",result.getFullName());assertEquals("0911111111",member.getPhone());verify(members).save(member);verify(users).save(user);
    }
    private void setId(Object target,Long id)throws Exception{Field f=target.getClass().getDeclaredField("id");f.setAccessible(true);f.set(target,id);}
}
