package psk.isf.sts.controller.view;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.service.authorization.RandomStringGenerator;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.integration.PayuService;
import psk.isf.sts.service.integration.dto.PayuOrderResponse;

@Controller
public class MyProfileController {

	public static String templateDirRoot = "authentication/";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@Autowired
	private UserService userService;

	@Autowired
	private PayuService paymentService;

	@GetMapping("/view/producer/account/activation")
	public String getProducerPayToActivateView(Principal principal, Model model) {
		User user = userService.findByLogin(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("linkToPayment", null);
		return getTemplateDir("pay-to-activate");
	}

	@PostMapping("/view/producer/account/activation")
	public String payURedirect(Principal principal, HttpSession session, Model model) {
		User user = userService.findByLogin(principal.getName());

		RandomStringGenerator randomStringGenerator = new RandomStringGenerator(16);
		String randomParam = randomStringGenerator.rand();

		session.setAttribute(AuthenticationController.activateUserControlParam, randomParam);

		PayuOrderResponse response = paymentService.getPaymentRedirect(user, randomParam);

		model.addAttribute("user", user);
		model.addAttribute("linkToPayment", response.getRedirectUri());

		return getTemplateDir("pay-to-activate");
	}

}
