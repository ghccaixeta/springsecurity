package tech.cx.springsecurity.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import tech.cx.springsecurity.config.AdminUserConfig;
import tech.cx.springsecurity.controller.dto.CreateTweetDto;
import tech.cx.springsecurity.entities.Tweet;
import tech.cx.springsecurity.repository.TweetRepository;
import tech.cx.springsecurity.repository.UserRepository;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class TweetController {

  private final AdminUserConfig adminUserConfig;
  private final TweetRepository tweetRepository;
  private final UserRepository userRepository;

  public TweetController(TweetRepository tweetRepository, UserRepository userRepository,
      AdminUserConfig adminUserConfig) {
    this.tweetRepository = tweetRepository;
    this.userRepository = userRepository;
    this.adminUserConfig = adminUserConfig;
  }

  @PostMapping("/tweets")
  public ResponseEntity<Void> createTweet(
      @RequestBody CreateTweetDto createTweetDto,
      JwtAuthenticationToken token) {

    var user = userRepository.findById(UUID.fromString(token.getName()));

    var tweet = new Tweet();
    tweet.setUser(user.get());
    tweet.setContent(createTweetDto.content());

    tweetRepository.save(tweet);

    return ResponseEntity.ok().build();

  }

}
