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
import org.springframework.web.bind.annotation.RequestParam;

import psk.isf.sts.entity.Contract;
import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.registration.UserSourceSystem;
import psk.isf.sts.service.TaskService;
import psk.isf.sts.service.authorization.AuthorizationService;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.authorization.dto.AuthorizationDTO;
import psk.isf.sts.service.authorization.dto.UserDTO;
import psk.isf.sts.service.contract.ContractService;
import psk.isf.sts.service.contract.dto.ContractDTO;
import psk.isf.sts.service.profile.dto.MyProfileDTO;

@Controller
public class AuthenticationController {

	public static final String activateUserControlParam = "controlParam";

	class SessionConsts {
		public static final String userToRegister = "userToRegister";
	}

	public static String templateDirRoot = "authentication/";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@Autowired
	private ContractService contractService;

	@Autowired
	private AuthorizationService authService;

	@Autowired
	private UserService userService;

	@Autowired
	private TaskService taskService;

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
	public String myProfile(
			@RequestParam(name = activateUserControlParam, required = false) String requestParamControlParam,
			HttpSession session, Model model, Principal principal) {
		User user = userService.findByLogin(principal.getName());
		model.addAttribute("userLogin", user.getDisplayLogin());
		model.addAttribute("isFromOurSystem", user.getSourceSystem().equals(UserSourceSystem.STS));

		if (user.getThumbnail() != null)
			model.addAttribute("thumbnailUrl", user.getThumbnail().toURL());

		String controlParam = (String) session.getAttribute(activateUserControlParam);
		if (controlParam != null && requestParamControlParam != null && controlParam.equals(requestParamControlParam)) {
			userService.makeProducerAccountFullActive(user);
			session.removeAttribute(requestParamControlParam);
		}

		return getTemplateDir("my-profile");
	}

	@PostMapping("/view/my-profile")
	public String saveProfileSettings(@ModelAttribute MyProfileDTO dto, Model model, Principal principal)
			throws IllegalStateException, IOException {
		User user = userService.setThumbnailForUserByLogin(principal.getName(), dto.getPicture());
		model.addAttribute("isFromOurSystem", user.getSourceSystem().equals(UserSourceSystem.STS));
		model.addAttribute("userLogin", user.getDisplayLogin());
		if (user.getThumbnail() != null)
			model.addAttribute("thumbnailUrl", user.getThumbnail().toURL());

		return getTemplateDir("my-profile");
	}

	@PostMapping("/view/my-profile/delete-thumbnail")
	public String deleteThumbnail(Model model, Principal principal) throws IllegalStateException, IOException {
		User user = userService.findByLogin(principal.getName());
		model.addAttribute("isFromOurSystem", user.getSourceSystem().equals(UserSourceSystem.STS));
		model.addAttribute("userLogin", user.getDisplayLogin());
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
			authService.createNewUser(dto,
					UserDTO.builder().real(true).role(Roles.ROLE_VIEWER.toRole()).userType(UserType.VIEWER).build());
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
			authService.validateForProducer(dto);
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

		session.setAttribute(SessionConsts.userToRegister, dto);

		model.addAttribute("contractTemplateUrl", "/contract-templates/contract-template-1.txt");
		return getTemplateDir("sign-up-producer-contract");
	}

	@PostMapping("/view/sign-up/producer/contract/accept")
	public String singUpProducerAcceptContract(HttpSession session, Model model) {
		AuthorizationDTO userToRegister = (AuthorizationDTO) session.getAttribute(SessionConsts.userToRegister);
		Contract contract = null;

		User producer = null;
		try {
			producer = authService.createNewProducer(userToRegister);
			contract = contractService.createNewContract(
					ContractDTO.builder().producer(producer).contractTemplateName("contract-template-1.txt").build());

		} catch (Exception e) {
			if (producer != null)
				userService.deleteUser(producer);

			String message = e.getMessage() == null
					? "Błąd. Nie udało sie utworzyc konta. Prosze sie skontaktowac z administratorem"
					: e.getMessage();
			model.addAttribute("message", message);
			return getTemplateDir("logIn");
		}

		taskService.addAcceptContractTask(producer, contract);

		model.addAttribute("message", "Udało sie założyć konto");
		session.removeAttribute(SessionConsts.userToRegister);
		return getTemplateDir("logIn");
	}

	@PostMapping("/view/sign-up/producer/contract/decline")
	public String singUpProducerDeclineContract(HttpSession session, Model model) {
		session.removeAttribute(SessionConsts.userToRegister);

		model.addAttribute("message", "Anulowano rejestracje konta dla producenta");
		return getTemplateDir("logIn");
	}

}
