package psk.isf.sts.service.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendContractByCourierDTO {
	private Long contractId;
	private String phoneNumber;
	private String address;
	private String companyName;
	// private MultipartFile contractPdf;
}
