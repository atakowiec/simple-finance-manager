package pl.pollub.backend.notifications;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MailController {

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/sendMail")
    public String sendMail(@RequestParam String email) {

        try {
            mailService.sendMail(
                    email,
                    "Zbliżasz się do przekroczenia limitu wydatków",
                    "<b>Uważaj z wydatkami, twój limit niebawem zostanie przekroczony</b><br>:P",
                    true
            );
            return "Wysłano e-mail do: " + email;

        } catch (MessagingException e) {
            return "Nie udało się wysłać e-maila do: " + email;
        }
    }
}
