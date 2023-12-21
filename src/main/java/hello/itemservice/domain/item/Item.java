package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/*
*  [Bean Validation - Object Error]
*
*  Bean Validation 에서 특정 필드(FieldError)가 아닌 해당 Object 관련 오류(ObjectError)는 어떻게 처리할 수 있을까?
*  다음과 같이(@ScriptAssert)를 사용하면 된다.
*
*
*  [@ScriptAssert 권장하지 않는 이유]
*  실제 사용해보면 제약이 많고 복잡하다.
*  실무에서는 검증 기능이 해당 객체의 범위를 넘어서는 경우들이 종종 등장하는데, 그런 경우 대응이 어렵다.
*
*  따라서 Object Error(Global Error) 의 경우 @ScriptAssert 을 억지로 사용하는 것보다는 Object Error 관련 부분만
*  직접 java code 로 작성하는 것을 권장한다.
* */
@Data
//@ScriptAssert(lang = "javascript" , script="_this.price * _this.quantity >= 10000")
public class Item {
    /*
    *  [Bean Validation 이란?]
    *  먼저 Bean Validation 은 특정한 구현체가 아니라 Bean Validation 2.0(JSR-380)이라는 기술 표준이다.
    *  쉽게 이야기해서 검증 애노테이션과 여러 인터페이스의 모음이다.
    *  마치 JPA가 표준 기술이고 그 구현체로 하이버네이트가 있는 것과 같다
    *
    *  Bean Validation 을 구현한 기술중에 일반적으로 사용하는 구현체는 하이버네이트 Validator 이다.
    *  이름이 하이버네이트가 붙어서 그렇지 ORM과는 관련이 없다.
    *
    *
    * [검증 애노테이션]
        @NotBlank : 빈값 + 공백만 있는 경우를 허용하지 않는다.
        @NotNull : null 을 허용하지 않는다.
        @Range(min = 1000, max = 1000000) : 범위 안의 값이어야 한다.
        @Max(9999) : 최대 9999까지만 허용한다.

    * [Bean Validation 메시지 찾는 순서]
    *  - 생성된 메시지 코드 순서대로 messageSource 에서 메시지 찾기
    *  - 애노테이션 message 속성 사용 -> @NotBlank(message = "공백! {0}")
    *  - 라이브러리가 제공하는 기본 값 사용 -> 공백일 수 없습니다.
    * */

    @NotNull(groups = UpdateCheck.class) //수정 요구사항 추가
    private Long id;

    /*
    *  [Bean Validation - 에러 코드]
    *
    *  Bean Validation 이 기본으로 제공하는 오류 메시지를 좀 더 자세히 변경하고 싶으면 어떻게 하면 될까 ?
    *
    *  Bean Validation 을 적용하고 'bindingResult' 에 등록된 검증 오류 코드를 보자.
    *  오류 코드가 애노테이션 이름으로 등록된다. 마치 'typeMismatch' 와 유사하다.
    *
    *  'NotBlank' 라는 오류 코드를 기반으로 'MessageCodesResolver' 를 통해 다양한 메시지 코드가 순서대로 생성된다.
    * */

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;
    /*
    *  [@NotBlank]`
    *  - NotBlank.item.itemName
    *  - NotBlank.itemName
    *  - NotBlank.java.lang.String
    *  - NonBlank
    *  -> 우선 순위
    * */
    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;
    /*
     *  [@Range]
     *  - Range.item.price
     *  - Range.price
     *  - Range.java.lang.Integer
     *  - Range
     *  -> 우선 순위
     * */

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = {SaveCheck.class})
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
