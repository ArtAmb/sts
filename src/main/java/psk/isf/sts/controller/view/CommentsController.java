package psk.isf.sts.controller.view;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.repository.CommentRepository;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.datatable.CommentDatatableService;
import psk.isf.sts.service.general.dto.PageDTO;

@Controller
public class CommentsController {

	public static String templateDirRoot = "comments/";

	private String getTemplateDir(String templateName) {
		return templateDirRoot + templateName;
	}

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CommentDatatableService commentDatatableService;

	@GetMapping("/view/comments/table")
	public String getDatatableView(@ModelAttribute PageDTO page, Model model) {
		commentDatatableService.findAll(page, model, commentRepository);
		return getTemplateDir("comments-table");
	}

	@GetMapping("/view/comment/{commentId}")
	public String getDetailView(@PathVariable Long commentId, Model model) {
		Comment comment = commentRepository.findOne(commentId);
		model.addAttribute("detailInfo", comment);
		return getTemplateDir("comment-detail");
	}

	@PostMapping("/view/comment/{commentId}")
	@PreAuthorize("hasRole('" + Roles.Consts.ROLE_ADMIN + "')")
	public String acceptComment(@ModelAttribute Comment dto, Principal principal, @PathVariable Long commentId,
			Model model) {
		Comment comment = commentRepository.findOne(commentId);
		comment.setContent(dto.getContent());
		comment.setLastEditUser(userService.findByLogin(principal.getName()));
		comment.setAccepted(true);
		model.addAttribute("detailInfo", commentRepository.save(comment));
		return getTemplateDir("comment-detail");
	}

	@PostMapping("/view/comment/{commentId}/decline")
	public String rejectComment(@ModelAttribute Comment dto, @PathVariable Long commentId, Model model) {
		Comment comment = commentRepository.findOne(commentId);
		comment.setAccepted(false);
		comment.setRejectionCause(dto.getRejectionCause());

		model.addAttribute("detailInfo", commentRepository.save(comment));
		return getTemplateDir("comment-detail");
	}

}
