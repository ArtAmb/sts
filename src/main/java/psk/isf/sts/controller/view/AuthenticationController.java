package psk.isf.sts.controller.view;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.service.authorization.AuthorizationService;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.authorization.dto.AuthorizationDTO;
import psk.isf.sts.service.profile.dto.MyProfileDTO;

@Controller
public class AuthenticationController {

	public static String templateDirRoot = "authentication/";

	@Autowired
	private AuthorizationService authService;

	@Autowired
	private UserService userService;

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
		User user = userService.findByLogin(principal.getName());
		model.addAttribute("userLogin", principal.getName());

		if (user.getThumbnail() != null)
			model.addAttribute("thumbnailUrl", user.getThumbnail().toURL());

		return getTemplateDir("my-profile");
	}

	@PostMapping("/view/my-profile")
	public String saveProfileSettings(@ModelAttribute MyProfileDTO dto, Model model, Principal principal)
			throws IllegalStateException, IOException {
		User user = userService.setThumbnailForUserByLogin(principal.getName(), dto.getPicture());

		model.addAttribute("userLogin", principal.getName());
		if (user.getThumbnail() != null)
			model.addAttribute("thumbnailUrl", user.getThumbnail().toURL());

		return getTemplateDir("my-profile");
	}

	@PostMapping("/view/my-profile/delete-thumbnail")
	public String deleteThumbnail(Model model, Principal principal) throws IllegalStateException, IOException {
		model.addAttribute("userLogin", principal.getName());
		User user = userService.findByLogin(principal.getName());
		user.setThumbnail(null);
		userService.saveUser(user);
		return getTemplateDir("my-profile");
	}

	@GetMapping("/view/sign-up")
	public String singUp() {
		return getTemplateDir("sign-up");
	}

	@PostMapping("/view/sign-up")
	public String singUp(@ModelAttribute AuthorizationDTO dto, Model model) {

		try {
			authService.createNewUser(dto, Roles.ROLE_VIEWER.toRole());
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
	public String singUpProducer(@ModelAttribute AuthorizationDTO dto, HttpSession session, Model model) {

		try {
			authService.validate(dto);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute("email", dto.getEmail());
			model.addAttribute("login", dto.getLogin());

			model.addAttribute("companyName", dto.getCompanyName());
			model.addAttribute("phoneNumber", dto.getPhoneNumber());
			model.addAttribute("address", dto.getAddress());
			model.addAttribute("nip", dto.getNip());
			return getTemplateDir("sign-up-producer");
		}

		session.setAttribute("userToRegister", dto);

		return getTemplateDir("sign-up-producer-contract");
	}

	@PostMapping("/view/sign-up/producer/contract/accept")
	public String singUpProducerAcceptContract(HttpSession session, Model model) {
		AuthorizationDTO userToRegister = (AuthorizationDTO) session.getAttribute("userToRegister");

		try {
			authService.createNewUser(userToRegister, Roles.ROLE_PRODUCER.toRole());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			return getTemplateDir("logIn");
		}

		model.addAttribute("message", "Udało sie założyć konto");
		return getTemplateDir("logIn");
	}

	@PostMapping("/view/sign-up/producer/contract/decline")
	public String singUpProducerDeclineContract(HttpSession session, Model model) {
		session.removeAttribute("userToRegister");

		model.addAttribute("message", "Anulowano rejestracje konta dla producenta");
		return getTemplateDir("logIn");
	}
}
