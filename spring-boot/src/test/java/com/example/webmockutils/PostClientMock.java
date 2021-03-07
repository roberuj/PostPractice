package com.example.webmockutils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.restClient.PostClient;

@SpringBootTest
public class PostClientMock extends PostClient {
	public PostClientMock( String baseUrl, WebClient webClient) {
		this.baseUrl = baseUrl;
		this.urlSuffix = "/posts?userId={id}";
		this.webClient = webClient;
	}
}
