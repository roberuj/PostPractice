package com.example.restClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
public class MyWebClient {
	static final Logger logger = LoggerFactory.getLogger(PostClient.class);
	
	final WebClient webClient;
	public  MyWebClient() {
		logger.debug("getting webclient instance");
		 HttpClient httpClient = HttpClient.create();
		
		 ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);  
		 this.webClient = WebClient.builder()
		        .clientConnector(connector)
		        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		        .build();
		
		
	}
	
	public WebClient getMyWebClient () {
		return this.webClient;
		
	}
	
	
}
