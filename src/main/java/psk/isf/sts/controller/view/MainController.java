package psk.isf.sts.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	public static String templateDirRoot = "";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@GetMapping("/")
	public String getMainView() {
		return getTemplateDir("homeView");
	}
}
