package com.sbs.tutorial.app1.base;

import com.sbs.tutorial.app1.boundedContext.member.entity.Member;
import com.sbs.tutorial.app1.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MemberService memberService;

    @ModelAttribute("loginedMember")
    public Member addLoginedMember(Principal principal) {
        if (principal != null && principal.getName() != null) {
            return memberService.getMemberByUsername(principal.getName());
        }
        return null;
    }

  @ModelAttribute("loginedMemberProfileImg")
  public String addLoginedMemberProfileImg(@ModelAttribute("loginedMember") Member loginedMember) {
    // 이미 조회된 loginedMember를 재사용하여 DB 중복 조회 방지
    if (loginedMember != null && loginedMember.getProfileImg() != null && !loginedMember.getProfileImg().isEmpty()) {
      System.out.println("loginedMember.getProfileImg() : " + loginedMember.getProfileImg());
      return "/gen/" + loginedMember.getProfileImg();
    }
    return ""; // null 대신 빈 문자열 반환
  }
}
