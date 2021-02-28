package com.example.service;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.example.domain.Comment;
import com.example.domain.CommentsByPost;
import com.example.domain.Post;
import com.example.restClient.CommentClient;
import com.example.restClient.PostClient;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.restpractice.UserException.UserConnectionException;
import com.restpractice.UserException.UserEmptyException;
import com.restpractice.UserException.UserFormatException;

import org.slf4j.Logger;
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
	@Value("${example.postCommentJsonFile}")
	private String postCommentJsonFile;
	@Value("${example.postXmlFile}")
	private String postXmlFile;
	@Value("${example.xmlPath}")
	private String xmlPath;
	//our log object to insert some logs when some problems come
	static final Logger logger = LoggerFactory.getLogger(PostsUserService.class);
	
	/*
	 * get the posts by user and for each post, get the post name and the number of comments per post.
	 * @param userId user
	 */
	public Flux<CommentsByPost> getCommentsByPostUser(String userId) throws Exception {
		Integer userAdIntegerid;
		ArrayList<CommentsByPost> commentsByPostList = new ArrayList<CommentsByPost>();
		ObjectMapper mapper = new ObjectMapper();
		XmlMapper xmlMapper = new XmlMapper();
		if (!StringUtils.hasText(userId))
			throw new UserEmptyException();
		//we assume that user must be and numeric ID. otherwise we don't need to check this
		try {
			userAdIntegerid = Integer.parseInt(userId);
		}
		catch (NumberFormatException e) {
			throw new UserFormatException(userId);
		}
		Flux<CommentsByPost> flux = postClient.getPostsByUser(userAdIntegerid)
		.onErrorMap(t -> t instanceof WebClientRequestException, t -> new UserConnectionException())
		.flatMapIterable(posts->{
			/*the call return one Mono of the List type, we can't loop it directly. we use flatmapiterable to transform the list into flux.
			 * By the way, we export the files
			 */
			try {
				mapper.writeValue(new File(jsonPath+postJsonFile), posts );
				xmlMapper.writeValue(new File(xmlPath+postXmlFile), posts );
			} catch (IOException e) {
				//We decided  not to stop the execution because we couldn't generate the xml.But in the real life depends on user requeriments.
				//We could add a warning list in the response (by now is a stream but we could convert into other class)
				//e.printStackTrace();
				logger.info("XML, json couldn't have been generated");
			}
			return posts;
		}).flatMap(post->{
			/*we loop the list and for each post we calculate a CommentByPost record (Post name and number of comments per post)
			 * 
			 */
			return commentClient.getCommentsByPost(post.getId())
			/*
			 * When we find a connection problem, in spite of we can get partial answers, the whole process can't be completed, so 
			 * we consider a partial result a wrong response here, therefore we interrupt the process, control the exception and throw 
			 * our user Exception (Which is tested in unit test as a expected user exception). But in the real world, the user requirement
			 * could be different from this.
			 */
					
			.onErrorMap(t -> t instanceof WebClientRequestException, t -> new UserConnectionException())
			.flatMap(commentList->{
				/*we have to get the comments of this post and create the record with the post name and the number of comments per post
				 * we return this record as a mono type. The flatmap is going to build the flux of this records as a result
				 */
				CommentsByPost commentByPost = new CommentsByPost();
				commentByPost.setPostTitle(post.getTitle());
				commentByPost.setCommentsNumber(commentList.size());
				commentsByPostList.add(commentByPost);
				try {
					mapper.writeValue(new File(jsonPath+postCommentJsonFile), commentList );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.info("Comment json couldn't have been generated");;
				}
				return Mono.just(commentByPost);
			});
		});
		
		return flux;
	}
}
