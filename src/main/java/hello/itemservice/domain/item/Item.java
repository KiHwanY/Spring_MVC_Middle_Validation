package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
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
    * */

    @NotNull(groups = UpdateCheck.class) //수정 요구사항 추가
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

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
