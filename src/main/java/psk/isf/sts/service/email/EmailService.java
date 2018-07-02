package psk.isf.sts.service.email;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.service.email.dto.EmailDTO;

@Service
public class EmailService {

	@Value("${sts.email}")
	private String email;
	@Value("${sts.email.password}")
	private String emialPassword;

	@Value("${sts.email.stmp.url}")
	private String stmpServerUrl;

	@Value("${sts.email.stmp.port}")
	private Integer stmpPort;

	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(stmpServerUrl);
		mailSender.setPort(stmpPort);

		mailSender.setUsername(email);
		mailSender.setPassword(emialPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

	public void sendSimpleMessage(EmailDTO emailDTO) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(email);
		message.setTo(emailDTO.getTo());
		message.setSubject(emailDTO.getSubject());
		message.setText(emailDTO.getContent());
		getJavaMailSender().send(message);
	}

	public void sendContractConfirmationEmail(User user, String rawPassword) {

		EmailDTO emailDTO = EmailDTO.builder().to(user.getEmail()).subject("STS Akceptacja umowy")
				.content("Witaj " + user.getLogin() + "!\nTwoje konto zostało aktywowane.\nLogin: " + user.getLogin()
						+ "\nHasło: " + rawPassword + "\n\nMiłego śledzenia seriali :)\n			Zespół STS")
				.build();

		sendSimpleMessage(emailDTO);
	}

	public void sendContractRejectionEmail(User user) {

		EmailDTO emailDTO = EmailDTO.builder().to(user.getEmail()).subject("STS Akceptacja umowy")
				.content("Witaj " + user.getLogin() + "!\nTwoja umowa została odrzucona." + "\n\n		Zespół STS")
				.build();

		sendSimpleMessage(emailDTO);
	}

	public static void main(String[] args) {
		EmailDTO emailDTO = EmailDTO.builder().to("czekolada18181818@o2.pl").subject("STS Akceptacja umowy")
				.content("TEST").build();

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.wp.pl");
		mailSender.setPort(465);
		mailSender.setUsername("psk-isf-sts@wp.pl");
		mailSender.setPassword("psk_isf_sts_2018_2");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("psk-isf-sts@wp.pl");
		message.setTo(emailDTO.getTo());
		message.setSubject(emailDTO.getSubject());
		message.setText(emailDTO.getContent());
		mailSender.send(message);

	}
}
