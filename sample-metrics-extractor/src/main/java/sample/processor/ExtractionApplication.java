/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.processor;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.backoff.FixedBackOff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Bjarte Stien Karlsen
 */
@SpringBootApplication
@EnableTransactionManagement
public class ExtractionApplication {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public ExtractionApplication(PersonRepository repository) {
	}

	public static void main(String[] args) {
		SpringApplication.run(ExtractionApplication.class, args);
	}
	
	@Value("${sleep.time.second:5}")
	private int sleepTime;

	private static Random rd= new Random();
	private static void sleepInSecond(int second) {
		try {
			TimeUnit.SECONDS.sleep(rd.nextInt(second));
		} catch (InterruptedException ex) {
		}
	}

	@Transactional
	@Bean
	public Function<JsonNode, JsonNode> process() {
		return e -> {
			logger.info("Received event={}", e.toString());

			if (sleepTime > 0) {
				sleepInSecond(sleepTime);
			}
			if (e instanceof ObjectNode) {
				((ObjectNode) e).put("extractedTime",
				        System.currentTimeMillis());
			}

			logger.info("Sent event={}", e.toString());
			return e;
		};
	}

	@Bean
	public ListenerContainerCustomizer<AbstractMessageListenerContainer<byte[], byte[]>> customizer() {
		// Disable retry in the AfterRollbackProcessor
		return (container, destination,
				group) -> container.setAfterRollbackProcessor(new DefaultAfterRollbackProcessor<byte[], byte[]>(
						(record, exception) -> System.out.println("Discarding failed record: " + record),
						new FixedBackOff(0L, 0)));
	}
}
