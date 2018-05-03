package psk.isf.sts.service.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EmailDTO {
	private String to;
	private String subject;
	private String content;
}
