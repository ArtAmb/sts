package psk.isf.sts.controller.view;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.service.authorization.AuthorizationService;
import psk.isf.sts.service.authorization.dto.AuthorizationDTO;

@Controller
public class AuthenticationController {

	public static String templateDirRoot = "authentication/";

	@Autowired
	private AuthorizationService authService;

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@GetMapping("/view/sign-in")
	public String singIn() {
		return getTemplateDir("logIn");
	}

	@GetMapping("/view/sign-in-error")
	public String loginError(Model model) {
		model.addAttribute("message", "Nieprawidłowy login lub hasło");
		return getTemplateDir("logIn");
	}

	@GetMapping("/view/my-profile")
	public String myProfile(Model model, Principal principal) {
		model.addAttribute("userLogin", principal.getName());
		return getTemplateDir("my-profile");
	}

	@GetMapping("/view/sign-up")
	public String singUp() {
		return getTemplateDir("sign-up");
	}

	@PostMapping("/view/sign-up")
	public String singUp(@ModelAttribute AuthorizationDTO dto, Model model) {

		try {
			authService.createNewUser(dto, Roles.ROLE_CLIENT.toRole());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute("email", dto.getEmail());
			model.addAttribute("login", dto.getLogin());
			model.addAttribute(dto);
			return getTemplateDir("sign-up");
		}

		model.addAttribute("message", "Udało sie założyć konto");
		return getTemplateDir("logIn");
	}

	@GetMapping("/view/sign-up/producer")
	public String singUpProducer() {
		return getTemplateDir("sign-up-producer");
	}
	
	@PostMapping("/view/sign-up/producer")
	public String singUpProducer(@ModelAttribute AuthorizationDTO dto, Model model) {

		try {
			authService.createNewUser(dto, Roles.ROLE_PRODUCER.toRole());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute("email", dto.getEmail());
			model.addAttribute("login", dto.getLogin());
			model.addAttribute(dto);
			return getTemplateDir("sign-up-producer");
		}

		model.addAttribute("message", "Udało sie założyć konto");
		return getTemplateDir("logIn");
	}

}
