package psk.isf.sts.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import psk.isf.sts.service.authorization.AuthorizationService;
import psk.isf.sts.service.authorization.UserService;

@Controller
public class FbController {

	public static String templateDirRoot = "connect/";

	private Facebook facebook;
	private ConnectionRepository connectionRepository;

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@Autowired
	private UserService userService;

	@Autowired
	private AuthorizationService authService;

	public FbController(Facebook facebook, ConnectionRepository connectionRepository) {
		this.facebook = facebook;
		this.connectionRepository = connectionRepository;
	}

	@GetMapping("/sing-up/fb")
	public String helloFacebook(Model model) {
		if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
			return "redirect:/view/sign-in";
		}

		String[] fields = { "id", "email", "first_name", "last_name" };
		User fbProfile = facebook.fetchObject("me", User.class, fields);

		psk.isf.sts.entity.registration.User user = userService.findFacebookUserByFbId(fbProfile.getId());
		if (user == null) {
			authService.createFacebookUser(fbProfile);
		}

		String fbSignInUrl = authService.createSingInFbUserUrl(fbProfile);

		model.addAttribute("fbSignInUrl", fbSignInUrl);

		return getTemplateDir("fbLogIn");
	}
}
