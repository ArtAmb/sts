package psk.isf.sts.service.series.dto;

import javax.mail.Multipart;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActorDTO {
	private Long id;
	private String name;
	private String surname;
	private String thumbnailUrl;
	private MultipartFile picture;
}
