package psk.isf.sts.service.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayuBuyer {
	private String emial;
	private String phone;
	private String firstName;
	private String lastName;
	private String laguage;
}
