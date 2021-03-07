package com.example.webmockutils;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/*this class implements the MockWebServer dispatcher. We are implemented every response we could receive from the real rest api
 * We extend the abstract class Dispatcher and implement its method dispatch. By this class, 
 * our MockWebServer will reply us with every situation we expected from the real rest api  
 */
public class PostDispatcher extends Dispatcher {

	private List postList;
	private List commentList;
	public PostDispatcher(List postList, List commentList) {
		this.postList = postList;
		this.commentList = commentList;
	}
	@Override
	public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			switch (request.getPath()) {
	        case "/posts?userId=100":
				return new MockResponse()
				  .setResponseCode(200)
				  .setBody(mapper.writeValueAsString(postList))
			      .addHeader("Content-Type", "application/json");
	        case "/posts?userId=150":
				return new MockResponse()
				  .setResponseCode(200)
				  .setBody(mapper.writeValueAsString(new ArrayList<>()))
			      .addHeader("Content-Type", "application/json");	
	        case "/posts?userId=200":
	          return new MockResponse().setResponseCode(500);
	        case "/comments?postId=1":
				return new MockResponse()
				  .setResponseCode(200)
				  .setBody(mapper.writeValueAsString(commentList))
			      .addHeader("Content-Type", "application/json");
	        case "/comments?postId=0":
				return new MockResponse()
				  .setResponseCode(200)
				  .setBody(mapper.writeValueAsString(new ArrayList<>()))
			      .addHeader("Content-Type", "application/json");
	        case "/comments?postId=2":
		          return new MockResponse().setResponseCode(500);
		    default:
		    	 return new MockResponse().setResponseCode(404);
	      }
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      return new MockResponse().setResponseCode(404);
    }
	

}
