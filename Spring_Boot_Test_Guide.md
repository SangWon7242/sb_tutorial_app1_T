# Spring Boot 테스트 코드 가이드

## 📋 개요
Spring Boot 애플리케이션의 웹 계층을 테스트하는 방법과 현재 프로젝트의 테스트 코드 분석

## 🔧 주요 어노테이션

### @SpringBootTest
- **목적**: Spring Boot 애플리케이션 전체 컨텍스트를 로드
- **특징**: 실제 애플리케이션과 동일한 환경에서 테스트 실행
- **사용 시기**: 통합 테스트 시

### @AutoConfigureMockMvc
- **목적**: MockMvc 자동 구성
- **특징**: 웹 계층 테스트를 위한 가짜 HTTP 요청/응답 환경 제공
- **장점**: 실제 서버 없이 컨트롤러 테스트 가능

### @Transactional
- **목적**: 테스트 후 데이터베이스 롤백
- **특징**: 각 테스트 메서드 실행 후 자동으로 데이터 원상복구
- **장점**: 테스트 간 데이터 간섭 방지

### @DisplayName
- **목적**: 테스트 메서드의 한글 설명 제공
- **특징**: 테스트 결과에서 읽기 쉬운 이름 표시

## 🧪 현재 프로젝트 테스트 분석

### 테스트 대상
```java
@Test
@DisplayName("메인화면에서는 안녕이 나와야 한다.")
void contextLoads() throws Exception
```

### 테스트 시나리오
1. **WHEN (언제)**: GET 요청을 "/" 경로로 보냄
2. **THEN (그러면)**: 다음 조건들을 확인
   - HTTP 상태코드가 2xx (성공)
   - 핸들러가 `HomeController` 클래스
   - 메서드명이 "main"
   - 응답 내용에 "안녕" 문자열 포함

### 핵심 코드 구조
```java
// HTTP GET 요청 수행
ResultActions resultActions = mvc.perform(get("/")).andDo(print());

// 결과 검증
resultActions
    .andExpect(status().is2xxSuccessful())           // 상태코드 확인
    .andExpect(handler().handlerType(HomeController.class))  // 컨트롤러 확인
    .andExpect(handler().methodName("main"))         // 메서드명 확인
    .andExpect(content().string(containsString("안녕")));    // 내용 확인
```

## 🔍 MockMvc 주요 메서드

| 메서드 | 설명 |
|--------|------|
| `perform()` | HTTP 요청 수행 |
| `andDo(print())` | 요청/응답 정보 콘솔 출력 |
| `andExpect()` | 결과 검증 |
| `status()` | HTTP 상태코드 검증 |
| `handler()` | 핸들러 정보 검증 |
| `content()` | 응답 내용 검증 |

## 💡 테스트 작성 팁

### 1. Given-When-Then 패턴
- **Given**: 테스트 준비 (데이터 설정)
- **When**: 테스트 실행 (실제 동작)
- **Then**: 결과 검증 (기대값 확인)

### 2. 의미있는 테스트명
```java
@DisplayName("메인화면에서는 안녕이 나와야 한다.")
```
- 한글로 명확한 테스트 목적 표현
- 비개발자도 이해할 수 있는 설명

### 3. 다양한 검증 방법
- **상태코드**: `status().isOk()`, `status().is2xxSuccessful()`
- **내용 검증**: `containsString()`, `content().json()`
- **핸들러 검증**: `handlerType()`, `methodName()`

## 🚀 실행 방법
```bash
# Gradle 사용 시
./gradlew test

# Maven 사용 시
mvn test
```

## 📚 추가 학습 포인트
- **단위 테스트**: `@WebMvcTest` 사용
- **데이터 계층 테스트**: `@DataJpaTest` 사용
- **보안 테스트**: `@WithMockUser` 사용
- **JSON 응답 테스트**: `jsonPath()` 사용
