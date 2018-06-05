package psk.isf.sts.service.datatable;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import psk.isf.sts.service.general.dto.PageDTO;
import psk.isf.sts.service.general.dto.Row;

@Service
public abstract class AbstractDatatableService<Element> {

	public String toValue(Object object) {
		if (object != null)
			return object.toString();
		else
			return "";
	}

	protected Page<Element> findElements(PageDTO page, PagingAndSortingRepository<Element, Long> repository) {
		return repository.findAll(new PageRequest(page.getStart(), page.getHowMany()));
	}

	abstract public Row prepareOneRow(Element element);

	abstract public List<String> getHeaders();

	public void findAll(PageDTO page, Model model, PagingAndSortingRepository<Element, Long> repository) {
		if (page.getStart() == 0)
			page.setStart(0);
		if (page.getHowMany() == 0)
			page.setHowMany(10);
		Page<Element> elements = findElements(page, repository);

		List<Row> rows = new LinkedList<>();
		for (Element element : elements.getContent()) {
			rows.add(prepareOneRow(element));
		}

		model.addAttribute("headers", getHeaders());
		model.addAttribute("rows", rows);
	}

}
