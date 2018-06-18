package psk.isf.sts.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import psk.isf.sts.entity.contract.Contract;
import psk.isf.sts.entity.contract.ContractState;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.task.Task;
import psk.isf.sts.entity.task.TaskState;
import psk.isf.sts.entity.task.TaskType;
import psk.isf.sts.repository.ContractRepository;
import psk.isf.sts.repository.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Value("${sts.self.url}")
	private String stsFacadeUrl;

	public void addAcceptContractTask(User producer, Contract contract) {
		taskRepository.save(Task.builder().producer(producer).contract(contract).type(TaskType.ACCEPT_CONTRACT)
				.date(new Timestamp(System.currentTimeMillis())).state(TaskState.NEW).build());
	}

	public void closeTask(Task task) {
		task.setState(TaskState.CLOSED);
		taskRepository.save(task);
	}

	public void closeTaskWithSignedContract(Task task) {
		task.setState(TaskState.CLOSED);
		task.getContract().setState(ContractState.SIGNED);
		taskRepository.save(task);
	}

	public void closeTaskWithRejectedContract(Task task) {
		task.setState(TaskState.CLOSED);
		task.setContract(null);
		taskRepository.save(task);
	}

	public void processTaskWithContract(Task task) {
		new RestTemplate().postForObject(stsFacadeUrl + "/sts/facade/send-contract-by-courier", task.getContract(),
				Void.class);

		task.setState(TaskState.IN_PROGRESS);
		task.getContract().setState(ContractState.ACCEPTED);
		taskRepository.save(task);
	}

}
