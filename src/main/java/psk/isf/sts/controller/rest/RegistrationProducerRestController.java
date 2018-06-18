package psk.isf.sts.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.task.Task;
import psk.isf.sts.service.TaskService;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.email.EmailService;

@RestController
public class RegistrationProducerRestController {

	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private TaskService taskService;

	@PostMapping("/sts/task/sign/contract")
	public void activateProducerAccountAndCloseTask(@RequestBody Task task) {
		User user = task.getContract().getProducer();
		String rawPassword = userService.activateProducerAccount(user);
		emailService.sendContractConfirmationEmail(user, rawPassword);
		taskService.closeTaskWithSignedContract(task);
	}
}
