package com.cocomo.secondhand_transaction.controller;

import com.cocomo.secondhand_transaction.dto.OrderDto;
import com.cocomo.secondhand_transaction.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cocomo.secondhand_transaction.components.MailComponents;

@RequiredArgsConstructor
@Controller
public class OrderController {

    private final MailComponents mailComponents;

    @GetMapping("/order/request") // 판매자의 여러 거래 날짜 및 시간 중 구매자가 하나를 선택한 후 판매자한테 정보 전달
    public String request() {

        return "/order/request";
    }

    @RequestMapping("/order/requested") // 거래 요청 승인/거절 선택 메일
    public String requested(OrderDto orderDto) {

        String email = orderDto.getSeller().getEmail(); // 판매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+" 에 대한 거래 요청이 도착했습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getSeller().getNickname() + "님, 판매 중인 "
                + orderDto.getProduct().getPd_name() + " 에 대한 거래 요청이 들어왔습니다.</p>" +
                "<div><a href='http://localhost:8080/order/request'>여기를 클릭하여 요청을 확인하세요.</a></div>";

        mailComponents.sendMail(email, subject, text);
        return "/order/requested";
    }

    @RequestMapping("/order/request_accept") // 거래 요청 승인 시
    public String accept(OrderDto orderDto) {
        String email = orderDto.getBuyer().getEmail(); // 구매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 판매자의 거래 요청 답변이 도착했습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getBuyer().getNickname() + "님.</p>" +
                "<p>요청하신 " + orderDto.getProduct().getPd_name() + " 에 대한 거래 요청이 판매자에 의해 수락되었습니다.</p>" +
                "<p>거래 진행을 위해 판매자와 연락을 취해 주세요.</p>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/accept";
    }

    @RequestMapping("/order/request_refuse") // 거래 요청 거절 시
    public String refuse(OrderDto orderDto) {
        String email = orderDto.getBuyer().getEmail(); // 구매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 판매자의 거래 요청 답변이 도착했습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getBuyer().getNickname() + "님.</p>" +
                "<p>요청하신 " + orderDto.getProduct().getPd_name() + " 에 대한 거래 요청이 판매자에 의해 거절되었습니다.</p>" +
                "<p>다른 상품을 확인하시거나 다음 기회에 거래가 성사되길 바랍니다.</p>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/refuse";
    }

    @RequestMapping("/order/product_payment_buyer") // 상품 결제 완료 메일
    public String payment_buyer(OrderDto orderDto) {
        String email = orderDto.getBuyer().getEmail(); // 구매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 결제가 완료되었습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getBuyer().getNickname() + "님.</p>" +
                "<p>구매하신 " + orderDto.getProduct().getPd_name() + " 의 결제가 완료되었습니다.</p>" +
                "<p>판매자가 거래를 준비 중이니 잠시만 기다려 주세요. 상품 관련 문의사항이 있으면 판매자와 연락을 취해 주세요.</p>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/product_payment";
    }

    @RequestMapping("/order/product_payment_seller") // 상품 결제 완료 메일
    public String payment_seller(OrderDto orderDto) {
        String email = orderDto.getSeller().getEmail(); // 판매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 결제가 완료되었습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getSeller().getNickname() + "님.</p>" +
                "<p>고객님께서 판매하신 " + orderDto.getProduct().getPd_name() + " 의 결제가 완료되었습니다.</p>" +
                "<p>상품을 준비해 주세요.</p>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/product_payment";
    }

    @RequestMapping("/order/cancel") // 거래 취소 요청
    public String cancel(OrderDto orderDto, Order order) {
        String email = orderDto.getSeller().getEmail(); // 판매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 구매자의 거래 취소 요청이 도착했습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getSeller().getNickname() + "님.</p>" +
                "<p>구매자 " + orderDto.getBuyer().getNickname() + "님이 " + orderDto.getProduct().getPd_name() + " 에 대한 거래 취소를 요청하셨습니다.</p>" +
                "<p>거래 취소 요청을 수락하거나 거절해 주세요.</p>" +
                "<div style='margin-top: 10px;'>" +
                "<a href='http://localhost:8080/order/cancel_accept?orderId=" + order.getOdNum()+ "' style='display:inline-block; padding:10px 20px; background-color:#4CAF50; color:white; text-align:center; text-decoration:none; border-radius:5px;'>거래 취소 승인</a>" +
                "<a href='http://localhost:8080/order/cancel_refuse?orderId=" + order.getOdNum() + "' style='display:inline-block; padding:10px 20px; background-color:#f44336; color:white; text-align:center; text-decoration:none; border-radius:5px;'>거래 취소 거절</a>" +
                "</div>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/cancel";
    }

    @RequestMapping("/order/cancel_accept") // 거래 취소 승인 시
    public String cancel_accept(OrderDto orderDto)  {
        String email = orderDto.getBuyer().getEmail(); // 구매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 판매자의 거래 취소 요청 답변이 도착했습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getBuyer().getNickname() + "님.</p>" +
                "<p>요청하신 " + orderDto.getProduct().getPd_name() + " 에 대한 거래 취소 요청이 판매자에 의해 수락되었습니다.</p>" +
                "<p>구매자 요청에 따라 거래가 취소되었으므로 환불 처리가 진행됩니다.</p>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/cancel_accept";
    }

    @RequestMapping("/order/cancel_refuse") // 거래 취소 거절 시
    public String cancel_refuse(OrderDto orderDto) {
        String email = orderDto.getBuyer().getEmail(); // 구매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 판매자의 거래 취소 요청 답변이 도착했습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getBuyer().getNickname() + "님.</p>" +
                "<p>요청하신 " + orderDto.getProduct().getPd_name() + " 에 대한 거래 취소 요청이 판매자에 의해 거절되었습니다.</p>" +
                "<p>거래는 계속 진행됩니다. 추가적인 문의가 있으시면 언제든지 연락주시기 바랍니다.</p>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/cancel_refuse";
    }

    @RequestMapping("/order/confirm") // 거래 성사 확인
    public String deal_completed(OrderDto orderDto, Order order) {
        String email = orderDto.getBuyer().getEmail(); // 구매자 이메일 불러와야함
        String subject = orderDto.getProduct().getPd_name()+"에 대한 판매자의 거래 요청 답변이 도착했습니다."; // 메일 제목
        String text = "<p>안녕하세요, " + orderDto.getBuyer().getNickname() + "님.</p>" +
                "<p>구매하신 " + orderDto.getProduct().getPd_name() + " 의 거래가 성사되었습니다.</p>" +
                "<p>아래 버튼을 클릭하여 거래를 확정해 주세요.</p>" +
                "<div><a href='http://localhost:8080/order/confirm?orderId=" + order.getOdNum() + "' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; border-radius: 5px;'>거래 확정</a></div>" +
                "<p>감사합니다.</p>";

        mailComponents.sendMail(email, subject, text);
        return "/order/deal_completed";
    }
}