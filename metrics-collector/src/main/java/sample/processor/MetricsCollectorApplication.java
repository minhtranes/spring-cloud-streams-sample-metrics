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

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Bjarte Stien Karlsen
 */
@SpringBootApplication
public class MetricsCollectorApplication {

    private static final Logger logger = LoggerFactory.getLogger(MetricsCollectorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MetricsCollectorApplication.class, args);
    }
    @Bean
    public Consumer<JsonNode> collect() {
        return e -> {
            logger.info("Exported [{}]", e.toString());
            String app = e.findValue("name").textValue();
            JsonNode metricNode = e.findValue("metrics");
            if(metricNode instanceof ArrayNode) {
            	ArrayNode metrics = (ArrayNode) metricNode;
            	metrics.forEach(m->{
            		JsonNode idNode = m.findValue("id");
            		String id = idNode.findPath("name").textValue();
            		int count = m.findValue("count").intValue();
            		float sum = m.findValue("sum").intValue();
            		logger.info("{}.{}: sum={}, count={}",app,id,sum,count);
            	});
            }
        };
    }

   
}