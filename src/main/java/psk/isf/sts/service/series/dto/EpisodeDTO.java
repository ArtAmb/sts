package psk.isf.sts.service.series.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeDTO {

	private String title;
	private String description;
	private MultipartFile picture;
	//private Timestamp startDate;
	private LocalDateTime startDate;
	
}
