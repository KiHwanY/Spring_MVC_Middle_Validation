package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.*;


/*
*       [오류 메시지 출력]
*
* ->    타임리프 화면을 렌더링 할 때 th:errors 가 실행된다.
*       만약 이때 오류가 있다면 생성된 오류 메시지 코드를 순서대로 돌아가면서 메시지를 찾는다.
*       그리고 없으면 디폴트 메시지를 출력한다.
* */
public class MessageCodesResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    /*
    *  [MessageCodesResolver 란?]
    * 1. 검증 오류 코드로 메시지 코드들을 생성한다.
    * 2. MessageCodesResolver 인터페이스이고 DefaultMessageCodesResolver 는 기본 구현체이다.
    * 3. 주로 다음과 함께 사용 ObjectError , FieldError
    * */

    /*
    *  [DefaultMessageCodesResolver의 기본 메시지 생성 규칙]
    *
    *  {객체 오류}
    *   객체 오류의 경우 다음 순서로 2가지 생성
    * 1. : code + "." + object name
    * 2. : code
    *
    * ex) 오류 코드 : required, object name :item
    * 1. : required.item
    * 2. : required
    *
    *   {필드 오류}
    *    필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
    * 1. : code + "." + object name + "." + field
    * 2. : code + "." + field
    * 3. : code + "." + field type
    * 4. : code
    *
    * ex) 오류 코드 : typeMismatch , object name "user", field "age" , field type : int
    * 1. "typeMismatch.user.age"
    * 2. "typeMismatch.age"
    * 3. "typeMismatch.int"
    * 4. "typeMismatch"
    * */

    /*
     *  [동작 방식]
     *
     * 1. rejectValue() , reject()는 내부에서 MessageCodesResolver 를 사용한다. 여기에서 메시지 코드들을 생성한다.
     * 2. FieldError , ObjectError 의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있다.
     * 3. MessageCodesResolver 를 통해서 생성된 순서대로 오류 코드를 보관한다.
     * 4. 이 부분을 BindingResult 의 로그를 통해서 확인해 보자.
     *   - codes [range.item.price , range.price , range.java.lang.Integer, range]
     * */


    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    /*
    *  [ObjectError] - reject("totalPriceMin")
    *   다음 2가지 오류 코드를 자동으로 생성
    * 1. totalPriceMin.item
    * 2. totalPriceMin
    * */



    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }

    /*
     *  [FieldError] - rejectValue("itemName" , "required")
     *   다음 4가지 오류 코드를 자동으로 생성
     * 1. required.item.itemName
     * 2. required.itemName
     * 3. required.java.lang.String
     * 4. required
     * */

}
