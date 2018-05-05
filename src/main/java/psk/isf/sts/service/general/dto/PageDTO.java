package psk.isf.sts.service.general.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PageDTO {
	@Builder.Default
	private int start = 1;
	@Builder.Default
	private int howMany = 10;

}
