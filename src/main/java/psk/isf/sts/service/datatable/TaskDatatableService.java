package psk.isf.sts.service.datatable;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Task;
import psk.isf.sts.service.general.dto.Row;

@Service
public class TaskDatatableService extends AbstractDatatableService<Task> {

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
	public List<String> getHeaders() {
		return Arrays.asList("Typ", "Stan", "Data", "Producent");
	}
}
