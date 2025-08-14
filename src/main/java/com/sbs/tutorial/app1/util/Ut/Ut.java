package com.sbs.tutorial.app1.util.Ut;

import groovy.grape.IvyGrabRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Ut {
  // date : 정적적 중첩 클래스
  // static 키워드가 붙음
  // 유틸리티 클래스를 내부에서 숨길 수 있다는 장점
  public static class date {

    public static String getCurrentDateFormatted(String pattern) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
      String formatted = LocalDateTime.now().format(formatter);

      return formatted;
    }
  }

  public static class file {
    public static String getExt(String fileName) {
      return Optional.ofNullable(fileName)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(f.lastIndexOf(".") + 1))
          .orElse("");
    }
  }
}
