// package psk.isf.sts.controller.view;
//
// import org.springframework.social.connect.ConnectionRepository;
// import org.springframework.social.facebook.api.Facebook;
// import org.springframework.social.facebook.api.PagedList;
// import org.springframework.social.facebook.api.Post;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
//
// @Controller
// public class FbController {
//
// public static String templateDirRoot = "authentication/";
//
// private Facebook facebook;
// private ConnectionRepository connectionRepository;
//
// public FbController(Facebook facebook, ConnectionRepository
// connectionRepository) {
// this.facebook = facebook;
// this.connectionRepository = connectionRepository;
// }
//
// private String getTemplateDir(String templateName) {
// return templateDirRoot + templateName;
// }
//
// @GetMapping("/sign-up/fb")
// public String helloFacebook(Model model) {
// if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
// return "redirect:/connect/facebook";
// }
//
// model.addAttribute("facebookProfile",
// facebook.userOperations().getUserProfile());
// PagedList<Post> feed = facebook.feedOperations().getFeed();
// model.addAttribute("feed", feed);
// return getTemplateDir("logIn");
// }
// }
