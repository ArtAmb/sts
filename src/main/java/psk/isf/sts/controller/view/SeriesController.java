package psk.isf.sts.controller.view;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.series.SerialService;
import psk.isf.sts.service.series.dto.CommentDTO;
import psk.isf.sts.service.series.dto.SerialDTO;

@Controller
public class SeriesController {
	@Autowired
	private SerialService serialService;
	@Autowired
	private UserService userService;

	public static String templateDirRoot = "series/";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@GetMapping("/view/serials")
	public String serialsView(Model model) {
		model.addAttribute("serials", serialService.allSerials());
		return getTemplateDir("serials");
	}
	
	@GetMapping("/view/season-detail")
	public String seasonView(Model model) {
		//model.addAttribute("serials", serialService.allSerials());
		return getTemplateDir("season-detail");
	}
	
	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_PRODUCER + "')")
	@GetMapping("/view/add-serial")
	public String addSerialView(Model model) {
		
		return getTemplateDir("add-serial");
	}
	
	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_PRODUCER + "')")
	@PostMapping("/view/add-serial")
	public String addSerial(@ModelAttribute SerialDTO dto, Principal principal, Model model) {
		
		if (principal == null) {
			model.addAttribute("message", "Tylko producent może dodawać seriale!");
			return getTemplateDir("add-serial");
		}

		User user = userService.findByLogin(principal.getName());

		try {
			serialService.addSerial(user, dto);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute("content", dto.getTitle());
			model.addAttribute("description", dto.getDescription());
			model.addAttribute("durationInSec", dto.getDurationInSec());
			model.addAttribute("linkToWatch", dto.getLinkToWatch());
			model.addAttribute("state", dto.getState());			
			model.addAttribute(dto);
			return getTemplateDir("add-serial");
		}
		

		model.addAttribute("message", "Serial został dodany");
		return getTemplateDir("add-serial");
	}
	@GetMapping("/view/add-season")
	public String addSeasonView(Model model) {
		return getTemplateDir("add-season");
	}
	@GetMapping("/view/add-episode")
	public String addEpisodeView(Model model) {
		return getTemplateDir("add-episode");
	}

	@GetMapping("/view/serial/{id}")
	public String serialsDetailView(@PathVariable Long id, Model model) {
		SerialElement serialElement = serialService.findById(id);
		model.addAttribute("serial", serialElement);
		return getTemplateDir("serial-detail");
	}

	@PostMapping("/view/serial/{id}")
	public String addComment(@PathVariable Long id, @ModelAttribute CommentDTO dto, Principal principal, Model model) {
		SerialElement serialElement = serialService.findById(id);
		model.addAttribute("serial", serialElement);

		if (principal == null) {
			model.addAttribute("message", "Musisz sie zalogować!");
			return getTemplateDir("serial-detail");
		}

		User user = userService.findByLogin(principal.getName());

		try {
			serialService.addComment(serialElement, user, dto);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute("content", dto.getContent());
			model.addAttribute("serial", serialElement);
			model.addAttribute(dto);
			return getTemplateDir("serial-detail");
		}

		model.addAttribute("message", "Komentarz zostanie dodany po zatwierdzeniu przez adminiastatora");
		return getTemplateDir("serial-detail");
	}
}
