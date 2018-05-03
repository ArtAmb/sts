package psk.isf.sts.service.email;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import psk.isf.sts.service.email.dto.EmailDTO;

@Service
public class EmailService {

	@Value("${sts.email}")
	private String email;
	@Value("${sts.email.password}")
	private String emialPassword;

	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.wp.com");
		mailSender.setPort(587);

		mailSender.setUsername(email);
		mailSender.setPassword(emialPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

	public void sendSimpleMessage(EmailDTO emailDTO) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(emailDTO.getTo());
		message.setSubject(emailDTO.getSubject());
		message.setText(emailDTO.getContent());
		getJavaMailSender().send(message);
	}
}
