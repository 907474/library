package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member1;

    @BeforeEach
    void setUp() {
        member1 = new Member();
        member1.setId(1);
        member1.setName("John Doe");
        member1.setUsername("johndoe");
        member1.setEmail("john.doe@example.com");
    }

    @Test
    void saveMember_ShouldSaveNewMemberSuccessfully() {
        Member newMember = new Member();
        newMember.setUsername("janedoe");
        newMember.setEmail("jane.doe@example.com");

        when(memberRepository.findByUsername("janedoe")).thenReturn(Optional.empty());
        when(memberRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(newMember);

        Member savedMember = memberService.saveMember(newMember);

        assertNotNull(savedMember);
        assertEquals(LocalDate.now(), savedMember.getRegistrationDate());
        verify(memberRepository).save(newMember);
    }

    @Test
    void saveMember_ShouldUpdateExistingMember() {
        // This test simulates updating an existing member
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        member1.setPhoneNumber("555-1234");
        Member updatedMember = memberService.saveMember(member1);

        assertNotNull(updatedMember);
        assertEquals("555-1234", updatedMember.getPhoneNumber());
        // Verify findBy methods are not called for updates
        verify(memberRepository, never()).findByUsername(anyString());
        verify(memberRepository, never()).findByEmail(anyString());
        verify(memberRepository).save(member1);
    }

    @Test
    void saveMember_ShouldThrowException_WhenUsernameExists() {
        Member newMember = new Member();
        newMember.setUsername("johndoe");
        when(memberRepository.findByUsername("johndoe")).thenReturn(Optional.of(member1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.saveMember(newMember));
        assertEquals("Username already exists: johndoe", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void saveMember_ShouldThrowException_WhenEmailExists() {
        Member newMember = new Member();
        newMember.setUsername("janedoe");
        newMember.setEmail("john.doe@example.com");
        when(memberRepository.findByUsername("janedoe")).thenReturn(Optional.empty());
        when(memberRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(member1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.saveMember(newMember));
        assertEquals("Email already exists: john.doe@example.com", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }
}