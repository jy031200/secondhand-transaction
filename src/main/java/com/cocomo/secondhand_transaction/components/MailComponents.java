package com.cocomo.secondhand_transaction.components;

import com.cocomo.secondhand_transaction.dto.OrderDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MailComponents {

    private final JavaMailSender javaMailSender;

/*    public void sendMailToBuyer(OrderDto orderDto) {
        String buyerEmail = orderDto.getBuyer().getUsername();
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(buyerEmail); // 구매자의 이메일
        msg.setSubject("Hello World"); // 구매자가 구매할 판매글 제목
        msg.setText("Hello World"); //

        javaMailSender.send(msg);

    }

    public void sendMailToSeller() {

        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo("114kit12@naver.com");
        msg.setSubject("Hello World");
        msg.setText("Hello World");

        javaMailSender.send(msg);

    }*/

    public boolean sendMail(String mail, String subject, String text) {

        boolean result = false;

        MimeMessagePreparator msg = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(mail);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(text, true);
            }
        };

        try {
            javaMailSender.send(msg);
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }
}