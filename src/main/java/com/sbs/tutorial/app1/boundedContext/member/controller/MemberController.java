package com.sbs.tutorial.app1.boundedContext.member.controller;

import com.sbs.tutorial.app1.boundedContext.member.entity.Member;
import com.sbs.tutorial.app1.boundedContext.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/join")
  public String showJoin() {
    return "member/join";
  }

  @PostMapping("/join")
  public String join(HttpServletRequest req, String username, String password, String email, MultipartFile profileImg) {
    Member oldMember = memberService.getMemberByUsername(username);

    String passwordClearText = password;
    password = passwordEncoder.encode(password);

    if (oldMember != null) {
      return "redirect:/?errorMsg=Already done.";
    }

    Member member = memberService.join(username, password, email, profileImg);

    try {
      req.login(username, passwordClearText); // 로그인 처리
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }
    
    return "redirect:/member/profile";
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/profile")
  public String showProfile(Principal principal, Model model) {
    Member loginedMember = memberService.getMemberByUsername(principal.getName());

    model.addAttribute("loginedMember", loginedMember);

    return "member/profile";
  }
}
