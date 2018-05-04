package psk.isf.sts.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.service.series.SerialService;

@Controller
public class SeriesController {
	@Autowired
	private SerialService serialService;
	
	@Autowired
	private SerialRepository serialRepo;
	
	public static String templateDirRoot = "series/";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@GetMapping("/view/serials")
	public String serialsView(Model model) {
		model.addAttribute("serials", serialService.allSerials());
		return getTemplateDir("serials");
	}
	
	/*@GetMapping("/view/serials/{id}")
	public String serialsDetailView(@PathVariable Long id, Model model) {
		SerialElement serial = serialRepo.findOne(id);
		model.addAttribute("serial", serial);
		return getTemplateDir("serial-detail");
	}*/
}
