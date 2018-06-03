package psk.isf.sts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("psk.isf.sts")
@EnableWebMvc
public class MyAppWebConfig extends WebMvcConfigurerAdapter {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	@Value("${sts.path.to.upload.file}")
	private String pathToUploadFiles;

	@Value("${sts.path.to.contract.templates}")
	public String pathToContractTemplate;

	@Value("${sts.path.to.upload.contract}")
	public String pathToUploadContracts;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
		registry.addResourceHandler("/images/**").addResourceLocations("file:" + pathToUploadFiles + "/",
				"classpath:/static/images/");

		registry.addResourceHandler("/contract-templates/**")
				.addResourceLocations("file:" + pathToContractTemplate + "/");
		registry.addResourceHandler("/contracts/**").addResourceLocations("file:" + pathToUploadContracts + "/");

	}

}