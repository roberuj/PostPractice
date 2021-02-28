package com.example.springboot;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.Mockito.when;   // ...or...

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;  


import com.example.domain.Post;
import com.example.restClient.PostClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName ("Testing the PostTester class function")

public class PostMockTester {
	@MockBean
	private WebClient webClient; 
	@Autowired	
	PostClient postClient;
	
	@Value("${example.jsonPath}")
	private String jsonPath;
	@Value("${example.postJsonFile}")
	private String postJsonFile;
	@Value("${example.postXmlFile}")
	private String postXmlFile;
	@Value("${example.xmlPath}")
	private String xmlPath;
	
	@Value("${example.baseUrl}")
	private String baseUrl;
	
	@Value("${example.post.user.urlSuffix}")
	private String urlSuffix;
	
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecMock;
	@Mock
	private WebClient.ResponseSpec responseSpecMock;
	
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Testing the mock service")
	public void testGetPosts() {
		Post mockPost = new Post();
		mockPost.setBody(".......");
		mockPost.setId(1);
		mockPost.setTitle("stardust explosion");
		mockPost.setUserId(100);
		ArrayList<Post> postList = new ArrayList<Post>();
		postList.add(mockPost);
		
		when(webClient.get())
        .thenReturn(requestHeadersUriSpecMock);
      when(requestHeadersUriSpecMock.uri(baseUrl+urlSuffix, mockPost.getUserId()))
        .thenReturn(requestHeadersSpecMock);
      when(requestHeadersSpecMock.retrieve())
        .thenReturn(responseSpecMock);
      when(responseSpecMock.bodyToMono(new ParameterizedTypeReference<List<Post>>() {}))
        .thenReturn(Mono.just(postList));
      Mono<List<Post>> mono = webClient
    		  	.get()
    		  	.uri(baseUrl+urlSuffix,mockPost.getUserId())
    		  	.retrieve()
    			.bodyToMono(new ParameterizedTypeReference<List<Post>>() {});
      StepVerifier.create(mono)
      .expectNextMatches(list -> list.get(0).getTitle()
        .equals("stardust explosion"))
      .verifyComplete();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Testing a existent user with 10 post and 5 comments per post")
	public void testGetPostsReal() {
		ObjectMapper mapper = new ObjectMapper();
		Post[] postArray = null;
		try {
			postArray = mapper.readValue(new File(jsonPath+postJsonFile), Post[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Post> postList = new ArrayList<Post>();
		Collections.addAll(postList, postArray);
		
		
		when(webClient.get())
        .thenReturn(requestHeadersUriSpecMock);
      when(requestHeadersUriSpecMock.uri(baseUrl+urlSuffix, 100))
        .thenReturn(requestHeadersSpecMock);
      when(requestHeadersSpecMock.retrieve())
        .thenReturn(responseSpecMock);
      when(responseSpecMock.bodyToMono(new ParameterizedTypeReference<List<Post>>() {}))
        .thenReturn(Mono.just(postList));
      
      Mono<List<Post>> mono = postClient.getPostsByUser(100);
      List<Post> posts = mono.block();
      /*StepVerifier.create(mono)
      .expectNextMatches(list -> list.get(0).getTitle()
        .equals("stardust explosion"))
      .verifyComplete();*/
	}
}
