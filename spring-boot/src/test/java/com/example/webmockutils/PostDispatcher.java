package com.example.webmockutils;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

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
		switch (request.getPath()) {
        case "/posts?userId=100":
          try {
			return new MockResponse()
					  .setResponseCode(200)
					  .setBody(mapper.writeValueAsString(postList))
				      .addHeader("Content-Type", "application/json");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        case "/posts?userId=200":
          return new MockResponse().setResponseCode(500);
        case "/comments?postId=1":
        	try {
				return new MockResponse()
				  .setResponseCode(200)
				  .setBody(mapper.writeValueAsString(commentList))
			      .addHeader("Content-Type", "application/json");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
      }
      return new MockResponse().setResponseCode(404);
    }
	

}
