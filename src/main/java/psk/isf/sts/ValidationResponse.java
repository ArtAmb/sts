package psk.isf.sts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationResponse {

	boolean isOK;
	private String message;
}
