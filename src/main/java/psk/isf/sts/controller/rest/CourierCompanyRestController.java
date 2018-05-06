package psk.isf.sts.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import psk.isf.sts.service.integration.dto.SendContractByCourierDTO;
import psk.isf.sts.service.integration.dto.SendContractByCourierResponse;

@RestController
public class CourierCompanyRestController {

	@Value("${sts.self.url}")
	private String stsUrl;

	@PostMapping("/courier-company/facade/send-contract-by-courier")
	public String courierMethod(@RequestBody SendContractByCourierDTO dto) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					System.out.println("CURIER COMPANY: recievedDTO - > " + dto.toString());
					Thread.sleep(5000);
					System.out.println("CURIER COMPANY: CLIENT HAS JUST REVICED AND SIGNED CONTRACT");
					new RestTemplate().postForObject(stsUrl + "/sts/facade/signContract",
							SendContractByCourierResponse.builder().contractId(dto.getContractId()).build(),
							Void.class);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		return "OK";
	}
}
