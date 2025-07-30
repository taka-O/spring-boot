package com.example.demo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtKeyProperties;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import jakarta.validation.constraints.NotBlank;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {
	private final UserRepository userRipository;
  private final JwtKeyProperties jwtKeyProperties;
  private final JavaMailSender javaMailSender;
  private final PasswordEncoder passwordEncoder;

  public record SendResetPasswordTokenRequest(
      @NotBlank
      @Email
      String email,

      @NotBlank
      String reset_url
  ) {
  };

  public record ResetPasswordRequest(
      @NotBlank
      String token,

      @NotBlank
      @Size(min=8)
      @Pattern(regexp="^[a-zA-Z0-9.?/-]{8,24}$$", message="{0}は大文字、小文字を含む半角英数字と記号（.?/-）で8文字以上、24文字以内で入力してください")
      String password,

      @NotBlank
      String password_confirmation
  ) {
    @AssertTrue(message = "パスワードが一致しません。")
    public boolean isPasswordValid() {
      return password.equals(password_confirmation);
    } 
  }

  public void sendResetPasswordMail(String email, String resetUrl) throws NotFoundException {
    User user = userRipository.findByEmail(email)
      .orElseThrow(() -> new NotFoundException());

    String token = generateToken(user);
    sendMail(user, token, resetUrl);
  }

  public void resetPassword(ResetPasswordRequest request) throws NotFoundException, BadJwtException {
    Jwt decodedToken = jwtDecoder().decode(request.token());
    String pid = decodedToken.getSubject();
    if (Instant.now().isAfter(decodedToken.getExpiresAt())) {
      throw new BadJwtException("token expired");
    }

    User user = userRipository.findByPid(pid)
      .orElseThrow(() -> new NotFoundException());

    user.setPassword(passwordEncoder.encode(request.password()));
    userRipository.save(user);
  }

  public String generateToken(User user) {
      Instant now = Instant.now();
      
      long expireInterval = 24 * 60 * 60 * 1000; // 24時間
      Instant tokenExpiry = now.plus(expireInterval, ChronoUnit.MILLIS);

      JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
              .subject(user.getPid())
              .expiresAt(tokenExpiry);
      
      return jwtEncoder().encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
  }

	private JwtEncoder jwtEncoder() {
			JWK jwk = new RSAKey.Builder(jwtKeyProperties.getPublicKey())
							.privateKey(jwtKeyProperties.getPrivateKey())
							.build();
			JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
			return new NimbusJwtEncoder(jwkSource);
	}

  private JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(jwtKeyProperties.getPublicKey()).build();
	}
  public void sendMail(User user, String token, String resetUrl) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setFrom("from@example.com");
      helper.setTo(user.getEmail());
      helper.setSubject("パスワード再設定");

      String link_url = resetUrl + "?token=" + token + "&email=" + user.getEmail();
      String insertMessage = "<html>"
          + "<head></head>"
          + "<body>"
          + "<p><a href='" + link_url + "'>パスワード再設定</a></p>"
          + "</body>"
          + "</html>";
      helper.setText(insertMessage, true);

      javaMailSender.send(mimeMessage);
		} catch (MessagingException | MailException ex) {
			System.err.println(ex.getMessage());
    }
  }
}
