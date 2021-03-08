package com.example.webmockutils;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.restClient.CommentClient;
/*Use inheritance to mock to instantiate the classes and mock them*/
public class CommentClientMock extends CommentClient {
	public CommentClientMock( String baseUrl, WebClient webClient) {
		this.baseUrl = baseUrl;
		this.urlSuffix = "/comments?postId={id}";
		this.webClient = webClient;
	}
}
