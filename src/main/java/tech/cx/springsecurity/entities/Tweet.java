package tech.cx.springsecurity.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_tweet")
@Getter
@Setter
@NoArgsConstructor
public class Tweet {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "tweet_id")
  private Long tweetId;

  private User user;

  private String content;

  @CreationTimestamp
  private Instant createdAt;

}
