package psk.isf.sts.controller.view;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.series.SerialService;
import psk.isf.sts.service.series.dto.CommentDTO;

@Controller
public class SeriesController {
	@Autowired
	private SerialService serialService;
	@Autowired	
    private UserService userService;

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

	
	@GetMapping("/view/serial/{id}")
	public String serialsDetailView(@PathVariable Long id, Model model) {
		SerialElement serialElement = serialService.findById(id); 
		model.addAttribute("serial", serialElement);
		return getTemplateDir("serial-detail");}	
	
	
	
	@PostMapping("/view/serial/{id}")
	 public String addComment(@PathVariable Long id, @ModelAttribute CommentDTO dto, Principal principal, Model model) {
		//if(principal == null)			
    	//throw new NotAuthorizedException();
	
    User user = userService.findByLogin(principal.getName()); 
    SerialElement serialElement = serialService.findById(id); 
    
		
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
