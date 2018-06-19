package psk.isf.sts.service.series;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.myserial.MySerial;
import psk.isf.sts.entity.myserial.MySerialConfig;
import psk.isf.sts.entity.picture.Picture;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.Genre;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.entity.serial.SerialElementType;
import psk.isf.sts.entity.serial.State;
import psk.isf.sts.repository.ActorRepository;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.repository.myserial.MySerialConfigRepository;
import psk.isf.sts.repository.myserial.MySerialRepository;
import psk.isf.sts.repository.serial.GenreRepository;
import psk.isf.sts.repository.serial.SerialRepository;
import psk.isf.sts.service.PictureService;
import psk.isf.sts.service.cache.serial.SerialCacheService;
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
	private MySerialConfigRepository mySerialConfigRepo;

	@Autowired
	private ActorRepository actorRepo;

	@Autowired
	private CommentRepository commentRepo;

	@Autowired
	private PictureService pictureService;

	@Autowired
	private SerialCacheService serialCacheService;

	@Value("${serial.cache.enabled}")
	private Boolean isCacheEnabled;

	public Collection<SerialElement> allSerialsElements() {
		return (Collection<SerialElement>) serialRepo.findAll();
	}

	public Collection<SerialElement> allSerials() {
		return serialRepo.findByParentAndElementType(null, SerialElementType.SERIAL);
	}

	public Collection<MySerial> allMySerials() {
		return (Collection<MySerial>) mySerialRepo.findAll();
	}

	public Collection<MySerialConfig> findMySerialsConfigByUser(User user) {
		return mySerialConfigRepo.findByUser(user);
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

	@Transactional
	public SerialElement findById(Long id) {
		if (isCacheEnabled) {
			return serialCacheService.get(id);
		}

		return serialRepo.findOne(id);
	}

	public MySerial findMySerialById(Long id) {
		return mySerialRepo.findOne(id);
	}

	public Comment addComment(SerialElement serialElement, User user, CommentDTO dto) throws Exception {
		validate(dto);

		Comment comment = new Comment();

		comment.setContent(dto.getContent());
		comment.setDate(new Timestamp(System.currentTimeMillis()));
		comment.setUser(user);
		comment.setSerialElement(serialElement);

		return commentRepo.save(comment);
	}

	@Transactional
	public void addToMine(SerialElement serialElement, User user) {
		if (!serialElement.getElementType().equals(SerialElementType.SERIAL))
			throw new IllegalStateException("Dodawnay element musi byc serialem!");

		MySerial mySerial = MySerial.builder().user(user).serial(serialElement).watched(false).build();

		MySerialConfig mySerialConfig = MySerialConfig.builder().user(user).serial(mySerial).sendNotifications(true)
				.trace(true).showDescriptions(true).build();

		mySerialConfig = mySerialConfigRepo.save(mySerialConfig);

		SerialElement serial = serialRepo.findOne(serialElement.getId());
		for (SerialElement season : serial.getElements()) {
			MySerial mySeason = MySerial.builder().user(user).serial(season).config(mySerialConfig).watched(false)
					.build();

			mySerialRepo.save(mySeason);

			for (SerialElement episode : season.getElements()) {
				MySerial myEp = MySerial.builder().user(user).serial(episode).config(mySerialConfig).watched(false)
						.build();

				mySerialRepo.save(myEp);
			}
		}

	}

	@Transactional
	public void deleteFromMine(User user, SerialElement serialElement) {
		if (!serialElement.getElementType().equals(SerialElementType.SERIAL))
			throw new IllegalStateException("serialElement musi byc serialem!");

		MySerialConfig config = mySerialConfigRepo.findByUserAndSerial_serial_id(user, serialElement.getId()).stream()
				.findFirst().orElse(null);
		if (config == null)
			throw new IllegalStateException(
					"NIE ZNALEZIONO MOJEGO SERIALU DLA serialu o id == !" + serialElement.getId());

		mySerialConfigRepo.delete(config);
	}

	public void validate(CommentDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getContent())) {
			throw new Exception("Komentarz nie może być pusty!");
		}

	}

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

		Long ordinalNumber = (long) parentElement.getElements().size();

		SerialElement season = SerialElement.builder().title(dto.getTitle()).description(dto.getDescription())
				.active(true).elementType(SerialElementType.SEASON).parent(parentElement).thumbnail(picture)
				.ordinalNumber(ordinalNumber).producer(user).build();

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

		Long ordinalNumber = (long) parentElement.getElements().size();

		SerialElement episode = SerialElement.builder().title(dto.getTitle()).description(dto.getDescription())
				.active(true).elementType(SerialElementType.EPISODE).parent(parentElement).thumbnail(picture)
				.producer(user).startDate(dto.getStartDate()).ordinalNumber(ordinalNumber).build();

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

	public void checkIfMine(SerialElement serialElement, User user) throws IllegalAccessException {
		if (user.getId() != serialElement.getProducer().getId()) {
			throw new IllegalAccessException("Nie masz uprawnień do zarządzania tym serialem!");
		}
	}

	public void addToWatched(MySerial mySerial, User user) {
		mySerialRepo.save(mySerial);
	}

	public int countProgressBar(Collection<SerialElement> allEpisodesOfSeason,
			Collection<SerialElement> watchedEpisodes) {
		float numberOfEpisodesOfSeason, numberOfWatchedEpisodes, valueOfProgress = 0;
		numberOfEpisodesOfSeason = allEpisodesOfSeason.size();
		numberOfWatchedEpisodes = watchedEpisodes.size();

		valueOfProgress = numberOfWatchedEpisodes / numberOfEpisodesOfSeason * 100;

		return (int) Math.round(valueOfProgress);

	}

	public SerialElement findNextEpisodeDate(SerialElement serial) {

		if (serial.getState() == State.FINISHED)
			return null;

		List<SerialElement> seasons = serial.getElements().stream()
				.sorted((s1, s2) -> Long.compare(s1.getId(), s2.getId())).collect(Collectors.toList());

		if (seasons.isEmpty())
			return null;

		SerialElement lastSeason = seasons.get(seasons.size() - 1);

		List<SerialElement> episodes = lastSeason.getElements().stream()
				.sorted((s1, s2) -> Long.compare(s1.getId(), s2.getId())).collect(Collectors.toList());

		if (episodes.isEmpty())
			return null;

		SerialElement lastEpisode = episodes.get(episodes.size() - 1);

		return lastEpisode;
	}

	@Transactional
	public Long countProgressForSeason(Long serialElId, User user) {
		Double watchedTime = 0.0;
		Long fullTime = 0l;

		SerialElement serailElParent = findById(serialElId);

		Collection<MySerial> mySerials = mySerialRepo.findByUserAndSerial_parent(user, serailElParent);

		for (MySerial mySerial : mySerials) {
			fullTime += mySerial.getSerial().getDurationInSec();
			if (mySerial.isWatched()) {
				watchedTime += mySerial.getSerial().getDurationInSec();
			}

		}

		if (fullTime == 0l)
			return 0l;

		return (long) (watchedTime / fullTime * 100);
	}

}
