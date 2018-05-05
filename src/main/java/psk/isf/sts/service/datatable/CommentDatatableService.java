package psk.isf.sts.service.datatable;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Comment;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.service.general.dto.PageDTO;
import psk.isf.sts.service.general.dto.Row;

@Service
public class CommentDatatableService extends AbstractDatatableService<Comment> {
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Override
	protected Page<Comment> findElements(PageDTO page, PagingAndSortingRepository<Comment, Long> repository) {
		return commentRepository.findAllByAcceptedIsFalse(PageRequest.of(page.getStart(), page.getHowMany()));
	}
	
	@Override
	public Row prepareOneRow(Comment comment) {
		List<String> values = Arrays.asList(
				toValue(comment.getDate()),
				toValue(comment.getUser().getLogin()),
				toValue(comment.getSerialElement().getElementType()),
				toValue(comment.getSerialElement().getTitle()));

		return Row.builder()
				.id(comment.getId())
				.detailPath("/view/comment/" + comment.getId())
				.values(values)
				.build();
	}

	@Override
	public List<String> getHeaders() {
		return Arrays.asList("Data", "Użytkownik", "Typ", "Tytuł");
	}
}
