package psk.isf.sts.controller.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import psk.isf.sts.entity.contract.Contract;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.task.Task;
import psk.isf.sts.entity.task.TaskState;
import psk.isf.sts.entity.task.TaskType;
import psk.isf.sts.repository.ContractRepository;
import psk.isf.sts.repository.TaskRepository;
import psk.isf.sts.service.contract.ContractService;
import psk.isf.sts.service.integration.dto.SendContractByCourierDTO;
import psk.isf.sts.service.integration.dto.SendContractByCourierResponse;

@RestController
public class StsIntegrationController {

	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ContractRepository contractRepo;
	@Autowired
	private ContractService contractService;

	@Value("${sts.self.url}")
	private String stsUrl;

	@PostMapping("/sts/facade/send-contract-by-courier")
	public void sendContractByCourier(@RequestBody Contract contract) throws FileNotFoundException, IOException {

		User user = contract.getProducer();
		SendContractByCourierDTO dto = SendContractByCourierDTO.builder().contractId(contract.getId())
				.address(user.getAddress()).phoneNumber(user.getPhoneNumber()).companyName(user.getCompanyName())
				// .contractPdf(contractService.contractToMultipartFile(contract))
				.build();

		new RestTemplate().postForObject(stsUrl + "/courier-company/facade/send-contract-by-courier", dto,
				String.class);

	}

	void tmp(Contract contract) {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("contract", new ClassPathResource(contract.getPath()));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
				map, headers);
		new RestTemplate().exchange(stsUrl + "/courier-company/facade/send-contract-by-courier", HttpMethod.POST,
				requestEntity, String.class);
	}

	@PostMapping("/sts/facade/signContract")
	public void contractWasRecievedAndSigned(@RequestBody SendContractByCourierResponse dto) {
		Contract contract = contractRepo.findOne(dto.getContractId());
		Collection<Task> tasks = taskRepository.findByContractAndState(contract, TaskState.IN_PROGRESS);

		if (tasks.size() != 1) {
			Task task = Task.builder().contract(contract).date(new Timestamp(System.currentTimeMillis()))
					.state(TaskState.NEW).type(TaskType.ERROR_SERVICE)
					.comments(String.format("Dla umowy[%s] znaleziono 0 lub wiecej niż 1 zadanie w progresie",
							contract.getId()))
					.build();

			taskRepository.save(task);
			return;
		}

		Task task = tasks.stream().findFirst().get();

		new RestTemplate().postForObject(stsUrl + "/sts/task/sign/contract", task, Void.class);

	}

}
