package zerobase.secondhand_transaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import zerobase.secondhand_transaction.components.MailComponents;

@RequiredArgsConstructor
@Controller
public class OrderController {

    private final MailComponents mailComponents;

    @GetMapping("/order/request")
    public String request() {

        return "/order/request";
    }

    @RequestMapping("/order/requested")
    public String index() {

        String email = "114kit12@naver.com"; // 판매자 이메일 불러와야함
        String subject = "주문 요청이 들어왔습니다.";
        String text = "<p>고객님이 판매를 원하신 물건에 주문이 들어왔습니다</p>" +
                "<div><a href ='http://localhost:8080/order/request'>링크를 클릭하여 확인하세요</a></div>";

        mailComponents.sendMail(email, subject, text);

        return "/order/requested";
    }

    @RequestMapping("/order/accept")
    public String accept() {

        return "/order/accept";
    }

    @RequestMapping("/order/refuse")
    public String refuse() {

        return "/order/refuse";
    }
}