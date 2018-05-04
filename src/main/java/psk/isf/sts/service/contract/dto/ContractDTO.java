package psk.isf.sts.service.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.registration.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {
	private User producer;

	private String contractTemplateName;
	// additional settings....

}
