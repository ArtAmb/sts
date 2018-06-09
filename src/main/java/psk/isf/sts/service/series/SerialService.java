package psk.isf.sts.service.series;

import java.sql.Timestamp;
import java.util.Collection;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.Genre;
import psk.isf.sts.entity.MySerial;
import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SerialElementType;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.ActorRepository;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.repository.GenreRepository;
import psk.isf.sts.repository.MySerialRepository;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.service.PictureService;
import psk.isf.sts.service.series.dto.ActorDTO;
import psk.isf.sts.service.series.dto.CommentDTO;
import psk.isf.sts.service.series.dto.EpisodeDTO;
import psk.isf.sts.service.series.dto.SeasonDTO;
import psk.isf.sts.service.series.dto.SerialDTO;

@Service
public class SerialService {

	@Autowired
	private SerialRepository serialRepo;

	@Autowired
	private GenreRepository genreRepo;

	@Autowired
	private MySerialRepository mySerialRepo;

	@Autowired
	private ActorRepository actorRepo;

	public Collection<SerialElement> allSerialsElements() {
		return (Collection<SerialElement>) serialRepo.findAll();
	}

	public Collection<SerialElement> allSerials() {
		return serialRepo.findByParentAndElementType(null, SerialElementType.SERIAL);
	}

	public Collection<MySerial> allMySerials() {
		return (Collection<MySerial>) mySerialRepo.findAll();
	}

	public Collection<SerialElement> findAllEpisodesOfSeason(SerialElement season) {
		if (!season.getElementType().equals(SerialElementType.SEASON))
			throw new IllegalStateException("Przekazany element nie jest sezonem");

		return serialRepo.findByParentAndElementType(season, SerialElementType.EPISODE);
	}

	public Collection<SerialElement> findAllSeasonsOfSerial(SerialElement serial) {
		if (!serial.getElementType().equals(SerialElementType.SERIAL))
			throw new IllegalStateException("Przekazany element nie jest serialem");

		return serialRepo.findByParentAndElementType(serial, SerialElementType.SEASON);
	}

	public SerialElement findById(Long id) {
		return serialRepo.findOne(id);
	}

	public MySerial findMySerialById(Long id) {
		return mySerialRepo.findOne(id);
	}

	@Autowired
	private CommentRepository commentRepo;

	public Comment addComment(SerialElement serialElement, User user, CommentDTO dto) throws Exception {
		validate(dto);

		Comment comment = new Comment();

		comment.setContent(dto.getContent());
		comment.setDate(new Timestamp(System.currentTimeMillis()));
		comment.setUser(user);
		comment.setSerialElement(serialElement);

		return commentRepo.save(comment);
	}

	public void addToMine(SerialElement serialElement, User user) {
		mySerialRepo.save(MySerial.builder().user(user).serial(serialElement).build());
	}

	public void deleteFromMine(Long id) {
		mySerialRepo.delete(id);
	}

	public void validate(CommentDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getContent())) {
			throw new Exception("Komentarz nie może być pusty!");
		}

	}

	@Autowired
	private PictureService pictureService;

	public SerialElement addSerial(User user, SerialDTO dto, String login, MultipartFile thumbnail) throws Exception {
		validate(dto);
		Picture picture = null;
		if (StringUtils.isNullOrEmpty(thumbnail.getOriginalFilename())) {
			picture = pictureService.findNoPhotoPicture();

		} else {
			picture = pictureService.savePicture(login, thumbnail);
		}

		SerialElement serial = SerialElement.builder().title(dto.getTitle()).description(dto.getDescription())
				.state(dto.getState()).durationInSec(dto.getDurationInSec()).linkToWatch(dto.getLinkToWatch())
				.active(true).elementType(SerialElementType.SERIAL).genres(dto.getGenres()).thumbnail(picture)
				.producer(user).build();

		return serialRepo.save(serial);
	}

	public void validate(SerialDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getTitle())) {
			throw new Exception("Tytuł nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getDescription())) {
			throw new Exception("Opis nie może być pusty!");
		}
		if (dto.getGenres() == null || dto.getGenres().isEmpty()) {
			throw new Exception("Wybierz jakiś gatunek!");
		}
		if (dto.getState() == null) {
			throw new Exception("Status nie może być pusty!");
		}

	
	}

	public Collection<Genre> allGenres() {
		return (Collection<Genre>) genreRepo.findAll();
	}

	public SerialElement addSeason(User user, SeasonDTO dto, SerialElement parentElement, String login,
			MultipartFile thumbnail) throws Exception {
		validate(dto);

		
		Picture picture = null;
		if (StringUtils.isNullOrEmpty(thumbnail.getOriginalFilename())) {
			picture = pictureService.findNoPhotoPicture();

		} else {
			picture = pictureService.savePicture(login, thumbnail);
		}

		SerialElement season = SerialElement.builder().title(dto.getTitle()).description(dto.getDescription())
				.active(true).elementType(SerialElementType.SEASON).parent(parentElement).thumbnail(picture)
				.producer(user).build();
		
		

		return serialRepo.save(season);
	}

	public void validate(SeasonDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getTitle())) {
			throw new Exception("Tytuł nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getDescription())) {
			throw new Exception("Opis nie może być pusty!");
		}

		

	}

	public SerialElement addEpisode(User user, EpisodeDTO dto, SerialElement parentElement, String login,
			MultipartFile thumbnail) throws Exception {

		validate(dto);

		Picture picture = null;
		if (StringUtils.isNullOrEmpty(thumbnail.getOriginalFilename())) {
			picture = pictureService.findNoPhotoPicture();

		} else {
			picture = pictureService.savePicture(login, thumbnail);
		}

		SerialElement episode = SerialElement.builder().title(dto.getTitle()).description(dto.getDescription())
				.active(true).elementType(SerialElementType.EPISODE).parent(parentElement).thumbnail(picture)
				.producer(user).startDate(dto.getStartDate()).build();

		return serialRepo.save(episode);
	}

	public void validate(EpisodeDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getTitle())) {
			throw new Exception("Tytuł nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getDescription())) {
			throw new Exception("Opis nie może być pusty!");
		}

		// if (StringUtils.isNullOrEmpty(dto.getPicture().getOriginalFilename())) {
		// throw new Exception("Błąd dodawania zdjęcia!");
		// }

	}

	public Actor addActor(User user, ActorDTO dto, SerialElement parentElement, String login, MultipartFile thumbnail,
			Collection<Actor> actors) throws Exception {

		validate(dto);

		Picture picture = null;
		if (StringUtils.isNullOrEmpty(thumbnail.getOriginalFilename())) {
			picture = pictureService.findNoPhotoPicture();

		} else {
			picture = pictureService.savePicture(login, thumbnail);
		}

		Actor actor = Actor.builder().name(dto.getName()).surname(dto.getSurname()).thumbnail(picture).build();

		actors.add(actor);
		return actorRepo.save(actor);
	}

	public void validate(ActorDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getName())) {
			throw new Exception("Imie nie może być puste!");
		}
		if (StringUtils.isNullOrEmpty(dto.getSurname())) {
			throw new Exception("Nazwisko nie może być puste!");
		}
	}

	public void deleteActor(User user, Long id) throws IllegalAccessException {

		Actor actor = actorRepo.findOne(id);
		if (actor == null)
			return;
		
		actorRepo.delete(actor);
	}

	public void deleteSerialElement(User user, Long id) throws IllegalAccessException {

		SerialElement element = serialRepo.findOne(id);
		
		if (element == null)
			return;
		
		serialRepo.delete(element);
	}
	
	public void checkIfMine(SerialElement serialElement, User user) throws IllegalAccessException
	{
		if (user.getId() != serialElement.getProducer().getId()) {
			throw new IllegalAccessException("Nie masz uprawnień do zarządzania tym serialem!");
		}
	}

}