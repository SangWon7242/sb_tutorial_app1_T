package com.sbs.tutorial.app1;

import com.sbs.tutorial.app1.boundedContext.home.controller.HomeController;
import com.sbs.tutorial.app1.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
      // mockMvc로 로그인 처리
  }

  @Test
  @DisplayName("user4로 로그인 후 프로필페이지에 접속하면 user4의 이메일이 보여야 한다.")
  void t4() throws Exception {

  }
}
