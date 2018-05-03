package psk.isf.sts.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.Picture;
import psk.isf.sts.repository.PictureRepository;

@Service
public class PictureService {

	@Value("${sts.path.to.upload.file}")
	public String basePath;

	@Autowired
	private PictureRepository pictureRepository;

	public Picture savePicture(String login, MultipartFile multipartFile) throws IllegalStateException, IOException {
		String[] fileName = multipartFile.getOriginalFilename().split(Pattern.quote("."));
		String extension = fileName[fileName.length - 1];
		String name = "picture-" + login + System.currentTimeMillis() + "." + extension;
		File file = new File(basePath);

		if (!file.exists())
			file.mkdirs();

		File destination = new File(basePath + "\\" + name);
		destination.createNewFile();
		multipartFile.transferTo(destination);

		return pictureRepository.save(Picture.builder().name(name).path(destination.getPath()).build());

	}

}