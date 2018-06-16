package psk.isf.sts.service.series.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.SimpleSerialElement;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyEpisodeDTO {

	private boolean watched;
	private SimpleSerialElement ep;
	
}
