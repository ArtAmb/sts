package psk.isf.sts.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.serial.SerialElement;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {

	Page<Comment> findAllByAcceptedIsFalse(Pageable pageable);

	// List<Comment> findAllByAcceptedIsTrueAndSort(Sort sort);

	List<Comment> findAllBySerialElementAndAcceptedIsTrueOrderByDateAsc(SerialElement serialElement);
}
