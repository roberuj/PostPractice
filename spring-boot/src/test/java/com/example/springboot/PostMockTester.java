package com.example.springboot;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Disabled;
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

import com.example.domain.Comment;
import com.example.domain.CommentsByPost;
import com.example.domain.Post;
import com.example.restClient.CommentClient;
import com.example.restClient.PostClient;
import com.example.service.PostsUserService;
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
	@Value("${example.postCommentJsonFile}")
	private String postCommentJsonFile;
	
	@Value("${example.baseUrl}")
	private String baseUrl;
	
	@Value("${example.post.user.urlSuffix}")
	private String urlSuffix;
	
	@Value("${example.comments.user.urlSuffix}")
	private String urlCommentSuffix;
	
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;	
	@SuppressWarnings({ "rawtypes" })
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecMock;
	@Mock
	private WebClient.ResponseSpec responseSpecMock;
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecMockComment;
	@Mock
	private WebClient.ResponseSpec responseSpecMockComment;
	
	@Autowired
	PostsUserService postsUserService;
	
	@Autowired
	CommentClient commentClient;
	
	@SuppressWarnings("unchecked")
	
	@Test
	@DisplayName("Testing a existent user with 10 post and 5 comments per post")
	public void testGetPostsReal() {
		ObjectMapper mapper = new ObjectMapper();
		Post[] postArray = null;
		Comment[] commentArray = null;
		try {
			postArray = mapper.readValue(new File(jsonPath+postJsonFile), Post[].class);
			commentArray = mapper.readValue(new File(jsonPath+postCommentJsonFile), Comment[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Post> postList = new ArrayList<Post>();
		Collections.addAll(postList, postArray);
		
		List<Comment> commentList = new ArrayList<Comment>();
		Collections.addAll(commentList, commentArray);
		
		when(webClient.get())
        .thenReturn(requestHeadersUriSpecMock);
      when(requestHeadersUriSpecMock.uri(baseUrl+urlSuffix, 100))
        .thenReturn(requestHeadersSpecMock);
      when(requestHeadersSpecMock.retrieve())
        .thenReturn(responseSpecMock);
      when(responseSpecMock.bodyToMono(new ParameterizedTypeReference<List<Post>>() {}))
        .thenReturn(Mono.just(postList));
      when(requestHeadersUriSpecMock.uri(baseUrl+urlCommentSuffix, 1))
      .thenReturn(requestHeadersSpecMockComment);
	    when(requestHeadersSpecMockComment.retrieve())
	      .thenReturn(responseSpecMockComment);
	    when(responseSpecMockComment.bodyToMono(new ParameterizedTypeReference<List<Comment>>() {}))
	      .thenReturn(Mono.just(commentList));
      
      Mono<List<Post>> monoPost = postClient.getPostsByUser(100);
      Mono<List<Comment>> monoComment  = commentClient.getCommentsByPost(1);
      List<Post> posts = monoPost.block();
      List<Comment> comments = monoComment.block();
      List<CommentsByPost> list = null;
		try {
			list = postsUserService.getCommentsByPostUser("100").toStream().collect(Collectors.toList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
      /*StepVerifier.create(mono)
      .expectNextMatches(list -> list.get(0).getTitle()
        .equals("stardust explosion"))
      .verifyComplete();*/
	}
	
	@SuppressWarnings("unchecked")
	@Disabled
	@Test
	@DisplayName("Testing comments")
	public void testGetcommentsReal() {
		ObjectMapper mapper = new ObjectMapper();
		Post[] postArray = null;
		Comment[] commentArray = null;
		try {
			postArray = mapper.readValue(new File(jsonPath+postJsonFile), Post[].class);
			commentArray = mapper.readValue(new File(jsonPath+postCommentJsonFile), Comment[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Post> postList = new ArrayList<Post>();
		Collections.addAll(postList, postArray);
		
		List<Comment> commentList = new ArrayList<Comment>();
		Collections.addAll(commentList, commentArray);
		when(webClient.get())
        .thenReturn(requestHeadersUriSpecMock);
      when(requestHeadersUriSpecMock.uri(baseUrl+urlCommentSuffix, 1))
        .thenReturn(requestHeadersSpecMockComment);
      when(requestHeadersSpecMockComment.retrieve())
        .thenReturn(responseSpecMockComment);
      when(responseSpecMockComment.bodyToMono(new ParameterizedTypeReference<List<Comment>>() {}))
        .thenReturn(Mono.just(commentList));
      Mono<List<Comment>> monoComment  = commentClient.getCommentsByPost(1);
      List<Comment> comments = monoComment.block();
	}
}
