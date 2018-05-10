package psk.isf.sts.service.series.dto;

import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.Picture;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActorDTO {

	private String name;
	private String surname;

	private Picture thumbnail;
	private Picture thumbnailUrl;
}
