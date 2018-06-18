package psk.isf.sts.service.series;

import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.entity.serial.SerialElementType;
import psk.isf.sts.entity.serial.State;
import psk.isf.sts.repository.serial.GenreRepository;
import psk.isf.sts.repository.serial.SerialRepository;
import psk.isf.sts.service.PictureService;

@Service
public class SerialsXmlParserService {

	@Autowired
	private SerialService serialService;

	@Autowired
	private PictureService pictureService;

	@Autowired
	private GenreRepository genreRepository;

	@Autowired
	private SerialRepository serialRepo;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public List<SerialElement> parseXmlToGetSerials(MultipartFile file, User user) throws Exception {

		List<SerialElement> serials = new LinkedList<>();

		InputStream stream = file.getInputStream();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(stream);
		NodeList allSerials = doc.getElementsByTagName("serial");
		for (int i = 0; i < allSerials.getLength(); ++i) {
			Element serialXml = (Element) allSerials.item(i);
			SerialElement serial = createSerial(serialXml, user);
			NodeList seasons = serialXml.getElementsByTagName("season");

			for (int j = 0; j < seasons.getLength(); ++j) {
				Element seasonXml = (Element) seasons.item(j);
				SerialElement season = createSeason(seasonXml, serial, user);
				NodeList episodes = seasonXml.getElementsByTagName("episode");

				for (int k = 0; k < episodes.getLength(); ++k) {
					Element episodeXml = (Element) episodes.item(k);
					SerialElement episode = createEpisode(episodeXml, season, user);

					season.getElements().add(episode);
				}

				serial.getElements().add(season);

			}

			serials.add(serial);
		}

		return serials;
	}

	private SerialElement createSeason(Element xmlSerialElement, SerialElement parentElement, User user) {
		String title = getSerialElementAtribiute(xmlSerialElement, "title");
		String description = getSerialElementAtribiute(xmlSerialElement, "description");
		String active = getSerialElementAtribiute(xmlSerialElement, "active");

		SerialElement season = SerialElement.builder().title(title).description(description)
				.active(active != null ? Boolean.parseBoolean(active) : true).elementType(SerialElementType.SEASON)
				.parent(parentElement).thumbnail(pictureService.findNoPhotoPicture()).producer(user)
				.elements(new LinkedList<>()).build();

		return season;
	}

	private SerialElement createSerial(Element xmlSerialElement, User user) {
		String title = getSerialElementAtribiute(xmlSerialElement, "title");
		String description = getSerialElementAtribiute(xmlSerialElement, "description");
		String active = getSerialElementAtribiute(xmlSerialElement, "active");

		String linkToWatch = getSerialElementAtribiute(xmlSerialElement, "linkToWatch");
		String state = getSerialElementAtribiute(xmlSerialElement, "state");
		String genres = getSerialElementAtribiute(xmlSerialElement, "genres");

		SerialElement season = SerialElement.builder().title(title).description(description)
				.active(active != null ? Boolean.parseBoolean(active) : true).elementType(SerialElementType.SERIAL)
				.parent(null).thumbnail(pictureService.findNoPhotoPicture()).linkToWatch(linkToWatch).producer(
						user)
				.state(state != null ? State.valueOf(state) : State.RUNNING)
				.genres(genres != null
						? Arrays.stream(genres.split(" "))
								.map(g -> genreRepository.findByName(g).stream().findFirst().orElse(null))
								.filter(g -> g != null).collect(Collectors.toList())
						: null)
				.elements(new LinkedList<>()).build();

		return season;
	}

	private SerialElement createEpisode(Element xmlSerialElement, SerialElement parentElement, User user)
			throws ParseException {
		String title = getSerialElementAtribiute(xmlSerialElement, "title");
		String description = getSerialElementAtribiute(xmlSerialElement, "description");
		String active = getSerialElementAtribiute(xmlSerialElement, "active");
		String startDate = getSerialElementAtribiute(xmlSerialElement, "startDate");
		String durationInSec = getSerialElementAtribiute(xmlSerialElement, "durationInSec");

		SerialElement season = SerialElement.builder().title(title).description(description)
				.active(StringUtils.isNullOrEmpty(active) ? Boolean.parseBoolean(active) : true)
				.elementType(SerialElementType.EPISODE).parent(parentElement)
				.thumbnail(pictureService.findNoPhotoPicture()).producer(user)
				.startDate(!StringUtils.isNullOrEmpty(startDate) ? new Date(sdf.parse(startDate).getTime()) : null)
				.durationInSec(durationInSec != null ? Long.parseLong(durationInSec) : null).build();

		return season;
	}

	private String getSerialElementAtribiute(Element xmlSerialElement, String attribiuteName) {
		Node nodeValue = xmlSerialElement.getElementsByTagName(attribiuteName).item(0);
		if (nodeValue != null)
			return nodeValue.getTextContent();

		return null;
	}

}
