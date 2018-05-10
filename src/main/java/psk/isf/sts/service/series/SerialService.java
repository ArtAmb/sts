package psk.isf.sts.service.series;

import java.sql.Timestamp;
import java.util.Collection;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.MySerial;
import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SerialElementType;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.repository.MySerialRepository;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.service.PictureService;
import psk.isf.sts.service.series.dto.CommentDTO;
import psk.isf.sts.service.series.dto.SerialDTO;

@Service
public class SerialService {

	@Autowired
	private SerialRepository serialRepo;
	
	
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
	
	public SerialElement addSerial(User user, SerialDTO dto) throws Exception {
		validate(dto);
			
		Picture picture = pictureService.saveSerialPicture(dto.getThumbnail());
			
		SerialElement serial =SerialElement.builder()
				.title(dto.getTitle())
				.description(dto.getDescription())
				.state(dto.getState())
				.durationInSec(dto.getDurationInSec())
				.linkToWatch(dto.getLinkToWatch())
				.active(true)
				.elementType(SerialElementType.SERIAL)
				.thumbnail(picture)
				.build();		
				

		return serialRepo.save(serial);
	}

	public void validate(SerialDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getTitle())) {
			throw new Exception("Tytuł nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getTitle())) {
			throw new Exception("Opis nie może być pusty!");
		}
		if (StringUtils.isNullOrEmpty(dto.getThumbnail().getOriginalFilename()))
		{
			throw new Exception("Błąd dodawania zdjęcia!");
		}
		

	}

}
