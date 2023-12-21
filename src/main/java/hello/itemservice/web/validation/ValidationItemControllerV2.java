package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);

        /*
        *  WebDataBinder 는 스프링의 파라미터 바인딩의 역할을 해주고 검증 기능도 내부에 포함한다.
        *  위처럼 'WebDataBinder' 에 검증기를 추가하면 해당 Controller에서는 검증기를 자동으로 적용할 수 있다.
        *  @InitBinder -> 해당 Controller 에서만 영향을 준다. 글로벌 설정은 별도로 해야한다.
        *
        * 위 검증기를 사용하려면 해당 메서드의 파라미터 매개변수 셋팅할 때 @Validated 어노테이션을 붙여줘야 한다.
        * (addItemV6 메서드 참고)
        * */
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }
//    BindingResult = 바인딩된 결과
//    BindingResult bindingResult 파라미터 위치는 @ModelAttribute Item item 다음에 와야 한다.

    /*BindingResult란 ?
    *
    * BindingResult는 스프링이 제공하는 값 검증 오류 처리의 핵심이다.
    * BindingResult는 스프링이 제공하는 검증 오류를 보관하는 객채이기 때문이고, 데이터 유효성 검사를 실패하면 ConstraintViolationException을 발생시키는데,
    * 데이터가 유효하지 않은 속성이 있으면 그에 대한 에러 정보를 BindingResult에 담는다.
    *
    * 정상적인 동작에서는 BindingResult에 담은 오류 정보를 가지고 Controller를 호출한다.
    * 하지만 BindingResult가 없다면 4xx 오류가 발생하면서 컨트롤러가 호출되지 않고 오류 페이지로 이동하게 된다.
    *
    * BindingResult 메서드를 사용하는 방식은 다음과 같다.
    *
    * boolean hasErrors() : 에러의 유무를 판단한다.
    * boolean hasGlobalErrors() : 글로벌 에러의 유무를 판단한다.
    * void addError(ObjectError error) : field, type, value 등의 에러를 출력할 수 있다.
    * void rejectValue() : field , errorCode, defaultMessage 등을 받아서 reject 됐을 대 데이터를 넘길 수 있다.
    *
    * [주로 전달하는 파라미터]
    *
    * objectName : 오류가 발생한 객체의 이름
    * field : 오류 필드
    * rejectValue : 사용자가 입력한 값(거절된 값)
    * bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
    * codes : 메시지 코드
    * arguments : 메시지에서 사용하는 인자
    * defaultMessage : 기본 오류 메시지
    * */


//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
//      FieldError 생성자 요약
//      public FieldError(String objectName , String field , String defaultMessage){}
//      필드에 오류가 있으면 FieldError 객체를 생성해서 bindingResult에 담아두면 된다.
        /*
        * objectName : @ModelAttribute 이름
        * field : 오류가 발생한 필드 이름
        * defaultMessages : 오류 기본 메시지
        * */
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
                // 필드가 없기 때문에 ObjectError를 생성해서 해줘야 한다.
            }
            /* ObjectError 생성자 요약
            * public ObjectError(String objectName ,  String defaultMessage) {}
            *
            * 특정 필드를 넘어서는 오류가 있으면 ObjectError 객체를 생성해서 bindingResult 에 담아두면 된다.
            * objectName : @ModelAttribute 의 이름
            * defaultMessages : 오류 기본 메시지
            *
            * FieldError 객체는 ObjectError 객체의 자식 타입이다.
            * */
        }
        //bindingResult.hasErrors() -> bindingResult가 에러가 있으면 ?
        //model 에 안담아도 알아서 Spring 처리 해준다.
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

  //  @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수 입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null ,null, "수량은 최대 9,999 까지 허용합니다."));
        }
/*    objectName : 오류가 발생한 객체의 이름
    * field : 오류 필드
    * rejectValue : 사용자가 입력한 값(거절된 값)
    * bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
    * codes : 메시지 코드
    * arguments : 메시지에서 사용하는 인자
    * defaultMessage : 기본 오류 메시지*/

        /* [오류 발생시 사용자 입력값 유지]
        * new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."
        *
        * 사용자의 입력 데이터가 컨트롤러의 @ModelAttribute에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어렵다.
        * 예를 들어서 가격에 숫자가 아닌 문자가 입력된다면 가격은 Integer 타입이므로 문자를 보관할 수 있는 방법이 없다.
        * 그래서 오류가 발생한 경우 사용자 입력 값을 보관하는 별도의 방법이 필요하다.
        * 그리고 이렇게 보관한 사용자 입력 값을 검증 오류 발생시 화면에 다시 출력하면 된다.
        *
        * FieldError 는 오류 발생 시 사용자 입력 값을 저장하는 기능을 제공한다.
        *
        * 여기서 rejectValue 가 바로 오류 발생시 사용자 입력 값을 저장하는 필드다.
        * bindingFailure 는 타입 오류 같은 바인딩이 실패했는지 여부를 적어주면 된다. 여기서는 바인딩이 실패한 것은 아니기 때문에 false 를 사용한다.
        *  */

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",null ,null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /*
        * [FieldError 생성자]
        *
        * FieldError 는 두 가지 생성자를 제공한다.
        * public FieldError(String objectName, String field, String defaultMessage);
          public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable
          Object[] arguments, @Nullable String defaultMessage)

          FieldError, ObjectError 의 생성자는 codes,arguments 를 제공한다. 이것은 오류 발생시 오류 코드로 메시지를 찾기 위해 사용된다.

        * */


        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"} ,new Object[]{9999}, null));
        }

        /*
        *  [error 메시지 파일 생성]
        * messages.properties 를 사용해도 되지만, 오류 메시지를 구분하기 쉽게 errors.properties 라는 별도의 파일로 관리하고 있다.
        *
        * 먼저 스프링 부트가 해당 메시지 파일을 인식할 수 있게 다음 설정을 추가한다.
        * {application.properties}
        * spring.messages.basename=messages,errors
        *
        * 이렇게 하면 messages.properties , errors.properties 두 파일을 모두 인식한다.(생략하면 messages.properties를 기본으로 인식한다.)
        * */

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"} ,new Object[]{10000, resultPrice}, null));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /*
    * [목표]
    * FieldError, ObjectError 는 다루기 너무 번거롭다.
    * 오류 코드도 좀 더 자동화 할 수 있지 않을까? ex) item.itemName처럼?
    *
    * Controller 에서 BindingResult 는 검증해야 할 객체인 target 바로 다음에 온다.
    * 따라서 BindingResult 는 이미 본인이 검증해야 할 객체인 target 을 알고 있다.
    * */

//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());
        /*
        * [rejectValue(), reject()]
        *
        * BindingResult 가 제공하는 rejectValue(), reject()를 사용하면 FieldError, ObjectError 를 직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.
        *
        * [rejectValue()]
        * void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
        *
        * field : 오류 필드명
        * errorCode : 오류 코드(이 오류 코드는 메시지에 등록된 코드가 아니다. 뒤에서 설명할 messageResolver 를 위한 오류 코드이다.)
        * errorArgs : 오류 메시지에서 {0} 을 치환하기 위한 값
        * defaultMessage : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지
        *
        * ex )
        * bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null)
        *
        * 앞에서 BindingResult 는 어떤 객체를 대상으로 검증하는지 target 을 이미 알고 있다고 했다. 따라서 target(item) 에 대한 정보는 없어도 된다.
        * 오류 필드명은 동일하게 price 를 사용했다.
        *
        * [축약된 오류 코드]
        * FieldError()를 직접 다룰 때는 오류 코드를 range.item.price 와 같이 모두 입력했다. 그런데 rejectValue()를 사용하고 부터는
        * 오류 코드를 range 로 간단하게 입력했다. 그래도 오류 메시지를 잘 찾아서 출력한다. 무언가 규칙이 있는 것 처럼 보인다.
        *
        * 이 부분을 이해하려면 MessageCodesResolver를 이해해야 한다. 왜 이런식으로 오류 코드를 구성하는지 바로 다음에 자세히 알아보자.
        * */

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // target, Errors를 넘겨준다.
        itemValidator.validate(item, bindingResult);

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /*
    *  [@Validated 란?]
    *  @Validated는 검증기를 실행하라는 애노테이션이다.
    *  이 애노테이션이 붙으면 앞서 'webDataBinder' 에 등록한 검증기를 찾아서 실행한다.
    *  그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할 지 구분이 필요하다.
    *  이때 'supports()' 가 사용된다.
    *
    *  여기서는 'supports(Item.class)' 호출되고, 결과가 'true' 이므로, 'ItemValidator' 의 'validate()' 가 호출된다.
    * */
    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

