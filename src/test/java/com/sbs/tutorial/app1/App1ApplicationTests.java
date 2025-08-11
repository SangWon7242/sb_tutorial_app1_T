package com.sbs.tutorial.app1;

import com.sbs.tutorial.app1.boundedContext.home.controller.HomeController;
import com.sbs.tutorial.app1.boundedContext.member.controller.MemberController;
import com.sbs.tutorial.app1.boundedContext.member.entity.Member;
import com.sbs.tutorial.app1.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
// @Transactional : JUnit 테스트 코드가 실행되면, 이 메서드가 끝나고나면, DB에 자동으로 롤백을 해준다.
@Transactional
@ActiveProfiles({"base-addi", "test"})
class App1ApplicationTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private MemberService memberService;

  @Test
  @DisplayName("메인화면에서는 안녕이 나와야 한다.")
  void contextLoads() throws Exception {
    // WHEN :
    // GET /

    ResultActions resultActions = mvc
        .perform(get("/")).andDo(print());
    // THEN
    // 안녕

    // resultActions : 결과를 담고 있는 변수
    // andExpect : 기대한다.
    resultActions.andExpect(status().is2xxSuccessful())
        .andExpect(handler().handlerType(HomeController.class))
        .andExpect(handler().methodName("main"))
        .andExpect(content().string(containsString("안녕")));
    // content() : 임포트 시에 servlet 이용
  }

  @Test
  @DisplayName("회원의 수")
  @Rollback(false)
  void t2() throws Exception {
    long count = memberService.count();
    assertThat(count).isGreaterThan(0);
  }

  @Test
  @DisplayName("user1로 로그인 후 프로필페이지에 접속하면 user1의 이메일이 보여야 한다.")
  void t3() throws Exception {
    // WHEN :
    // GET /
    ResultActions resultActions = mvc
        .perform(
            get("/member/profile")
                .with(user("user1").password("1234").roles("user"))
        ).andDo(print());

    // THEN
    resultActions.andExpect(status().is2xxSuccessful())
        .andExpect(handler().handlerType(MemberController.class))
        .andExpect(handler().methodName("showProfile"))
        .andExpect(content().string(containsString("user1@test.com")));
  }

  @Test
  @DisplayName("user4로 로그인 후 프로필페이지에 접속하면 user4의 이메일이 보여야 한다.")
  void t4() throws Exception {
// WHEN :
    // GET /
    ResultActions resultActions = mvc
        .perform(
            get("/member/profile")
                .with(user("user4").password("1234").roles("user"))
        ).andDo(print());

    // THEN
    resultActions.andExpect(status().is2xxSuccessful())
        .andExpect(handler().handlerType(MemberController.class))
        .andExpect(handler().methodName("showProfile"))
        .andExpect(content().string(containsString("user4@test.com")));
  }

  @Test
  @DisplayName("회원가입")
  @Rollback(false)
  void t5() throws Exception {
    // 파일 다운로드 - 실제 이미지 URL에서 다운로드
    String imageUrl = "https://picsum.photos/200/300";
    String originalFileName = "test-image.jpg";

    byte[] imageBytes;
    
    try {
      URL url = new URL(imageUrl);
      imageBytes = url.openStream().readAllBytes();
    } catch (IOException e) {
      // 네트워크 오류 시 기본 테스트 데이터 사용
      imageBytes = "test image content".getBytes();
    }
    
    MockMultipartFile profileImg = new MockMultipartFile(
        "profileImg", // 폼 필드명 (HTML form의 name 속성)
        originalFileName, // 원본 파일명
        "image/jpeg", // MIME 타입
        imageBytes // 실제 파일 데이터
    );
    
    // 회원가입(MVC MOCK)
    ResultActions resultActions = mvc
        .perform(
            multipart("/member/join")
                .file(profileImg)
                .param("username", "user5")
                .param("password", "1234")
                .param("email", "user5@test.com")
                .characterEncoding("UTF-8")
        ).andDo(print());
    
    // 회원가입 성공 시 프로필 페이지로 리다이렉트 확인
    resultActions.andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/member/profile"))
        .andExpect(handler().handlerType(MemberController.class))
        .andExpect(handler().methodName("join"));

    
    // 5번 회원이 생성, 테스트
    long memberCount = memberService.count();
    assertThat(memberCount).isEqualTo(5); // TestInitData에서 4명 + 새로 가입한 1명 = 5명
    
    // 생성된 회원 정보 확인
    Member member = memberService.getMemberByUsername("user5");
    assertThat(member).isNotNull();
    assertThat(member.getUsername()).isEqualTo("user5");
    assertThat(member.getEmail()).isEqualTo("user5@test.com");

    memberService.removeProfileImg(member);
  }
}
