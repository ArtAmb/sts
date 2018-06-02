package psk.isf.sts.service.series;

import java.sql.Timestamp;
import java.util.Collection;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.Genre;
import psk.isf.sts.entity.MySerial;
import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SerialElementType;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.repository.GenreRepository;
import psk.isf.sts.repository.MySerialRepository;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.service.PictureService;
import psk.isf.sts.service.series.dto.CommentDTO;
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
	
	public Collection<SerialElement> allSerials() {
		return (Collection<SerialElement>) serialRepo.findAll();
	}
	
	public Collection<MySerial> allMySerials() {
		return (Collection<MySerial>) mySerialRepo.findAll();
	}

	public SerialElement findById(Long id) {
		return serialRepo.findById(id).get();
	}
	
	public MySerial findMySerialById(Long id) {
		return mySerialRepo.findById(id).get();
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
	
	public void addToMine(SerialElement serialElement, User user)
	{
		mySerialRepo.save(MySerial.builder().user(user).serial(serialElement).build());
	}
	
	public void deleteFromMine(Long id)
	{
		mySerialRepo.deleteById(id);
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
		Picture picture = pictureService.savePicture(login, thumbnail);	
				    
			
		SerialElement serial =SerialElement.builder()
				.title(dto.getTitle())
				.description(dto.getDescription())
				.state(dto.getState())
				.durationInSec(dto.getDurationInSec())
				.linkToWatch(dto.getLinkToWatch())
				.active(true)
				.elementType(SerialElementType.SERIAL)
				.genres(dto.getGenres())
				.thumbnail(picture)
				.build();		
				

		return serialRepo.save(serial);
	}

	public void validate(SerialDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getTitle())) {
			throw new Exception("Tytuł nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getDescription())) {
			throw new Exception("Opis nie może być pusty!");
		}
		if(dto.getGenres() == null || dto.getGenres().isEmpty())
		{
			throw new Exception("Wybierz jakiś gatunek!");
		}
		if(dto.getState()==null)
		{
			throw new Exception("Status nie może być pusty!");
		}
		
		if (StringUtils.isNullOrEmpty(dto.getPicture().getOriginalFilename()))
		{
			throw new Exception("Błąd dodawania zdjęcia!");
		}
		

	}
	
	public Collection<Genre> allGenres() {
		return (Collection<Genre>) genreRepo.findAll();
	}
	
	public SerialElement addSeason(User user, SeasonDTO dto, SerialElement parentElement, String login, MultipartFile thumbnail) throws Exception {
		validate(dto);
		
		Picture picture = pictureService.savePicture(login, thumbnail);	
			
		SerialElement season =SerialElement.builder()
				.title(dto.getTitle())
				.description(dto.getDescription())
				.state(dto.getState())
				.active(true)
				.elementType(SerialElementType.SEASON)
				.parent(parentElement)
				.thumbnail(picture)
				.build();		
				
		
		return serialRepo.save(season);
	}

	public void validate(SeasonDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getTitle())) {
			throw new Exception("Tytuł nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getDescription())) {
			throw new Exception("Opis nie może być pusty!");
		}
		
		if(dto.getState()==null)
		{
			throw new Exception("Status nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getPicture().getOriginalFilename()))
		{
			throw new Exception("Błąd dodawania zdjęcia!");
		}
		

	}
	
	
	


}