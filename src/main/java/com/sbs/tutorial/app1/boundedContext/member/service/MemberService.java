package com.sbs.tutorial.app1.boundedContext.member.service;

import com.sbs.tutorial.app1.boundedContext.member.entity.Member;
import com.sbs.tutorial.app1.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
  @Value("${custom.genFileDirPath}")
  private String genFileDirPath;

  private final MemberRepository memberRepository;

  public Member getMemberByUsername(String username) {
    return memberRepository.findByUsername(username).orElse(null);
  }

  public Member join(String username, String password, String email, MultipartFile profileImg) {
    // 프로필 이미지가 저장될 경로를 생성한다.
    String profileImgRelPath = "member/" + UUID.randomUUID().toString() + ".png";
    File profileImgFile = new File(genFileDirPath + "/" + profileImgRelPath);

    if (!profileImgFile.exists()) {
      profileImgFile.mkdirs(); // 관련된 폴더가 혹시나 없다면 만들어준다.
    }

    try {
      profileImg.transferTo(profileImgFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Member member = Member.builder()
        .username(username)
        .password(password)
        .email(email)
        .profileImg(profileImgRelPath)
        .build();
    memberRepository.save(member);

    return member;
  }

  public Member getMemberById(Long id) {
    return memberRepository.findById(id).orElse(null);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByUsername(username).get();

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("member"));

    return new User(member.getUsername(), member.getPassword(), authorities);
  }
}
