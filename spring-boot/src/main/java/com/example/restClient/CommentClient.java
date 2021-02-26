package com.example.restClient;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.Comment;
import com.example.domain.Post;

import reactor.core.publisher.Mono;

public class CommentClient {
	@Autowired
	WebClient webClient;
	@Value("${example.baseUrl}")
	private String baseUrl;
	
	@Value("${example.comments.user.urlSuffix}")
	private String urlSuffix;
	public Mono<List<Comment>> getCommentsByPost(int id){
		Mono<List<Comment>> promiseReply = webClient.get()
				.uri(baseUrl+urlSuffix,id)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Comment>>() {});
		
		return promiseReply;
	}
}
