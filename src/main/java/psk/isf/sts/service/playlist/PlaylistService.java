package psk.isf.sts.service.playlist;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Playlist;
import psk.isf.sts.entity.PlaylistElement;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.PlaylistElementRepository;
import psk.isf.sts.repository.PlaylistRepository;
import psk.isf.sts.service.playlist.dto.PlaylistDTO;

@Service
public class PlaylistService {

	@Autowired
	private PlaylistRepository playlistRepo;

	@Autowired
	private PlaylistElementRepository playlistElementRepo;

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

	@Transactional
	public void addNewPlaylistElement(Playlist playlist, SerialElement serialEl) {

		List<PlaylistElement> elements = playlist.getElements();

		PlaylistElement lastElement = null;
		if (!elements.isEmpty())
			lastElement = elements.get(elements.size() - 1);

		PlaylistElement newElement = PlaylistElement.builder().next(null).previous(lastElement).serialElement(serialEl)
				.build();
		newElement = playlistElementRepo.save(newElement);

		if (lastElement != null)
			lastElement.setNext(newElement);

		elements.add(newElement);
		playlistRepo.save(playlist);
	}

	public void deletePlaylist(User user, Long id) throws IllegalAccessException {

		Playlist playlist = playlistRepo.findOne(id);
		if (playlist == null)
			return;

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

	@Transactional
	public void deletePlaylistElement(Long playlistElId, Long playlistId) throws IllegalAccessException {

		PlaylistElement deletedPlaylistelement = playlistElementRepo.findOne(playlistElId);
		if (deletedPlaylistelement == null)
			return;

		PlaylistElement deletedPrev = deletedPlaylistelement.getPrevious();
		PlaylistElement deletedNext = deletedPlaylistelement.getNext();

		// Playlist playlist = playlistRepo.findOne(playlistId);

		if (deletedPrev == null && deletedNext != null) {
			deletedNext.setPrevious(null);
		}
		if (deletedPrev != null && deletedNext == null) {
			deletedPrev.setNext(null);
		}
		if (deletedPrev != null && deletedNext != null) {
			deletedPrev.setNext(deletedNext);
			deletedNext.setPrevious(deletedPrev);

			playlistElementRepo.save(deletedPrev);
			playlistElementRepo.save(deletedNext);
		}
		if (deletedPrev == null && deletedNext == null) {
			// playlist.setElements(null);
		}

		deletedPlaylistelement.setNext(null);
		deletedPlaylistelement.setPrevious(null);

		playlistElementRepo.delete(deletedPlaylistelement);
	}

}
