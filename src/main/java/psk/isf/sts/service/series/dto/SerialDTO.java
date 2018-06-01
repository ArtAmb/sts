package psk.isf.sts.service.series.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SerialDTO {

	private String title;
	private String description;
	//private State state;
	//private Collection<Genre> genres;
	//private long durationInSec;
	private String linkToWatch;
	private MultipartFile picture;
}
