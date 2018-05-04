package psk.isf.sts.service.series;


import java.util.Collection;
import java.sql.Timestamp;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.repository.SerialRepository;

import psk.isf.sts.service.series.dto.CommentDTO;

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
	

	public void addComment(SerialElement serialElement, User user, CommentDTO dto) throws Exception {
		validate(dto);
		
		Comment comment = new Comment();
		
		comment.setContent(dto.getContent());
		comment.setDate(new Timestamp(System.currentTimeMillis()));
		comment.setUser(user);
		comment.setSerialElement(serialElement);
		
		commentRepo.save(comment);
		

	}
	
	public void validate(CommentDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getContent())) {
			throw new Exception("Komentarz nie może być pusty!");
		}
	

	}
	
}
