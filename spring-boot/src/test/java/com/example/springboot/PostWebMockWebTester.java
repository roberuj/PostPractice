package com.example.springboot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.Comment;
import com.example.domain.CommentsByPost;
import com.example.domain.Post;
import com.example.service.PostsUserService;
import com.example.webmockutils.PostClientMock;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@DisplayName ("Testing the PostWebMockWebTester class function")
@TestInstance(Lifecycle.PER_CLASS)
public class PostWebMockWebTester {
	private PostClientMock postClientMock;
	private MockWebServer mockWebServer;
	//mock data files
	private final static String postJsonFile = "post.json";
	private final static String postCommentJsonFile = "comment.json";
	private final static String postNotExistentComment = "postNotExistentComment.json";
	private final static String postCommentConnectionException = "postCommentConnectionException.json";
	//files paths
	@Value("${example.inputJsonPath}")
	private String jsonPath;
	@Value("${example.jsonPath}")
	private String jsonPathOutput;
	@Value("${example.postJsonFile}")
	private String postJsonFileOutput;
	@Value("${example.postXmlFile}")
	private String postXmlFile;
	@Value("${example.xmlPath}")
	private String xmlPath;
	
	private List<Post> postList = new ArrayList<Post>();
	private List<Comment> commentList = new ArrayList<Comment>();
	private List<Post> postWithEmptyCommentList = new ArrayList<Post>();
	private List<Post> postWithConnExceptionList = new ArrayList<Post>();
	
	@Autowired
	PostsUserService postsUserService;
	
	//before start testing we load the json files (Only once)
	// Every test start mocking the info that we need and then run the program and the assertions.
	@BeforeAll
	public void loadMocksContent() {
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
		Collections.addAll(postWithConnExceptionList, postWithConnectException);
	}
	
	@BeforeEach
	public void loadMock() throws IOException{
		this.mockWebServer = new MockWebServer();
	    this.mockWebServer.start();
		postClientMock = new PostClientMock(WebClient.builder(),mockWebServer.url("/").toString());
	}
	
	@Test
	@DisplayName("Testing a existent user with 1 post and 5 comments per post")
	public void testGetPostsandCommentsByUser() throws Exception {
		 List<Post> list = null;
		ObjectMapper mapper = new ObjectMapper();
		mockWebServer.enqueue(new MockResponse()
	    	      .setBody(mapper.writeValueAsString(postList))
	    	      .addHeader("Content-Type", "application/json"));
		try {
			list = postClientMock.getPostsByUser(100).block();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
