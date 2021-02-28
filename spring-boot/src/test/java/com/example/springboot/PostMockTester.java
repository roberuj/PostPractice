package com.example.springboot;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.util.StringUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
	
	
	private final static String jsonPath = "c:\\inputs\\";
	private final static String postJsonFile = "post.json";
	private final static String postCommentJsonFile = "comment.json";
	private final static String postNotExistentComment = "postNotExistentComment.json";
	private final static String postCommentConnectionException = "postCommentConnectionException.json";
	
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
	private WebClient.ResponseSpec responseSpecNormalPostMock;
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecMockComment;
	@Mock
	private WebClient.ResponseSpec responseSpecMockComment;
	
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecEmptyPostMock;
	@Mock
	private WebClient.ResponseSpec responseSpecEmptyPostMock;
	
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecPostWithoutCommentMock;
	@Mock
	private WebClient.ResponseSpec responseSpecEmptyPostWithoutCommentMock;
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecEmptyCommentMock;
	@Mock
	private WebClient.ResponseSpec responseSpecEmptyCommentMock;
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecExceptionPostMock;
	@Mock
	private WebClient.ResponseSpec responseSpecExceptionPostMock;
	@SuppressWarnings("rawtypes")
	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpecExceptionCommentMock;
	@Mock
	private WebClient.ResponseSpec responseSpecExceptionCommentMock;
	
	@Autowired
	PostsUserService postsUserService;
	
	@Autowired
	CommentClient commentClient;
	
	private static List<Post> postList = new ArrayList<Post>();
	private static List<Comment> commentList = new ArrayList<Comment>();
	private static List<Post> postWithEmptyCommentList = new ArrayList<Post>();
	private static List<Post> postWithConnExceptionList = new ArrayList<Post>();
	

	
	@BeforeAll
	public static void loadMocksContent() {
		ObjectMapper mapper = new ObjectMapper();
		Post[] postArray = null;
		Comment[] commentArray = null;
		Post[] postWithEmptyCommentArray = null;
		Post[] postWithConnectException = null; //the post list includes one comment which will cause WebClientException
		try {
			postArray = mapper.readValue(new File(jsonPath+postJsonFile), Post[].class);
			commentArray = mapper.readValue(new File(jsonPath+postCommentJsonFile), Comment[].class);
			postWithEmptyCommentArray = mapper.readValue(new File(jsonPath+postNotExistentComment), Post[].class);
			postWithConnectException = mapper.readValue(new File(jsonPath+postCommentConnectionException), Post[].class);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collections.addAll(postList, postArray);
		Collections.addAll(commentList, commentArray);
		Collections.addAll(postWithEmptyCommentList, postWithEmptyCommentArray);
		Collections.addAll(postWithConnExceptionList, postWithEmptyCommentArray);
	}
	@SuppressWarnings("unchecked")
	@BeforeEach
	public void loadMocks() {	
		//one post and 5 comments per that post
		when(webClient.get())
        .thenReturn(requestHeadersUriSpecMock);
		when(requestHeadersUriSpecMock.uri(baseUrl+urlSuffix, 100))
        .thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.retrieve())
        .thenReturn(responseSpecNormalPostMock);
		when(responseSpecNormalPostMock.bodyToMono(new ParameterizedTypeReference<List<Post>>() {}))
        .thenReturn(Mono.just(postList));
		//mocking the comments for the post above
		when(requestHeadersUriSpecMock.uri(baseUrl+urlCommentSuffix, 1))
		.thenReturn(requestHeadersSpecMockComment);
	    when(requestHeadersSpecMockComment.retrieve())
	      .thenReturn(responseSpecMockComment);
	    when(responseSpecMockComment.bodyToMono(new ParameterizedTypeReference<List<Comment>>() {}))
	      .thenReturn(Mono.just(commentList));
	    //No post for that user
	    when(requestHeadersUriSpecMock.uri(baseUrl+urlSuffix, 0))
	    .thenReturn(requestHeadersSpecEmptyPostMock);
	    when(requestHeadersSpecEmptyPostMock.retrieve())
	      .thenReturn(responseSpecEmptyPostMock);
	    when(responseSpecEmptyPostMock.bodyToMono(new ParameterizedTypeReference<List<Post>>() {}))
	      .thenReturn(Mono.just(new ArrayList<Post>()));
	    
	    //No comments for one post
	    when(requestHeadersUriSpecMock.uri(baseUrl+urlSuffix, 3))
        .thenReturn(requestHeadersSpecPostWithoutCommentMock);
		when(requestHeadersSpecPostWithoutCommentMock.retrieve())
        .thenReturn(responseSpecEmptyPostWithoutCommentMock);
		when(responseSpecEmptyPostWithoutCommentMock.bodyToMono(new ParameterizedTypeReference<List<Post>>() {}))
        .thenReturn(Mono.just(postWithEmptyCommentList));
	    when(requestHeadersUriSpecMock.uri(baseUrl+urlCommentSuffix, 0))
		.thenReturn(requestHeadersSpecEmptyCommentMock);
	    when(requestHeadersSpecEmptyCommentMock.retrieve())
	      .thenReturn(responseSpecEmptyCommentMock);
	    when(responseSpecEmptyCommentMock.bodyToMono(new ParameterizedTypeReference<List<Comment>>() {}))
	      .thenReturn(Mono.just(new ArrayList<Comment>()));
	    
	    
	    
	}
	
	@Test
	@DisplayName("Testing a inexistent user and verify the response is empty")
	public void testEmptyPost() {
		List<CommentsByPost> list = null;
		try {
			list = postsUserService.getCommentsByPostUser("0").toStream().collect(Collectors.toList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(list.size(),0);
	}
	
	@Test
	@DisplayName("Testing a inexistent comment for one post and verify the response is empty")
	public void testEmptyComments() {
		List<CommentsByPost> list = null;
		try {
			list = postsUserService.getCommentsByPostUser("3").toStream().collect(Collectors.toList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(list.size(),1);
		assertEquals(list.get(0).getCommentsNumber(),0);
	}
	
	@Test
	@DisplayName("Testing a existent user with 1 post and 5 comments per post")
	public void testGetPostsandCommentsByUser() {
        List<CommentsByPost> list = null;
		try {
			list = postsUserService.getCommentsByPostUser("100").toStream().collect(Collectors.toList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(list.size(), 1);
		list.forEach(comment->{
			assertNotEquals(comment, null);
			assertEquals(comment.getCommentsNumber(), 5);
			assertEquals(StringUtils.hasText(comment.getPostTitle()),true);
		});
		
      /*StepVerifier.create(mono)
      .expectNextMatches(list -> list.get(0).getTitle()
        .equals("stardust explosion"))
      .verifyComplete();*/
	}
	
	
	@Test
	@DisplayName("Testing comments resource Rest")
	public void testGetComments() {
      Mono<List<Comment>> monoComment  = commentClient.getCommentsByPost(1);
      List<Comment> comments = monoComment.block();
      assertNotEquals(comments,null);
      assertEquals(comments.size(),5);
	}
	
	@Test
	@DisplayName("Testing Posts Rest resource")
	public void testGetPosts() {
		Mono<List<Post>> monoPost = postClient.getPostsByUser(100);
		List<Post> posts = monoPost.block();
      assertNotEquals(posts,null);
      assertEquals(posts.size(),1);
	}
	
	
}
