package psk.isf.sts.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.Contract;
import psk.isf.sts.entity.ContractState;
import psk.isf.sts.entity.Task;
import psk.isf.sts.entity.TaskState;
import psk.isf.sts.entity.TaskType;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.ContractRepository;
import psk.isf.sts.repository.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private TaskRepository taskRepository;

	public void addAcceptContractTask(User producer, Contract contract) {
		taskRepository.save(Task
				.builder()
				.producer(producer)
				.contract(contract)
				.type(TaskType.ACCEPT_CONTRACT)
				.date(new Timestamp(System.currentTimeMillis()))
				.state(TaskState.NEW)
				.build());
	}

}
