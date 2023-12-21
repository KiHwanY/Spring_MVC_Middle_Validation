package hello.itemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

}


/*
*  [글로벌 설정 - 모든 컨트롤러에 다 적용]
*  @SpringBootApplication
   public class ItemServiceApplication implements WebMvcConfigurer {

	 public static void main(String[] args) {
	 	SpringApplication.run(ItemServiceApplication.class, args);

	 }

	 @Override
	 public Validator getValidator() {
		 return new ItemValidator();
	 	}
	}
*
*  위 Application 코드 처럼 글로벌 설정을 추가할 수 있다.
*  기존 Controller @InitBinder 를 제거해도 글로벌 설정으로 정상 동작하는 것을
*  확인할 수 있다.
*
*  <주의>
   글로벌 설정을 하면 다음에 설명할 BeanValidator 가 자동 등록되지 않는다.
   글로벌 설정 부분은 주석처리 해두자. 참고로 글로벌 설정을 직접 사용하는 경우는 드물다.
   *
   *
   #참고
   -> 검증시 @Validated , @Valid 둘 다 사용가능하다.
   *  javax.validation.@Valid 를 사용하려면 build.gradle 의존관계 추가가 필요하다.
   *  implementation 'org.springframework:spring-boot-starter-validation'
   *  @validated 는 스프링 전용 검증 애노테이션이고, @Valid 는 자바 표준 검증 애노테이션이다.
* */