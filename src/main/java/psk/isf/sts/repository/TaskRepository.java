package psk.isf.sts.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Task;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

}