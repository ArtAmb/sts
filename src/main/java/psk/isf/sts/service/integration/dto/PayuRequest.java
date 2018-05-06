package psk.isf.sts.service.integration.dto;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayuRequest {

	private String continueUrl;
	private String customerIp;
	private String merchantPosId;
	private String description;
	private String currencyCode;
	private String totalAmount;
	private PayuSettings settings;
	private Collection<PayuProduct> products;
	private PayuBuyer buyer;

}
