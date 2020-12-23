package org.springframework.samples.petclinic.Sample;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class SampleControllerTest {

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void testDi(){
		SampleController sampleController = applicationContext.getBean(SampleController.class);
		assertThat(sampleController).isNotNull();
	}

}
