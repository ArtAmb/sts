package psk.isf.sts.service.contract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import psk.isf.sts.entity.Contract;
import psk.isf.sts.entity.ContractState;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.ContractRepository;
import psk.isf.sts.service.contract.dto.ContractDTO;

@Service
public class ContractService {

	Font normalFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
	Font headerFont = FontFactory.getFont(FontFactory.COURIER, 32, BaseColor.BLACK);
	Font subHeaderFont = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
	Font boldFont = new Font(FontFamily.COURIER, 16, Font.BOLD);

	@Autowired
	private ContractRepository contractRepository;

	@Value("${sts.path.to.contract.templates}")
	private String basePath = "D://sts/contract-templates";

	@Value("${sts.path.to.contract.templates.encoding}")
	private String contractTemplateEncoding = "UTF-8";

	@Value("${sts.path.to.upload.contract}")
	private String pathToUploadContract;

	public Contract createNewContract(ContractDTO contractDTO) throws DocumentException, IOException {
		String pdfName = generatePdf(contractDTO);
		return saveContract(contractDTO.getProducer(), pdfName);
	}

	public String generatePdf(ContractDTO contractDTO) throws DocumentException, IOException {
		String path = pathToUploadContract + "\\" + contractDTO.getProducer().getLogin();
		File file = new File(path);

		if (!file.exists())
			file.mkdirs();

		String contractName = "contract-" + contractDTO.getProducer().getLogin() + System.currentTimeMillis() + ".pdf";
		file = new File(path + "\\" + contractName);

		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));

			document.open();

			Paragraph header = new Paragraph(new Chunk("STS", headerFont));
			header.setAlignment(Element.ALIGN_CENTER);
			document.add(header);

			Paragraph subHeader = new Paragraph(new Chunk("SERIAL TRACE SYSTEM", subHeaderFont));
			subHeader.setAlignment(Element.ALIGN_CENTER);
			document.add(subHeader);

			document.add(emptyLines(1));
			Paragraph contractTypeInfo = new Paragraph(
					new Chunk("Umowa rejestracji konta dla uzytkownika typu producent", subHeaderFont));
			contractTypeInfo.setAlignment(Element.ALIGN_CENTER);
			document.add(contractTypeInfo);

			document.add(emptyLines(2));

			document.add(getLabelParagraph("Nip", contractDTO.getProducer().getNip()));
			document.add(getLabelParagraph("Nazwa", contractDTO.getProducer().getCompanyName()));
			document.add(getLabelParagraph("Adres", contractDTO.getProducer().getAddress()));

			document.add(emptyLines(2));

			document.add(getList(contractDTO.getContractTemplateName()));
			document.add(emptyLines(2));

			Paragraph footerSign = new Paragraph(new Chunk("......................", normalFont));
			footerSign.setAlignment(Element.ALIGN_RIGHT);
			document.add(footerSign);

			Paragraph footerSignInfo = new Paragraph(new Chunk("czytelny podpis", normalFont));
			footerSignInfo.setAlignment(Element.ALIGN_RIGHT);
			document.add(footerSignInfo);
		} finally {
			document.close();
		}
		return contractName;
	}

	public Paragraph getLabelParagraph(String label, String value) {
		Phrase phrase = new Phrase(label + ": ", boldFont);
		Paragraph result = new Paragraph(phrase);
		result.add(new Chunk(value, normalFont));

		result.setAlignment(Element.ALIGN_LEFT);

		return result;
	}

	public Contract saveContract(User producer, String pdfName) throws IllegalStateException, IOException {

		return contractRepository.save(Contract.builder().name(pdfName).producer(producer)
				.path(pathToUploadContract + "/" + producer.getLogin() + "/" + pdfName).state(ContractState.NEW)
				.build());
	}

	public static void main(String[] args) throws DocumentException, IOException {
		ContractDTO dto = ContractDTO.builder()
				.producer(User.builder().login("batman").companyName("Disney").nip("123456789987654")
						.address("Czekoladowa rzeka 5/81").build())
				.contractTemplateName("contract-template-1.txt").build();

		new ContractService().generatePdf(dto);
	}

	public List getList(String templateName) throws IOException {
		List list = new List();
		Files.readAllLines(Paths.get(basePath + "/" + templateName), Charset.forName(contractTemplateEncoding)).stream()
				.forEach((line) -> {

					if (StringUtils.isNullOrEmpty(line))
						list.add(Chunk.NEWLINE);
					else
						list.add(new ListItem(new Chunk(line, normalFont)));
				});

		return list;
	}

	public Element emptyLines(int howMany) throws DocumentException {
		Paragraph para = new Paragraph();
		for (int i = 0; i < howMany; ++i)
			para.add(Chunk.NEWLINE);

		return para;
	}

	public MultipartFile contractToMultipartFile(Contract contract) throws FileNotFoundException, IOException {
		return new MockMultipartFile(contract.getName(), new FileInputStream(contract.getPath()));
	}

}
