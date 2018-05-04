package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Comment;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

}
