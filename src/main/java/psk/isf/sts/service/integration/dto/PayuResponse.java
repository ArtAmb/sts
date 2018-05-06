package psk.isf.sts.service.integration.dto;

import lombok.Data;

@Data
public class PayuResponse {
	private String access_token;
	private String token_type;
	private Long expires_in;
	private String grant_type;
}
