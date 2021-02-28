package com.example.springboot;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.Comment;
import com.example.domain.CommentsByPost;
import com.example.domain.Post;
import com.example.restClient.CommentClient;

import com.example.restClient.PostClient;
import com.example.service.PostsUserService;
import com.restpractice.UserException.UserEmptyException;
import com.restpractice.UserException.UserFormatException;

import reactor.core.publisher.Flux;

@SpringBootTest
@DisplayName ("Testing the PostTester class function")
public class PostTester {
	@Autowired
	PostClient postClient;
	@Autowired
	CommentClient commentClient;
	@Autowired
	PostsUserService postsUserService;
	
	@Value("${example.jsonPath}")
	private String jsonPath;
	@Value("${example.postJsonFile}")
	private String postJsonFile;
	@Value("${example.postXmlFile}")
	private String postXmlFile;
	@Value("${example.xmlPath}")
	private String xmlPath;
	
	
	@Disabled
	@Test
	@DisplayName("Testing a inexistent user and verify the response is empty")
	public void testEmptyOnes() {
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
	@DisplayName("Testing with a null value as a user and verify the system doesn't crash and an expected UserException is cought")
	public void testUserNullValue() {
		Throwable throable = assertThrows(UserEmptyException.class, ()-> postsUserService.getCommentsByPostUser(null).toStream());
		assertEquals("user cannot be empty", throable.getMessage());
	}
	
	@Test
	@DisplayName("Testing with a wrong parameter format and verify the system doesn't crash and an expected UserException is cought")
	public void testWrongParameterFormat() {
		String parameter = "3a";
		Throwable throable = assertThrows(UserFormatException.class, ()-> postsUserService.getCommentsByPostUser(parameter).toStream());
		assertEquals("User format not valid, " + parameter, throable.getMessage());
	}
	
	@Test
	@DisplayName("Verify the json file existence")
	public void testJsonFileExistence() {
		File file = new File(jsonPath+postJsonFile);
		assertEquals(file.exists(),true);
		assertEquals(file.length()>0,true);
	}
	
	@Test
	@DisplayName("Verify the xml file existence")
	public void testXmlFileExistence() {
		File file = new File(xmlPath+postXmlFile);
		assertEquals(file.exists(),true);
		assertEquals(file.length()>0,true);
	}
	
	@Test
	@DisplayName("Testing a existent user with 10 post and 5 comments per post")
	public void testGetPosts() {
		
		
		
		List<CommentsByPost> list = null;
		try {
			list = postsUserService.getCommentsByPostUser("1").toStream().collect(Collectors.toList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(list.size(), 10);
		list.forEach(comments->{
			assertNotEquals(comments, null);
			assertEquals(comments.getCommentsNumber(), 5);
			assertEquals(StringUtils.hasText(comments.getPostTitle()),true);
		});
	}
}
