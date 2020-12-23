package org.springframework.samples.petclinic.Sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleConfig {
	@Bean
	public SampleController sampleController() {
		return new SampleController();
	}
}
