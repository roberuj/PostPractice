package com.example.springboot;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.restClient.CommentClient;
import com.example.restClient.MyWebClient;
import com.example.restClient.PostClient;

@SpringBootApplication
@ComponentScan(value= {"com.example.*"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean(name = "webClient")
	//@Scope("prototype")
	public WebClient getWebClient() {
		MyWebClient myWebClientinstance = new MyWebClient();
		return myWebClientinstance.getMyWebClient();
	}
	@Bean(name="postClient")
	public PostClient getPostClient() {
		return  new PostClient();
	}
	@Bean(name="commentClient")
	public CommentClient getCommentClient() {
		return  new CommentClient();
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}

}
