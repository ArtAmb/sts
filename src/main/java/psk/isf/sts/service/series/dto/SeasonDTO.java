package psk.isf.sts.service.series.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.State;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeasonDTO {

	private String title;
	private String description;
	private State state;
	private MultipartFile picture;
	
}
