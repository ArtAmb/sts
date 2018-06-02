package psk.isf.sts.controller.view;

import java.security.Principal;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.RequestParam;

import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Genre;
import psk.isf.sts.entity.MySerial;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SimpleSerialElement;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.series.SerialService;
import psk.isf.sts.service.series.dto.CommentDTO;
import psk.isf.sts.service.series.dto.EpisodeDTO;
import psk.isf.sts.service.series.dto.SeasonDTO;
import psk.isf.sts.service.series.dto.SerialDTO;
import psk.isf.sts.service.series.mapper.ActorMapper;
import psk.isf.sts.service.series.mapper.GenreMapper;

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

		Collection<SimpleSerialElement> serials = serialService.allSerials().stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());

		model.addAttribute("serials", serials);
		return getTemplateDir("serials");
	}

	@GetMapping("/view/my-serials")
	public String mySerialsView(Model model, Principal principal) {
		Collection<MySerial> mySerials = serialService.allMySerials();
		Collection<SerialElement> serials = new LinkedList<>();
		User user;
		for (MySerial element : mySerials) {
			user = element.getUser();
			if ((user.getLogin().equals(principal.getName()))) {
				serials.add(element.getSerial());
			}
		}
		Collection<SimpleSerialElement> serials2 = serials.stream().map(el -> el.toSimpleSerialElement())
				.collect(Collectors.toList());

		model.addAttribute("serials", serials2);
		return getTemplateDir("my-serials");
	}

	@GetMapping("/view/serial/{id}/season/{id}")
	public String seasonView(@PathVariable Long id, Principal principal, Model model) {

		SerialElement serialElement = serialService.findById(id);

		model.addAttribute("serial", serialElement);
		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());

		Collection<Actor> actors = serialElement.getActors();
		model.addAttribute("actors", actors.stream().map(ActorMapper::map).collect(Collectors.toList()));

		Collection<SimpleSerialElement> episodes = serialService.findAllEpisodesOfSeason(serialElement).stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());

		model.addAttribute("episodes", episodes);

		if (principal == null) {
			return getTemplateDir("season-detail");
		}

		return getTemplateDir("season-detail");
	}

	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_PRODUCER + "')")
	@GetMapping("/view/add-serial")
	public String addSerialView(@ModelAttribute SerialDTO dto, Principal principal, Model model) {

		Collection<Genre> genres = serialService.allGenres();
		model.addAttribute("genres", genres.stream().map(GenreMapper::map).collect(Collectors.toList()));

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

		Collection<Genre> genres = serialService.allGenres();
		model.addAttribute("genres", genres.stream().map(GenreMapper::map).collect(Collectors.toList()));

		try {
			serialService.addSerial(user, dto, principal.getName(), dto.getPicture());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute(dto);
			return getTemplateDir("add-serial");
		}

		model.addAttribute("message", "Serial został dodany");
		return getTemplateDir("add-serial");
	}

	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_PRODUCER + "')")
	@GetMapping("/view/serial/{id}/add-season")
	public String addSeasonView(@PathVariable Long id, Model model) {

		SerialElement serialElement = serialService.findById(id);
		model.addAttribute("serial", serialElement);

		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());

		return getTemplateDir("add-season");
	}

	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_PRODUCER + "')")
	@PostMapping("/view/serial/{id}/add-season")
	public String addSeason(@PathVariable Long id, @ModelAttribute SeasonDTO dto, Principal principal, Model model) {

		SerialElement serialElement = serialService.findById(id);
		model.addAttribute("serial", serialElement);

		if (principal == null) {
			model.addAttribute("message", "Tylko producent może dodawać sezony!");
			return getTemplateDir("add-season");
		}

		User user = userService.findByLogin(principal.getName());

		try {
			serialService.addSeason(user, dto, serialElement, principal.getName(), dto.getPicture());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute(dto);
			return getTemplateDir("add-season");
		}

		model.addAttribute("message", "Sezon został dodany");
		return getTemplateDir("add-season");
	}

	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_PRODUCER + "')")
	@GetMapping("/view/serial/{id}/add-episode")
	public String addEpisodeView(@PathVariable Long id, Model model) {

		SerialElement serialElement = serialService.findById(id);
		model.addAttribute("serial", serialElement);

		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());

		return getTemplateDir("add-episode");
	}

	@PostMapping("/view/serial/{id}/add-episode")
	public String addEpisode(@PathVariable Long id, @ModelAttribute EpisodeDTO dto, Principal principal, Model model) {

		SerialElement serialElement = serialService.findById(id);
		model.addAttribute("serial", serialElement);

		if (principal == null) {
			model.addAttribute("message", "Tylko producent może dodawać odcinki!");
			return getTemplateDir("add-episode");
		}

		User user = userService.findByLogin(principal.getName());

		try {
			serialService.addEpisode(user, dto, serialElement, principal.getName(), dto.getPicture());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute(dto);
			return getTemplateDir("add-episode");
		}

		model.addAttribute("message", "Odcinek został dodany");
		return getTemplateDir("add-episode");
	}

	@GetMapping("/view/addToMine/{id}")
	public String addToMine(@PathVariable Long id, Principal principal, Model model,
			@RequestParam("context") String contextTemplate) {
		boolean czyDodano = false;
		SerialElement serialElement = serialService.findById(id);
		Collection<MySerial> mySerials = serialService.allMySerials();
		model.addAttribute("serial", serialElement);
		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());

		Collection<Actor> actors = serialElement.getActors();
		model.addAttribute("actors", actors.stream().map(ActorMapper::map).collect(Collectors.toList()));

		Collection<SimpleSerialElement> seasons = serialService.findAllSeasonsOfSerial(serialElement).stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());

		model.addAttribute("seasons", seasons);

		if (principal == null) {
			model.addAttribute("czyDodano", czyDodano);
			model.addAttribute("message2", "Musisz sie zalogować!");
			return getTemplateDir(contextTemplate);
		}

		User user = userService.findByLogin(principal.getName());
		User user2;
		SerialElement serial2;
		for (MySerial element : mySerials) {
			user2 = element.getUser();
			serial2 = element.getSerial();
			if (user2.getLogin().equals(principal.getName())) {
				if ((serial2.getId().equals(id))) {
					czyDodano = true;
					model.addAttribute("czyDodano", czyDodano);
					model.addAttribute("message2", "Ten serial już został dodany wczesniej!");
					model.addAttribute("mySerial", element);
					return getTemplateDir(contextTemplate);
				}

			}
		}

		try {
			serialService.addToMine(serialElement, user);
		} catch (Exception e) {
			model.addAttribute("message2", e.getMessage());
			model.addAttribute("serial", serialElement);
			return getTemplateDir(contextTemplate);
		}
		mySerials = serialService.allMySerials();
		for (MySerial element : mySerials) {
			user2 = element.getUser();
			serial2 = element.getSerial();
			if (user2.getLogin().equals(principal.getName()))
				;
			{
				if ((serial2.getId().equals(id))) {
					czyDodano = true;
					model.addAttribute("czyDodano", czyDodano);
					model.addAttribute("mySerial", element);
				}

			}
		}
		model.addAttribute("czyDodano", czyDodano);
		return getTemplateDir(contextTemplate);
	}

	@GetMapping("/view/deleteFromMine/{id}")
	public String deleteFromMine(@PathVariable Long id, Principal principal, Model model,
			@RequestParam("context") String contextTemplate) {
		boolean czyDodano = true;
		MySerial mySerial = serialService.findMySerialById(id);
		SerialElement serialElement = mySerial.getSerial();
		model.addAttribute("serial", serialElement);
		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());
		serialService.deleteFromMine(id);
		czyDodano = false;
		model.addAttribute("czyDodano", czyDodano);
		model.addAttribute("mySerial", mySerial);
		Collection<SimpleSerialElement> seasons = serialService.findAllSeasonsOfSerial(serialElement).stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());

		model.addAttribute("seasons", seasons);
		return getTemplateDir(contextTemplate);
	}

	@GetMapping("/view/serial/{id}")
	public String serialsDetailView(@PathVariable Long id, Principal principal, Model model) {
		boolean czyDodano = false;
		SerialElement serialElement = serialService.findById(id);
		Collection<MySerial> mySerials = serialService.allMySerials();
		model.addAttribute("serial", serialElement);
		model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());

		Collection<Actor> actors = serialElement.getActors();
		model.addAttribute("actors", actors.stream().map(ActorMapper::map).collect(Collectors.toList()));
		Collection<SimpleSerialElement> seasons = serialService.findAllSeasonsOfSerial(serialElement).stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());

		model.addAttribute("seasons", seasons);
		if (principal == null) {
			return getTemplateDir("serial-detail");
		}

		User user2;
		SerialElement serial2;
		for (MySerial element : mySerials) {
			user2 = element.getUser();
			serial2 = element.getSerial();
			if (user2.getLogin().equals(principal.getName())) {
				if ((serial2.getId().equals(id))) {
					czyDodano = true;
					model.addAttribute("mySerial", element);
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
		if (serialElement.getThumbnail() != null)
			model.addAttribute("thumbnailUrl", serialElement.getThumbnail().toURL());

		Collection<Actor> actors = serialElement.getActors();
		model.addAttribute("actors", actors.stream().map(ActorMapper::map).collect(Collectors.toList()));

		Collection<SimpleSerialElement> seasons = serialService.findAllSeasonsOfSerial(serialElement).stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());

		model.addAttribute("seasons", seasons);

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
		boolean czyDodano = false;

		Collection<MySerial> mySerials = serialService.allMySerials();
		User user2;
		SerialElement serial2;
		for (MySerial element : mySerials) {
			user2 = element.getUser();
			serial2 = element.getSerial();
			if (user2.getLogin().equals(principal.getName())) {
				if ((serial2.getId().equals(id))) {
					czyDodano = true;
					model.addAttribute("mySerial", element);
				}

			}
		}
		model.addAttribute("czyDodano", czyDodano);
		model.addAttribute("message", "Komentarz zostanie dodany po zatwierdzeniu przez adminiastatora");
		return getTemplateDir("serial-detail");
	}

}