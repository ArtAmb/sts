package psk.isf.sts.controller.view;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.MySerial;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.SimpleSerialElement;
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
		
		
    Collection<SimpleSerialElement> serials = serialService.allSerials()
      .stream()
      .map(el->el.toSimpleSerialElement())
      .collect(Collectors.toList()); 
    
        
    model.addAttribute("serials", serials);
    return getTemplateDir("serials");
	}
	
	@GetMapping("/view/my-serials")
	public String mySerialsView(Model model, Principal principal) {
		Collection<MySerial> mySerials = serialService.allMySerials();
		Collection<SerialElement> serials = new LinkedList<>();
		User user;
		for(MySerial element : mySerials)
		{
			user = element.getUser();
			if((user.getLogin().equals(principal.getName())));
			{
				 serials.add(element.getSerial());
			}
		}
		Collection<SimpleSerialElement> serials2 = serials
			      .stream()
			      .map(el->el.toSimpleSerialElement())
			      .collect(Collectors.toList()); 
		model.addAttribute("serials", serials2);
		return getTemplateDir("my-serials");
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
	
	@GetMapping("/serial/addToMine/{id}")
	public String addToMine(@PathVariable Long id, Principal principal, Model model) {
		SerialElement serialElement = serialService.findById(id);
		Collection<MySerial> mySerials = serialService.allMySerials();
		model.addAttribute("serial", serialElement);
		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());

		if (principal == null) {
			model.addAttribute("message2", "Musisz sie zalogować!");
			return getTemplateDir("serial-detail");
		}
		
		User user = userService.findByLogin(principal.getName());
		User user2;
		SerialElement serial2;
		for(MySerial element : mySerials)
		{
			user2 = element.getUser();
			serial2 = element.getSerial();
			if(user2.getLogin().equals(principal.getName()));
			{
				if((serial2.getId().equals(id)))
				{
				model.addAttribute("message2", "Ten serial już został dodany wczesniej!");
				return getTemplateDir("serial-detail");
				}
				
			}
		}
		
		try {
			serialService.addToMine(serialElement, user);
		} catch (Exception e) {
			model.addAttribute("message2", e.getMessage());
			model.addAttribute("serial", serialElement);
			return getTemplateDir("serial-detail");
		}

		model.addAttribute("message2", "Dodano !");
		return getTemplateDir("serial-detail");
	}
	
	@GetMapping("/view/serial/{id}")
	public String serialsDetailView(@PathVariable Long id, Principal principal, Model model) {
		boolean czyDodano = false;
		SerialElement serialElement = serialService.findById(id);
		Collection<MySerial> mySerials = serialService.allMySerials();
		model.addAttribute("serial", serialElement);
		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());
		if (principal == null) {
			return getTemplateDir("serial-detail");
		}
		
		User user2;
		SerialElement serial2;
		for(MySerial element : mySerials)
		{
			user2 = element.getUser();
			serial2 = element.getSerial();
			if(user2.getLogin().equals(principal.getName()));
			{
				if((serial2.getId().equals(id)))
				{
				czyDodano = true;
				}
				
			}
		}
		model.addAttribute("czyDodano", czyDodano);
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
