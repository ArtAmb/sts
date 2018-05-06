package psk.isf.sts.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Contract;
import psk.isf.sts.entity.Task;
import psk.isf.sts.entity.TaskState;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

	Collection<Task> findByContractAndState(Contract cotract, TaskState state);

	Page<Task> findByStateIn(Collection<TaskState> states, Pageable page);
}