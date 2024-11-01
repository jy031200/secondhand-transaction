package zerobase.secondhand_transaction;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.secondhand_transaction.components.MailComponents;


@Controller
public class MainController {

    @RequestMapping ("/")
    public String index() {

        return "mainpage";
    }
}