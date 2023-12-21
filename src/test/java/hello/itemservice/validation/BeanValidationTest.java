package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationTest {

    /*
    *  [검증기 생성]
    *
    *  다음 코드와 같이 검증기(buildDefaultValidatorFactory)를 생성한다.
    *  이후 스프링과 통합하면 우리가 직접 이런 코드를 작성하지는 않으므로, 이렇게 사용하는구나 정도만 참고하자.
    *
    *   ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
    * */

    @Test
    void beanValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setItemName(" "); //공백
        item.setPrice(0);
        item.setQuantity(10000);

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        /*
        *  [검증 실행]
        *  검증 대상(item) 을 직접 검증기에 넣고 그 결과를 받는다. Set 에는 ConstrainViolation 이라는 검증 오류가 담긴다.
        *  따라서 결과가 비어있으면 검증 오류가 없는 것이다.
        * */
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation = " + violation.getMessage());
        }

        /*
        *  [실행 결과]
        *
           violation={interpolatedMessage='공백일 수 없습니다', propertyPath=itemName,
            rootBeanClass=class hello.itemservice.domain.item.Item,
            messageTemplate='{javax.validation.constraints.NotBlank.message}'}
            violation.message=공백일 수 없습니다
            violation={interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity,
            rootBeanClass=class hello.itemservice.domain.item.Item,
            messageTemplate='{javax.validation.constraints.Max.message}'}
            violation.message=9999 이하여야 합니다
            violation={interpolatedMessage='1000에서 1000000 사이여야 합니다',
            propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item,
            messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
            violation.message=1000에서 1000000 사이여야 합니다


           -> ConstrainViolation 출력 결과를 보면, 검증 오류가 발생한 객체, 필드, 메시지 정보등 다양한 정보를 확인할 수 있다.


           [정리]
           이렇게 빈 검증기(Bean Validation)를 직접 사용하는 방법을 알아보았다.
           아마 지금까지 배웠던 Spring MVC 검증 방법에 빈 검증기를 어떻게 적용하면 좋을지 생각이 들 것이다.
           Spring 은 이미 개발자를 위해 빈 검증기를 스프링에 완전히 통합해두었다.
        * */

    }
}
