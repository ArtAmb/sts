package psk.isf.sts.controller.view;

import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.Playlist;
import psk.isf.sts.entity.PlaylistElement;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.playlist.PlaylistService;
import psk.isf.sts.service.playlist.dto.PlaylistDTO;

@Controller
public class PlaylistController {
	
	@Autowired
	private PlaylistService playlistService;
	
	@Autowired
	private UserService userService;
	
	public static String templateDirRoot = "playlist/";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}
	  
	@GetMapping("/view/playlists")
	public String playlistView(Model model, Principal principal) {

		Collection<Playlist> playlists = playlistService.allMyPlaylist().stream().filter(w->w.getUser().getLogin().equals(principal.getName())).collect(Collectors.toList());

		model.addAttribute("playlists", playlists);
		return getTemplateDir("playlists"); 
	}
	
	
	
	
	@GetMapping("/view/playlists/{id}")
	public String playlistDetailView(@PathVariable Long id, Principal principal, Model model) {

		Playlist playlist = playlistService.findById(id);
		//Collection<Playlist> myPlaylist = playlistService.allMyPlaylist();
		//Collection<Playlist> playlists = playlistService.allMyPlaylist().stream().filter(w->w.getUser().getLogin().equals(principal.getName())).collect(Collectors.toList());
		model.addAttribute("playlist", playlist);

		Collection<PlaylistElement> playlistElements = playlist.getElements();
		model.addAttribute("playlistElements", playlistElements);

		if (principal == null) {
			return getTemplateDir("playlist-detail");
		}

		return getTemplateDir("playlist-detail");
	}
	
	
	@GetMapping("/view/add-playlist")
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

		model.addAttribute("message", "Serial został dodany");
		return getTemplateDir("add-playlist");
	}
}
