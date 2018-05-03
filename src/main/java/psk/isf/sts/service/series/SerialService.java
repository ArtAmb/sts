package psk.isf.sts.service.series;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.repository.SerialRepository;

@Service
public class SerialService {

	@Autowired
	private SerialRepository serialRepo;

	public Collection<SerialElement> allSerials() {
		return (Collection<SerialElement>) serialRepo.findAll();
	}
}
