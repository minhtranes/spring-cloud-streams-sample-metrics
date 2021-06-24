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
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Bjarte Stien Karlsen
 */
@SpringBootApplication
public class ExporterApplication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        SpringApplication.run(ExporterApplication.class, args);
    }
    private static Random rd= new Random();
	private static void sleepInSecond(int second) {
		try {
			TimeUnit.SECONDS.sleep(rd.nextInt(second));
		} catch (InterruptedException ex) {
		}
	}
	@Value("${sleep.time.second:5}")
	private int sleepTime;
	
    @Bean
    public Consumer<JsonNode> read() {
        return e -> {
        	if(sleepTime>0) {
        	ExporterApplication.sleepInSecond(sleepTime);
        }
            logger.info("Exported [{}]", e.toString());
        };
    }

   
}