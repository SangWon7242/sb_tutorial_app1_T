package com.sbs.tutorial.app1.boundedContext.member.entity;

import com.sbs.tutorial.app1.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder // 상속 구조에서 부모 클래스의 필드까지 포함하여 빌더 패턴을 사용
@ToString(callSuper = true) // 부모 클래스의 toString() 메서드도 호출하여 출력 결과에 포함
public class Member extends BaseEntity {
  @Column(unique = true)
  private String username;
  private String password;
  private String email;
  private String profileImg;
}
