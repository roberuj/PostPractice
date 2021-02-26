package com.example.springboot;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.Comment;
import com.example.domain.CommentsByPost;
import com.example.domain.Post;
import com.example.restClient.CommentClient;

import com.example.restClient.PostClient;
import com.example.service.PostsUserService;

import reactor.core.publisher.Flux;

@SpringBootTest

public class PostTester {
	@Autowired
	PostClient postClient;
	@Autowired
	CommentClient commentClient;
	@Autowired
	PostsUserService postsUserService;
	
	@Test
	public void testGetPosts() {
		
		
		//Post post = postClient.getPostById("1");
		//List<Post> posts = postClient.getPostsByUser(1);
		//List<Comment> comments = commentClient.getCommentsByPost(1);
		
		List<CommentsByPost> list = postsUserService.getCommentsByPostUser(1).toStream().collect(Collectors.toList());
		assertEquals(list.size(), 10);
		list.forEach(comments->assertEquals(comments.getCommentsNumber(), 5));
	}
	
	@Test
	public void testEmptyOnes() {
		List<CommentsByPost> list = postsUserService.getCommentsByPostUser(0).toStream().collect(Collectors.toList());
		assertEquals(list.size(),0);
	}
}
