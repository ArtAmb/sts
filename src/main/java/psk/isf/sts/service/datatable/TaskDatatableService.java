package psk.isf.sts.service.datatable;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Task;
import psk.isf.sts.entity.TaskState;
import psk.isf.sts.repository.TaskRepository;
import psk.isf.sts.service.general.dto.PageDTO;
import psk.isf.sts.service.general.dto.Row;

@Service
public class TaskDatatableService extends AbstractDatatableService<Task> {

	@Autowired
	private TaskRepository taskRepository;
	
	@Override
	public Row prepareOneRow(Task task) {
		List<String> values = Arrays.asList(
				toValue(task.getType() != null ? task.getType().toDisplayName() : ""),
				toValue(task.getState() != null ? task.getState().toDisplayName() : ""), 
				toValue(task.getDate()),
				task.getProducer() != null ? toValue(task.getProducer().getCompanyName()) : "");

		return Row.builder()
				.id(task.getId())
				.detailPath("/view/task/" + task.getId())
				.values(values)
				.build();
	}

	@Override
	protected Page<Task> findElements(PageDTO page, PagingAndSortingRepository<Task, Long> repository) {
		return taskRepository.findByStateIn(Arrays.asList(TaskState.NEW, TaskState.IN_PROGRESS), PageRequest.of(page.getStart(), page.getHowMany()));
	}
	
	@Override
	public List<String> getHeaders() {
		return Arrays.asList("Typ", "Stan", "Data", "Producent");
	}
}
