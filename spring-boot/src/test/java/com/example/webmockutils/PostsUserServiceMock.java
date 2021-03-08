package com.example.webmockutils;

import com.example.restClient.CommentClient;
import com.example.restClient.PostClient;
import com.example.service.PostsUserService;
/*Use inheritance to mock to instantiate the classes and mock them*/
public class PostsUserServiceMock extends PostsUserService {
	public PostsUserServiceMock(PostClient postClient, CommentClient commentClient) {
		this.postClient = postClient;
		this.commentClient = commentClient;
	}
}
