package psk.isf.sts.controller.view;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.playlist.Playlist;
import psk.isf.sts.entity.playlist.PlaylistElement;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.entity.serial.SimplePlaylistElement;
import psk.isf.sts.entity.serial.SimpleSerialElement;
import psk.isf.sts.repository.PlaylistRepository;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.playlist.PlaylistService;
import psk.isf.sts.service.playlist.dto.PlaylistDTO;
import psk.isf.sts.service.playlist.dto.PlaylistElementDTO;
import psk.isf.sts.service.series.SerialService;

@Controller
public class PlaylistController {

	@Autowired
	private PlaylistService playlistService;
	
	@Autowired
	private SerialService serialService;

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private UserService userService;

	public static String templateDirRoot = "playlist/";

	
	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	
	@GetMapping("/view/playlists")
	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_VIEWER + "')")
	public String playlistView(Model model, Principal principal) {
	
		Collection<Playlist> playlists = playlistRepository.findByUser(userService.findByLogin(principal.getName()));
		model.addAttribute("playlists", playlists);
		
		return getTemplateDir("playlists");
	}

	
	@GetMapping("/view/playlists/{id}")
	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_VIEWER + "')")
	public String playlistDetailView(@PathVariable Long id, Principal principal, Model model) {

		Playlist playlist = playlistService.findById(id);
		model.addAttribute("playlist", playlist);

		Collection<PlaylistElement> noSortedPlaylistElement = playlist.getElements();
		
		//posortowanie listy  
		PlaylistElement playlistElement = noSortedPlaylistElement.stream().filter(e->e.getPrevious() == null).findFirst().orElse(null);
		List<PlaylistElement> sortedPlaylistElements = new LinkedList<>();
		while(playlistElement != null) {
			sortedPlaylistElements.add(playlistElement);
			playlistElement = playlistElement.getNext();
		}
				
		Collection<SimplePlaylistElement> simplePlaylistElements = sortedPlaylistElements.stream().map(el -> el.toSimplePlaylistElement())
				.collect(Collectors.toList());
		model.addAttribute("playlistElements", simplePlaylistElements);

		return getTemplateDir("playlist-detail");
	}

	
	@PostMapping("/view/add-playlist")
	public String addPlaylist(@ModelAttribute PlaylistDTO dto, Principal principal, Model model) {

		if (principal == null) {
			model.addAttribute("message", "Nie udalo sie dodac playlisty!");
			return getTemplateDir("add-playlist");
		}

		User user = userService.findByLogin(principal.getName());

		try {
			playlistService.addPlaylist(user, dto);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			model.addAttribute("name", dto.getName());
			model.addAttribute(dto);
			return getTemplateDir("add-playlist");
		}

		model.addAttribute("message", "Playlista została dodana");
		return "redirect:/view/playlists/";
	}

	
	@GetMapping("/view/add-playlist")
	public String getAddPlaylistView(@ModelAttribute PlaylistDTO dto, Principal principal, Model model) {
		return getTemplateDir("add-playlist");
	}
	

	@GetMapping("/view/playlists/{id}/add-playlist-element-serial")
	public String addPlaylistElementSerialView(@PathVariable Long id, Model model) {

		Playlist playlist = playlistService.findById(id);
		model.addAttribute("playlist", playlist);

		Collection<PlaylistElement> noSortedPlaylistElement = playlist.getElements();
		
		//posortowanie listy  
		PlaylistElement playlistElement = noSortedPlaylistElement.stream().filter(e->e.getPrevious() == null).findFirst().orElse(null);
		List<PlaylistElement> sortedPlaylistElements = new LinkedList<>();
		while(playlistElement != null) {
			sortedPlaylistElements.add(playlistElement);
			playlistElement = playlistElement.getNext();
		}			 
		
		Collection<SimplePlaylistElement> simplePlaylistElements = sortedPlaylistElements.stream().map(el -> el.toSimplePlaylistElement())
				.collect(Collectors.toList());
				
		model.addAttribute("playlistElements", simplePlaylistElements);
		
		
		Collection<SimpleSerialElement> serials = serialService.allSerials().stream().map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());

		model.addAttribute("serials", serials);

		return getTemplateDir("add-playlist-element-serial");
	}
	
	
	@GetMapping("/view/playlists/{idPlaylist}/add-playlist-element-season/{idSerialElement}")
	public String addPlaylistElementSeasonView(@PathVariable Long idPlaylist, @PathVariable Long idSerialElement, Model model) {

		Playlist playlist = playlistService.findById(idPlaylist);
		model.addAttribute("playlist", playlist);

		Collection<PlaylistElement> noSortedPlaylistElement = playlist.getElements();
		
		//posortowanie listy  
		PlaylistElement playlistElement = noSortedPlaylistElement.stream().filter(e->e.getPrevious() == null).findFirst().orElse(null);
		List<PlaylistElement> sortedPlaylistElements = new LinkedList<>();
		while(playlistElement != null) {
			sortedPlaylistElements.add(playlistElement);
			playlistElement = playlistElement.getNext();
		}			 
		
		Collection<SimplePlaylistElement> simplePlaylistElements = sortedPlaylistElements.stream().map(el -> el.toSimplePlaylistElement())
				.collect(Collectors.toList());		
		model.addAttribute("playlistElements", simplePlaylistElements);
		
		SerialElement serialElement = serialService.findById(idSerialElement);
		model.addAttribute("serial", serialElement);
		Collection<SimpleSerialElement> seasons = serialService.findAllSeasonsOfSerial(serialElement).stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());
		model.addAttribute("seasons", seasons);

		return getTemplateDir("add-playlist-element-season");
	}
	
	
	@GetMapping("/view/playlists/{idPlaylist}/add-playlist-element-episode/{idSerialElement}")
	public String addPlaylistElementEpisodeView(@PathVariable Long idPlaylist, @PathVariable Long idSerialElement, Model model) {

		Playlist playlist = playlistService.findById(idPlaylist);
		model.addAttribute("playlist", playlist);

		Collection<PlaylistElement> noSortedPlaylistElement = playlist.getElements();
		
		//posortowanie listy  
		PlaylistElement playlistElement = noSortedPlaylistElement.stream().filter(e->e.getPrevious() == null).findFirst().orElse(null);
		List<PlaylistElement> sortedPlaylistElements = new LinkedList<>();
		while(playlistElement != null) {
			sortedPlaylistElements.add(playlistElement);
			playlistElement = playlistElement.getNext();
		}			 
			
		Collection<SimplePlaylistElement> simplePlaylistElements = sortedPlaylistElements.stream().map(el -> el.toSimplePlaylistElement())
				.collect(Collectors.toList());
				
		model.addAttribute("playlistElements", simplePlaylistElements);
		
		SerialElement serialElement = serialService.findById(idSerialElement);
		model.addAttribute("serial", serialElement);
		Collection<SimpleSerialElement> episodes = serialService.findAllEpisodesOfSeason(serialElement).stream()
				.map(el -> el.toSimpleSerialElement()).collect(Collectors.toList());
		model.addAttribute("episodes", episodes);

		return getTemplateDir("add-playlist-element-episode");
	}
	
	
	@PostMapping("/view/playlists/{idPlaylist}/add-playlist-element/{serialElementId}")
	public String addPlaylistElement(@PathVariable Long idPlaylist, @PathVariable Long serialElementId, @ModelAttribute PlaylistElementDTO dto, Principal principal, Model model) {
		
		SerialElement serialEl = serialService.findById(serialElementId);
		Playlist playlist = playlistRepository.findOne(idPlaylist);
		
		playlistService.addNewPlaylistElement(playlist, serialEl);
		
		return "redirect:/view/playlists/" + idPlaylist;
	}
	

	@PostMapping("/view/playlist/{id}/delete")
	public String deletePlaylist(@PathVariable Long id, Principal principal, Model model)
			throws IllegalAccessException {

		User user = userService.findByLogin(principal.getName());

		playlistService.deletePlaylist(user, id);

		Collection<Playlist> playlists = playlistRepository.findByUser(userService.findByLogin(principal.getName()));
		model.addAttribute("playlists", playlists);

		return getTemplateDir("playlists");
	}
	
	
	@PostMapping("/view/playlists/{idPlaylist}/delete/{idPlaylistElement}")
	public String deletePlaylistElement(@PathVariable Long idPlaylist, @PathVariable Long idPlaylistElement, Model model)
			throws IllegalAccessException {

		playlistService.deletePlaylistElement(idPlaylistElement);
		return "redirect:/view/playlists/" + idPlaylist;
	}
	
	
	@PostMapping("/view/playlists/{idPlaylist}/up/{idPlaylistElement}")
	public String upPlaylistElement(@PathVariable Long idPlaylist, @PathVariable Long idPlaylistElement, Model model)
			throws IllegalAccessException {

		playlistService.upPlaylistElement(idPlaylistElement);
		return "redirect:/view/playlists/" + idPlaylist;
	}
	
	
	@PostMapping("/view/playlists/{idPlaylist}/down/{idPlaylistElement}")
	public String downPlaylistElement(@PathVariable Long idPlaylist, @PathVariable Long idPlaylistElement, Model model)
			throws IllegalAccessException {

		playlistService.downPlaylistElement(idPlaylistElement);
		return "redirect:/view/playlists/" + idPlaylist;
	}
}
