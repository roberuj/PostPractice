package com.example.restClient;


import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.Post;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

public class PostClient {

	@Autowired
	protected WebClient webClient;
	@Value("${example.baseUrl}")
	protected String baseUrl;
	
	
	@Value("${example.post.user.urlSuffix}")
	protected String urlSuffix;
	static final Logger logger = LoggerFactory.getLogger(PostClient.class);
	
	
	/*
	 * Call the rest service "Get a post by post id". We are not using this method
	 * @id post Id
	 * @return the completed post with its attributes
	 */
	
	public Post getPostById(String id) {
		Mono<Post> reply = webClient.get()
		.uri("https://jsonplaceholder.typicode.com/posts/{id}",id)
		.retrieve()
		.bodyToMono(Post.class);
		
		Post post = reply.block();
		return post;
		
	}
	/*
	 * Call the rest service "Get the posts by user". 
	 * @id UserId
	 * @return the list of post by that user
	 */
	public Mono<List<Post>> getPostsByUser(int id){
		Mono<List<Post>> promiseReply = webClient.get()
				.uri(baseUrl+urlSuffix,id)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Post>>() {});
		        				   
		        	
				
				
		
		return promiseReply;
	}
	/*
	 * Call the rest service Delete post. 
	 * @id the post Id that we want to delete
	 * @return the deleted post
	 */
	public void deletePost(String id) {
		Mono<Void> reply = webClient.delete()
				.uri("https://jsonplaceholder.typicode.com/posts/{id}",id)
				.retrieve()
				.bodyToMono(Void.class);
		reply.block();
	}
	
	
	
	 
	/*public Mono<String> pushFcm(FCMData data, String apiKey) {
		String id = "1";		
		Mono<String> myMono = webClient.post()
				.uri(this.baseUrl+this.urlSuffix)
		       .header("Authorization", "key=" + apiKey)	
		       .body(BodyInserters.fromValue(data.getNotification()))
		       .exchange()
		       .timeout(Duration.ofMillis(5000000))
	           .flatMap(clientResponse -> {
	        	   if(clientResponse.statusCode().is2xxSuccessful()) {
	        		   logger.debug("PUSH SENDED OK. StatusCode: " + clientResponse.statusCode());
	        		   return clientResponse.bodyToMono(String.class)
	        				   .timeout(Duration.ofMillis(5000000));
	        	   }
	        	   else {
	        		   logger.debug("PUSH SENDED KO. StatusCode: " + clientResponse.statusCode());
	        		   clientResponse.body((clientHttpResponse, context) -> clientHttpResponse.getBody().
	        					subscribe(body -> {
	        						logger.debug("StatusCode: " + clientResponse.statusCode() + " body: " + body);
	        					}
	        			));	        		   
	        		   return Mono.error(new IllegalStateException());
	        	   }
	        	  });
		
		return myMono;
	}*/
	
	

	
}
