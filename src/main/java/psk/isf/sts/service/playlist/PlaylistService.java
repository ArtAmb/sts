package psk.isf.sts.service.playlist;

import java.util.Collection;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Playlist;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.PlaylistRepository;
import psk.isf.sts.service.playlist.dto.PlaylistDTO;

@Service
public class PlaylistService {

	@Autowired
	private PlaylistRepository playlistRepo;

	public Collection<Playlist> allMyPlaylist() {
		return playlistRepo.findAll();
	}

	public Playlist findById(Long id) {
		return playlistRepo.findOne(id);
	}

	public Playlist addPlaylist(User user, PlaylistDTO dto) throws Exception {

		Playlist playlist = Playlist.builder().name(dto.getName()).user(user).build();

		validatePlaylist(dto);

		return playlistRepo.save(playlist);
	}

	public void deletePlaylist(User user, Long id) throws IllegalAccessException {

		Playlist playlistOptional = playlistRepo.findOne(id);
		if (playlistOptional == null)
			return;

		Playlist playlist = playlistOptional;

		if (user.getId() != playlist.getUser().getId()) {
			throw new IllegalAccessException("Nie masz uprawnien do usuniece tej playlisty");
		}

		playlistRepo.delete(playlist);
	}

	public void validatePlaylist(PlaylistDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getName())) {
			throw new Exception("Musisz podać nazwę playlisty");
		}
	}
}
