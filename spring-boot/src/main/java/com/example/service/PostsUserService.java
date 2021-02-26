package com.example.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.domain.Comment;
import com.example.domain.CommentsByPost;
import com.example.domain.Post;
import com.example.restClient.CommentClient;
import com.example.restClient.PostClient;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class PostsUserService {
	/*
	 * we inject a RestClient object which wraps the Posts resource call
	 */
	@Autowired
	PostClient postClient;
	/*
	 * we inject a CommentClient object which wraps the Comments resource calls
	 */
	@Autowired
	CommentClient commentClient;
	//load the file paths and name to export the json and xml
	@Value("${example.jsonPath}")
	private String jsonPath;
	@Value("${example.postJsonFile}")
	private String postJsonFile;
	@Value("${example.postXmlFile}")
	private String postXmlFile;
	@Value("${example.xmlPath}")
	private String xmlPath;
	/*
	 * get the posts by user and for each post, get the post name and the number of comments per post.
	 * @param userId user
	 */
	public Flux<CommentsByPost> getCommentsByPostUser(int userId) {
		ArrayList<CommentsByPost> commentsByPostList = new ArrayList<CommentsByPost>();
		ObjectMapper mapper = new ObjectMapper();
		XmlMapper xmlMapper = new XmlMapper();
		Flux<CommentsByPost> flux = postClient.getPostsByUser(userId)
		.flatMapIterable(posts->{
			/*the call return one Mono of the List type, we can't loop it directly. we use flatmapiterable to transform the list into flux.
			 * By the way, we export the files
			 */
			try {
				mapper.writeValue(new File(jsonPath+postJsonFile), posts );
				xmlMapper.writeValue(new File(xmlPath+postXmlFile), posts );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return posts;
		}).flatMap(post->{
			/*we loop the list and for each post we calculate a CommentByPost record (Post name and number of comments per post)
			 * 
			 */
			return commentClient.getCommentsByPost(post.getId()).flatMap(commentList->{
				/*we have to get the comments of this post and create the record with the post name and the number of comments per post
				 * we return this record as a mono type. The flatmap is going to build the flux of this records as a result
				 */
				CommentsByPost commentByPost = new CommentsByPost();
				commentByPost.setPostTitle(post.getTitle());
				commentByPost.setCommentsNumber(commentList.size());
				commentsByPostList.add(commentByPost);
				return Mono.just(commentByPost);
			});
		});
		
		return flux;
	}
}
