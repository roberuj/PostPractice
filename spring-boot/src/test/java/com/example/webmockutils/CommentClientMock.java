package com.example.webmockutils;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.restClient.CommentClient;

public class CommentClientMock extends CommentClient {
	public CommentClientMock( String baseUrl, WebClient webClient) {
		this.baseUrl = baseUrl;
		this.urlSuffix = "";
		this.webClient = webClient;
	}
}
