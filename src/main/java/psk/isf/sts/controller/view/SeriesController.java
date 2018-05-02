package psk.isf.sts.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SeriesController {
	public static String templateDirRoot = "series/";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@GetMapping("/view/seriale-test")
	public String series() {
		return getTemplateDir("series");
	}
}
