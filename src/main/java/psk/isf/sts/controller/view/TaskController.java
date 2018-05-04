package psk.isf.sts.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import psk.isf.sts.entity.Task;
import psk.isf.sts.repository.TaskRepository;
import psk.isf.sts.service.datatable.TaskDatatableService;
import psk.isf.sts.service.general.dto.PageDTO;

@Controller
public class TaskController {

	@Autowired
	private TaskDatatableService taskDatatableService;

	@Autowired
	private TaskRepository taskRepository;

	public static String templateDirRoot = "tasks/";

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
		Task task = taskRepository.findById(taskId).get();
		model.addAttribute("detailInfo", task);
		model.addAttribute("contractPath", task.getContract() != null ? task.getContract().toURL() : null);
		return getTemplateDir("task-detail");
	}
}
