package psk.isf.sts.service.integration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import psk.isf.sts.controller.view.AuthenticationController;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.service.integration.dto.PayuBuyer;
import psk.isf.sts.service.integration.dto.PayuOrderResponse;
import psk.isf.sts.service.integration.dto.PayuProduct;
import psk.isf.sts.service.integration.dto.PayuRequest;
import psk.isf.sts.service.integration.dto.PayuResponse;
import psk.isf.sts.service.integration.dto.PayuSettings;

@Service
public class PayuService {
	
	@Value("${sts.self.url}")
	private String selfUrl;
	
	public PayuOrderResponse getPaymentRedirect(User user, String controlParam) {
		String url = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize?grant_type=client_credentials&client_id=332246&client_secret=f29943d8404568aaedfbd424c5922775";
		String urlToOrder = "https://secure.snd.payu.com/api/v2_1/orders";
		
		RestTemplate restTemplate = new RestTemplate();

		LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Access-Control-Allow-Origin", "*");

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
				body, headers);

		PayuResponse payuResponse = restTemplate.postForObject(url, requestEntity, PayuResponse.class);
		System.out.println(payuResponse);

		
		HttpHeaders payuOrderHeaders = new HttpHeaders();
		payuOrderHeaders.add("Authorization", String.format("Bearer %s", payuResponse.getAccess_token()));
		payuOrderHeaders.add("Content-Type", "application/json");
		HttpEntity<PayuRequest> payuOrderRequestEntity = new HttpEntity<>(buildPayuRequest(user, controlParam), payuOrderHeaders);
		
		return restTemplate.postForObject(urlToOrder, payuOrderRequestEntity, PayuOrderResponse.class);
	}
	
	private PayuRequest buildPayuRequest(User user, String controlParam) {
		return PayuRequest.builder()
				.continueUrl(selfUrl + "/view/my-profile?" + AuthenticationController.activateUserControlParam + "=" + controlParam)
				.customerIp("127.0.0.1")
				.merchantPosId("332246")
				.description("Producer account activation")
				.currencyCode("PLN")
				.totalAmount("500")
				.buyer(PayuBuyer.builder()
						.emial(user.getEmail())
						.phone(user.getPhoneNumber())
						.firstName(user.getCompanyName())
						.lastName(user.getCompanyName())
						.laguage("pl")
						.build())
				.settings(PayuSettings.builder().invoiceDisabled(true).build())
				.products(Arrays.asList(PayuProduct.builder()
						.name("Producer account activation")
						.unitPrice("500")
						.quantity("1")
						.virtual(true)
						.build()))
				.build();
	}
	
}
