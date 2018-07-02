package psk.isf.sts.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

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

	private String selfUrl;

	@Autowired
	private UserService userService;

	@Autowired
	private AuthorizationService authService;

	public FbController(Facebook facebook, ConnectionRepository connectionRepository,
			@Value("${sts.self.url}") String selfUrl) {
		this.facebook = facebook;
		this.connectionRepository = connectionRepository;
		this.selfUrl = selfUrl;
	}

	@GetMapping("/sing-up/fb")
	public String helloFacebook(Model model) {
		if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
			return "redirect:/view/sign-in";
		}

		connectionRepository.findAllConnections();
		connectionRepository.findConnections("facebook");

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

	@GetMapping("/test/connection/fb")
	public boolean isConnectionWithFb() {
		return connectionRepository.findPrimaryConnection(Facebook.class) != null;
	}

	@PostMapping("/test/connection/fb/POST")
	public boolean isConnectionWithFbPost() {
		return connectionRepository.findPrimaryConnection(Facebook.class) != null;
	}

	@PostMapping("/remove/all/conections/for/fb")
	public String removeAllConectionsForFb() {
		connectionRepository.findAllConnections();
		connectionRepository.findConnections("facebook");

		connectionRepository.removeConnections("facebook");

		connectionRepository.findAllConnections();
		connectionRepository.findConnections("facebook");
		return "authentication/logOut";
	}

	@PostMapping("/remove/con/fb")
	public String magicFbRemove() {
		connectionRepository.findAllConnections();
		connectionRepository.findConnections("facebook");

		new RestTemplate().postForEntity(selfUrl + "/remove/all/conections/for/fb", null, Void.class);

		return "authentication/logOut";
	}
}
