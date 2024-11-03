package com.cocomo.secondhand_transaction.service;

import com.cocomo.secondhand_transaction.dto.OrderDto;
import com.cocomo.secondhand_transaction.entity.Order;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.entity.constant.OrderCancelRequestStatus;
import com.cocomo.secondhand_transaction.entity.constant.Payment;
import com.cocomo.secondhand_transaction.entity.constant.RequestOrder;
import com.cocomo.secondhand_transaction.repository.OrderRepository;
import com.cocomo.secondhand_transaction.repository.ProductRepository;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.cocomo.secondhand_transaction.entity.constant.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

    // 상품 등록 유저 객체 찾기
    private User findUserByAuthentication(Authentication authentication) {
        String email = authentication.getName();  // 현재 로그인한 유저의 인증 정보에서 이메일 추출
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 거래 요청
    public void requestOrder(OrderDto.Order orderDto, Authentication authentication) {
        // 구매자 User 객체 찾기
        User buyer = findUserByAuthentication(authentication);

        // 구매 Product 객체 찾기
        Product product = productRepository.findProductByPdNum(orderDto.getPdNum())
                .orElseThrow(() -> new RuntimeException("해당 물품의 정보가 없습니다."));
        if (product.getStatus().equals(RESERVED)) {
            throw new RuntimeException("이 상품은 예약 중입니다.");
        } else if (product.getStatus().equals(SOLD_OUT)) {
            throw new RuntimeException("이 상품은 판매 종료된 상품입니다.");
        }

        // 판매자 User 객체 찾기
        User seller = product.getUser();

        // Order 객체 생성
        Order order = new Order(orderDto, seller, buyer, product);
        orderRepository.save(order);
        // 이메일 발송
        log.info("{}님, {}님으로부터 {}상품에 대한 거래 요청이 왔습니다.", seller.getNickname(), buyer.getNickname(), product.getPd_name());
    }

    // 거래 요청 승인
    public void approveOrder(String orderNum, Authentication authentication) {
        // order 찾기
        Order order = orderRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));

        // order 의 판매자와 일치하는지 확인
        User seller = findUserByAuthentication(authentication);
        if (!seller.equals(order.getSeller())) {
            throw new RuntimeException("판매자 권한이 없습니다.");
        }

        order.updateRequestOrder(RequestOrder.APPROVED);
        // 결제 바로
        if (order.getRequestOrder().equals(RequestOrder.APPROVED) && order.getRequestCancel().equals(OrderCancelRequestStatus.NONE)){
            order.updatePayment(Payment.DEPOSITED);
        }
        orderRepository.save(order);
        // 이메일 발송
        log.info("{}님, {}님이 거래 요청을 승인하셨습니다. 상품 정보 : {}, 판매자 번호 : {}",
                order.getBuyer().getNickname(), seller.getNickname(), order.getProduct().getPd_name(), seller.getPhone_nb()); // 구매자에게
        log.info("거래 요청을 승인하셨습니다. 상품 정보 : {}, 구매자 번호 : {}",
                order.getProduct().getPd_name(), order.getBuyer().getPhone_nb()); // -> 판매자에게

    }

    // 거래 요청 거절
    public void rejectOrder(String orderNum, Authentication authentication) {
        // order 찾기
        Order order = orderRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));

        // order 의 판매자와 일치하는지 확인
        User seller = findUserByAuthentication(authentication);
        if (!seller.equals(order.getSeller())) {
            throw new RuntimeException("판매자 권한이 없습니다.");
        }

        order.updateRequestOrder(RequestOrder.REJECTED);
        orderRepository.save(order);

        // 물품 상태 바꾸기
        Product product = order.getProduct();
        product.updateProductStatus(AVAILABLE);
        productRepository.save(product);
        // 이메일 발송
        log.info("{}님, {}이 거래 요청을 거절하셨습니다. 상품 정보 : {}", order.getBuyer().getNickname(), seller.getNickname(), order.getProduct().getPd_name());
    }

    // 거래 취소 요청
    public void cancelOrder(String orderNum, Authentication authentication) {
        // order 찾기
        Order order = orderRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));

        // product 찾기
        Product product = order.getProduct();

        // 구매자 확인
        User buyer = findUserByAuthentication(authentication);
        if (!buyer.equals(order.getBuyer())) {
            throw new RuntimeException("구매자 권한이 없습니다.");
        }

        // 거래 승인 및 결제 이전 -> 바로 취소 가능
        if (order.getPayment().equals(Payment.NONE)) {
            order.updateRequestOrder(RequestOrder.NONE);
            order.updateRequestCancelOrder(OrderCancelRequestStatus.APPROVED);
            order.updatePayment(Payment.REFUND);
            product.updateProductStatus(AVAILABLE);
            orderRepository.save(order);
            productRepository.save(product);
            // 이메일 발송
            log.info("{}님, {}님이 거래를 취소 하셨습니다. 상품 정보 : {}", order.getSeller().getNickname(), buyer.getNickname(), order.getProduct().getPd_name());
            log.info("{}님, 거래가 취소 되었습니다. 상품 정보 : {}", buyer.getNickname(), order.getProduct().getPd_name());
            return;
        }
        // 결제 후인 경우
        order.updateRequestCancelOrder(OrderCancelRequestStatus.REQUESTED);
        // 이 때 이메일 발송
        log.info("{}님, {}님이 거래 취소 요청을 하셨습니다. 상품 정보 : {}", order.getSeller().getNickname(), buyer.getNickname(), order.getProduct().getPd_name());
        orderRepository.save(order);
    }

    // 거래 취소 요청 승인
    public void approveCancel(String orderNum, Authentication authentication) {
        User seller = findUserByAuthentication(authentication);
        Order order = orderRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다"));

        if (!seller.equals(order.getSeller())) {
            throw new RuntimeException("판매자 권한이 없습니다.");
        }

        Product product = order.getProduct();

        if (order.getRequestOrder().equals(RequestOrder.REQUESTED)) {
            order.updateRequestCancelOrder(OrderCancelRequestStatus.APPROVED);
            order.updateRequestOrder(RequestOrder.NONE);
            order.updatePayment(Payment.REFUND); // 바로 환불
            product.updateProductStatus(AVAILABLE);
            orderRepository.save(order);
            productRepository.save(product);
            // 이메일 발송
            log.info("{}님, 거래가 취소 되었습니다. 상품 정보 : {}", order.getBuyer().getNickname(), product.getPd_name());
        } else {
            throw new RuntimeException("취소 요청된 주문이 아닙니다.");
        }
    }

    // 거래 취소 요청 거절
    public void rejectCancel(String orderNum, Authentication authentication) {
        User seller = findUserByAuthentication(authentication);
        Order order = orderRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다"));

        if (!seller.equals(order.getSeller())) {
            throw new RuntimeException("판매자 권한이 없습니다.");
        }

        Product product = order.getProduct();

        if (order.getRequestOrder().equals(RequestOrder.REQUESTED)) {
            order.updateRequestCancelOrder(OrderCancelRequestStatus.REJECTED);
            // 이메일 발송
            orderRepository.save(order);
            log.info("{}님, 거래가 취소 되었습니다. 상품 정보 : {}", order.getBuyer().getNickname(), product.getPd_name());
        } else {
            throw new RuntimeException("취소 요청된 주문이 아닙니다.");
        }
    }

    // 거래 확정
    public void confirmOrder(String orderNum, Authentication authentication) {
        Order order = orderRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));

        Product product = order.getProduct();

        User buyer = findUserByAuthentication(authentication);
        if (!buyer.equals(order.getBuyer())) {
            throw new RuntimeException("구매자 권한이 없습니다.");
        }

        if (!order.getRequestOrder().equals(RequestOrder.APPROVED) ||
        order.getRequestCancel().equals(OrderCancelRequestStatus.APPROVED) ||
        !order.getPayment().equals(Payment.DEPOSITED)) {
            throw new RuntimeException("거래 진행중 혹은 취소되었습니다.");
        }

        order.orderSuccess(); // 거래 확정
        log.info("거래가 확정되었습니다. 상품 정보 : {}", product.getPd_name()); // -> 판매자에게
        log.info("거래를 확정하셨습니다. 상품 정보 : {}", product.getPd_name()); // -> 구매자에게
        orderRepository.save(order);
        product.updateProductStatus(SOLD_OUT);
        productRepository.save(product);
    }


    // 거래 시간 문자열을 LocalDateTime 으로 변환
    private LocalDateTime parseSelectedTime(String selectedTime) {
        return LocalDateTime.parse(selectedTime, dateTimeFormatter);
    }

    // 스케줄러 - 3일이 지난 거래는 거래 확정 요청 알림 보내기
    @Scheduled(fixedRate = 86400000)
    public void sendConfirmationNotification() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = now.minusDays(3);

        // 알림을 아직 보내지 않았고, 거래 확정이 안된 주문들만 조회하기
        List<Order> orders = orderRepository.findBySuccessFalseAndNotifiedFalse();

        for (Order order : orders) {
            LocalDateTime selectedTime = parseSelectedTime(order.getSelectedTime());
            if (selectedTime.isBefore(threeDaysAgo)) {
                log.info("{} 님, 거래가 완료되었다면 확정 버튼을 눌러주세요. 거래 일자 이후 7일이 지나면 자동 확정이 됩니다.", order.getBuyer().getNickname());
                // 알림 전송 후 notified 상태 업데이트
                order.orderNotified();
                orderRepository.save(order);
            }
        }
    }

    // 스케줄러 - 7일이 지나면 자동 거래 확정
    @Scheduled(fixedRate = 86400000)
    public void confirmSuccess() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // 알림을 보냈지만 아직 거래 확정 X인 주문들만 조회
        List<Order> orders = orderRepository.findBySuccessFalseAndNotifiedTrue();

        for (Order order : orders) {
            LocalDateTime selectedTime = parseSelectedTime(order.getSelectedTime());
            if (selectedTime.isBefore(sevenDaysAgo)) {
                log.info("{} 님, 7일이 지나 거래가 확정 되었습니다.", order.getBuyer().getNickname());
                // 알림 전송 후 notified 상태 업데이트
                order.orderNotified();
                orderRepository.save(order);
            }
        }
    }

    // 거래 조회
    public List<OrderDto.responseOrder> getMyOrder(Authentication authentication) {
        User user = findUserByAuthentication(authentication);
        List<Order> myOrders = orderRepository.findAllBySeller(user);
        List<Order> myOrders2 = orderRepository.findAllByBuyer(user);
        myOrders.addAll(myOrders2);

        return myOrders.stream().map(order -> {
            OrderDto.responseOrder responseOrder = new OrderDto.responseOrder();
            responseOrder.setPdNum(order.getProduct().getPdNum());
            responseOrder.setProductName(order.getProduct().getPd_name());
            responseOrder.setSellerNickname(order.getSeller().getNickname());
            responseOrder.setBuyerNickname(order.getBuyer().getNickname());
            responseOrder.setSelectedTime(order.getSelectedTime());
            responseOrder.setStatus(order.getRequestOrder().toString());
            return responseOrder;
        }).collect(Collectors.toList());
    }
}
