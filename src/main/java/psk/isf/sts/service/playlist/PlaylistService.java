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
	
	public void validatePlaylist(PlaylistDTO dto) throws Exception {
		
		if (StringUtils.isNullOrEmpty(dto.getName())) {
			throw new Exception("Musisz podać nazwę playlisty");
		}
	}

	@Transactional
	public void addNewPlaylistElement(Playlist playlist, SerialElement serialEl) {
		
		List<PlaylistElement> elements = playlist.getElements();

		PlaylistElement lastElement = null;
		if (!elements.isEmpty())
			lastElement = elements.get(elements.size() - 1);

		PlaylistElement newElement = PlaylistElement.builder().next(null).previous(lastElement).serialElement(serialEl).build();
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

	@Transactional
	public void deletePlaylistElement(Long playlistElId) throws IllegalAccessException {

		PlaylistElement deletedPlaylistelement = playlistElementRepo.findOne(playlistElId);
		if (deletedPlaylistelement == null)
			return;

		PlaylistElement deletedPrev = deletedPlaylistelement.getPrevious();
		PlaylistElement deletedNext = deletedPlaylistelement.getNext();

		if (deletedPrev == null && deletedNext != null) {
			deletedNext.setPrevious(null);
			playlistElementRepo.save(deletedNext);
		}
		if (deletedPrev != null && deletedNext == null) {
			deletedPrev.setNext(null);
			playlistElementRepo.save(deletedPrev);
		}
		if (deletedPrev != null && deletedNext != null) {
			deletedPrev.setNext(deletedNext);
			deletedNext.setPrevious(deletedPrev);

			playlistElementRepo.save(deletedPrev);
			playlistElementRepo.save(deletedNext);
		}

		deletedPlaylistelement.setNext(null);
		deletedPlaylistelement.setPrevious(null);

		playlistElementRepo.delete(deletedPlaylistelement);
	}

	
	@Transactional
	public void upPlaylistElement(Long playlistElId) throws IllegalAccessException {

		PlaylistElement clicked = playlistElementRepo.findOne(playlistElId); //element który kliknieto
		if (clicked == null)
			return;

		PlaylistElement clickedPrev = clicked.getPrevious(); //poprzedni kliknietego
		PlaylistElement clickedNext = clicked.getNext(); //nastepny kliknietego
		
		PlaylistElement clickedPrevPrev = null;
		if(clickedPrev != null)
			clickedPrevPrev = clickedPrev.getPrevious(); //poprzedni 2 wyzej , zmienia mu sie element next

			
		if (clickedPrev != null && clickedNext != null && clickedPrevPrev == null) { //kliknieto drugi element
			clicked.setPrevious(null);
			clicked.setNext(clickedPrev);
			
			clickedPrev.setPrevious(clicked);
			clickedPrev.setNext(clickedNext);
			
			clickedNext.setPrevious(clickedPrev);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedPrev);
			playlistElementRepo.save(clickedNext);
		}
		
		if (clickedPrev != null && clickedNext != null && clickedPrevPrev != null) { //kliknieto element co ma 2 przed sobą
			clicked.setPrevious(clickedPrevPrev);
			clicked.setNext(clickedPrev);
			
			clickedPrev.setPrevious(clicked);
			clickedPrev.setNext(clickedNext);
			
			clickedNext.setPrevious(clickedPrev);
			
			clickedPrevPrev.setNext(clicked);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedPrev);
			playlistElementRepo.save(clickedNext);
			playlistElementRepo.save(clickedPrevPrev);		
		}
		
		if (clickedPrev != null && clickedNext == null && clickedPrevPrev != null) { //kliknieto ostatni, który ma 2 miejsca przd soba
			clicked.setPrevious(clickedPrevPrev);
			clicked.setNext(clickedPrev);
			
			clickedPrev.setPrevious(clicked);
			clickedPrev.setNext(null);
			
			clickedPrevPrev.setNext(clicked);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedPrev);
			playlistElementRepo.save(clickedPrevPrev);
		}
		
		if (clickedPrev != null && clickedNext == null && clickedPrevPrev == null) { //kliknieto ostatni, który ma 1 miejsce przd soba
			clicked.setPrevious(null);
			clicked.setNext(clickedPrev);
			
			clickedPrev.setPrevious(clicked);
			clickedPrev.setNext(null);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedPrev);	
		}		
	}		
	
	
	@Transactional
	public void downPlaylistElement(Long playlistElId) throws IllegalAccessException {

		PlaylistElement clicked = playlistElementRepo.findOne(playlistElId); //element który kliknieto
		if (clicked == null)
			return;

		PlaylistElement clickedPrev = clicked.getPrevious(); //poprzedni kliknietego
		PlaylistElement clickedNext = clicked.getNext(); //nastepny kliknietego
		
		PlaylistElement clickedNextNext = null;
		if(clickedNext != null)
			clickedNextNext = clickedNext.getNext(); //poprzedni 2 wyzej , zmienia mu sie element next

			
		if (clickedPrev != null && clickedNext != null && clickedNextNext == null) { //kliknieto drugi element od dołu
			clicked.setNext(null);
			clicked.setPrevious(clickedNext);
			
			clickedNext.setNext(clicked);
			clickedNext.setPrevious(clickedPrev);
			
			clickedPrev.setNext(clickedNext);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedNext);
			playlistElementRepo.save(clickedPrev);
		}
		
		if (clickedPrev != null && clickedNext != null && clickedNextNext != null) { //kliknieto element co ma 2 x next i prev
			clicked.setNext(clickedNextNext);
			clicked.setPrevious(clickedNext);
			
			clickedNext.setNext(clicked);
			clickedNext.setPrevious(clickedPrev);
			
			clickedPrev.setNext(clickedNext);
			
			clickedNextNext.setPrevious(clicked);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedNext);
			playlistElementRepo.save(clickedPrev);
			playlistElementRepo.save(clickedNextNext);
		}
		
		if (clickedPrev == null && clickedNext != null && clickedNextNext != null) { //kliknieto pierwszy co ma 2 x next
			clicked.setNext(clickedNextNext);
			clicked.setPrevious(clickedNext);
			
			clickedNext.setNext(clicked);
			clickedNext.setPrevious(null);
			
			clickedNextNext.setPrevious(clicked);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedNext);
			playlistElementRepo.save(clickedNextNext);
		}
		
		if (clickedPrev == null && clickedNext != null && clickedNextNext == null) { //kliknieto pierwszy co ma 1 x next
			clicked.setNext(null);
			clicked.setPrevious(clickedNext);
			
			clickedNext.setNext(clicked);
			clickedNext.setPrevious(null);
			
			playlistElementRepo.save(clicked);
			playlistElementRepo.save(clickedNext);
		}		
	}		
}
