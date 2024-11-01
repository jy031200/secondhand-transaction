package zerobase.secondhand_transaction.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.secondhand_transaction.components.MailComponents;
import zerobase.secondhand_transaction.service.OrderService;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final MailComponents mailComponents;
//
//
//
//    String email = "114kit12@naver.com"; // 판매자 이메일 불러와야함
//    String subject = "주문 요청이 들어왔습니다.";
//    String text = "<p>고객님이 판매를 원하신 물건에 주문이 들어왔습니다</p>" +
//            "<div><a href ='http://localhost:8080/order/request'>아래 링크를 클릭하여 확인하세요</a></div>";
//
//    mailComponets.sendMail();


}