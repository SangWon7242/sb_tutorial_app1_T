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
    String profileImgDirName = "member";
    String fileName = UUID.randomUUID().toString() + ".png";
    String profileImgDirPath = genFileDirPath + "/" + profileImgDirName; // 폴더 경로
    String profileImgFilePath = profileImgDirPath + "/" + fileName; // 파일 경로

    new File(profileImgDirPath).mkdirs(); // 관련된 폴더가 혹시나 없다면 만들어준다.

    try {
      profileImg.transferTo(new File(profileImgFilePath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String profileImgRelPath = profileImgDirName + "/" + fileName;

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

  public Member join(String username, String password, String email) {

    Member member = Member.builder()
        .username(username)
        .password(password)
        .email(email)
        .build();
    memberRepository.save(member);

    return member;
  }

  public long count() {
    return memberRepository.count();
  }

  public void removeProfileImg(Member member) {
    if (member.getProfileImg() == null || member.getProfileImg().isEmpty()) return;

    // 추가적으로 memberService를 호출한다거나 하는게 아니면 member에 있어도 무관
    String profileImgPath = genFileDirPath + "/" + member.getProfileImg();
    File file = new File(profileImgPath);

    // 파일이 있는경우만 삭제
    if(file.exists()) file.delete();
  }
}
