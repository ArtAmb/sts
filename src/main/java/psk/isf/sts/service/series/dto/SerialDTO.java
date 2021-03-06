package psk.isf.sts.service.series.dto;

import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.serial.Genre;
import psk.isf.sts.entity.serial.State;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SerialDTO {

	private String title;
	private String description;
	private State state;
	private Collection<Genre> genres;
	private Long durationInSec;
	private String linkToWatch;
	private MultipartFile picture;
}
