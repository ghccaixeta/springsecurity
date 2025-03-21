package tech.cx.springsecurity.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import tech.cx.springsecurity.config.AdminUserConfig;
import tech.cx.springsecurity.controller.dto.CreateTweetDto;
import tech.cx.springsecurity.controller.dto.FeedDto;
import tech.cx.springsecurity.controller.dto.FeedItemDto;
import tech.cx.springsecurity.entities.Role;
import tech.cx.springsecurity.entities.Tweet;
import tech.cx.springsecurity.repository.TweetRepository;
import tech.cx.springsecurity.repository.UserRepository;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("feed")
  public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

    var tweets = tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"))
        .map(tweet -> new FeedItemDto(
            tweet.getTweetId(),
            tweet.getContent(),
            tweet.getUser().getUsername()));

    return ResponseEntity
        .ok(new FeedDto(tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getTotalElements()));

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

  @DeleteMapping("/tweets/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long tweetId, JwtAuthenticationToken token) {

    var tweet = tweetRepository.findById(tweetId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    var user = userRepository.findById(UUID.fromString(token.getName()));

    var isAdmin = user.get().getRoles()
        .stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

    if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
      tweetRepository.deleteById(tweet.getTweetId());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

  }

}
