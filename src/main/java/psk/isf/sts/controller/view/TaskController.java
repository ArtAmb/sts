package psk.isf.sts.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.ContractState;
import psk.isf.sts.entity.Task;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.TaskRepository;
import psk.isf.sts.service.TaskService;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.datatable.TaskDatatableService;
import psk.isf.sts.service.email.EmailService;
import psk.isf.sts.service.general.dto.PageDTO;

@Controller
public class TaskController {

	@Autowired
	private TaskDatatableService taskDatatableService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private TaskService taskService;

	public static String templateDirRoot = "tasks/";

	@Autowired
	private EmailService emailService;

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@GetMapping("/view/tasks/table")
	public String findAll(@ModelAttribute PageDTO page, Model model) {
		taskDatatableService.findAll(page, model, taskRepository);
		return getTemplateDir("tasks-table");
	}

	@GetMapping("/view/task/{taskId}")
	public String taskDetailView(@PathVariable Long taskId, Model model) {
		Task task = taskRepository.findOne(taskId);
		model.addAttribute("detailInfo", task);
		model.addAttribute("contractPath", task.getContract() != null ? task.getContract().toURL() : null);
		return getTemplateDir("task-detail");
	}

	@PostMapping("/view/task/{taskId}/acceptContract")
	public String taskAcceptContract(@PathVariable Long taskId, Model model) {
		Task task = taskRepository.findOne(taskId);
		if (task.getContract() == null || !task.getContract().getState().equals(ContractState.NEW)) {
			throw new IllegalStateException("To zadanie nie posiada umowy lub umowa został już akceptowana");
		}

		// User user = task.getContract().getProducer();
		// String rawPassword = userService.activateProducerAccount(user);
		// emailService.sendContractConfirmationEmail(user, rawPassword);
		taskService.processTaskWithContract(task);

		model.addAttribute("detailInfo", task);
		model.addAttribute("contractPath", task.getContract() != null ? task.getContract().toURL() : null);

		return findAll(new PageDTO(0, 10), model);
	}

	@PostMapping("/view/task/{taskId}/rejectContract")
	public String taskRejectContract(@PathVariable Long taskId, Model model) {
		Task task = taskRepository.findOne(taskId);
		if (task.getContract() == null || !task.getContract().getState().equals(ContractState.NEW)) {
			throw new IllegalStateException("To zadanie nie posiada umowy lub umowa został już akceptowana");
		}

		User user = task.getContract().getProducer();
		emailService.sendContractRejectionEmail(user);
		taskService.closeTaskWithRejectedContract(task);
		userService.removeNotRegisteredProducerAccount(user, task.getContract());

		return findAll(new PageDTO(0, 10), model);
	}

}
