package psk.isf.sts.service.series;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SerialElementType;
import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.service.series.dto.CommentDTO;
import psk.isf.sts.service.series.dto.SerialDTO;

@Service
public class SerialService {

	@Autowired
	private SerialRepository serialRepo;

	public Collection<SerialElement> allSerials() {
		return (Collection<SerialElement>) serialRepo.findAll();
	}
	
	
	public SerialElement findById(Long id) {
		return serialRepo.findById(id).get();
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

	public void validate(CommentDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getContent())) {
			throw new Exception("Komentarz nie może być pusty!");
		}

	}
	
	public SerialElement addSerial(User user, SerialDTO dto) throws Exception {
		validate(dto);
		
			
		SerialElement serial =SerialElement.builder()
				.title(dto.getTitle())
				.description(dto.getDescription())
				.state(dto.getState())
				.durationInSec(dto.getDurationInSec())
				.linkToWatch(dto.getLinkToWatch())
				.active(true)
				.elementType(SerialElementType.SERIAL)
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
		

	}

}
