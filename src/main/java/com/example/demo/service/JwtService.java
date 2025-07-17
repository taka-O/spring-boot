package com.example.demo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtConfig;
import com.example.demo.config.JwtKeyProperties;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.RequiredArgsConstructor;

/**
 * JWTサービス
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final JwtKeyProperties jwtKeyProperties;

    /**
     * 認証情報からトークンを生成
     */
    public String generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), 
                             authentication.getAuthorities().stream()
                                 .map(GrantedAuthority::getAuthority)
                                 .collect(Collectors.toList()));
    }

    /**
     * UserDetailsからトークンを生成
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), 
                             userDetails.getAuthorities().stream()
                                 .map(GrantedAuthority::getAuthority)
                                 .collect(Collectors.toList()));
    }

    /**
     * ユーザー名と権限リストからトークンを生成
     */
    public String generateToken(String username, Iterable<String> roles) {
        // 現在の時刻
        Instant now = Instant.now();
        
        // アクセストークンの有効期限
        Instant tokenExpiry = now.plus(jwtConfig.getExpiration(), ChronoUnit.MILLIS);

        // アクセストークンに含めるクレーム
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        // アクセストークンの生成
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(tokenExpiry);
        
        claims.forEach(claimsBuilder::claim);
        
        return jwtEncoder().encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

	public JwtEncoder jwtEncoder() {
			JWK jwk = new RSAKey.Builder(jwtKeyProperties.getPublicKey())
							.privateKey(jwtKeyProperties.getPrivateKey())
							.build();
			JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
			return new NimbusJwtEncoder(jwkSource);
	}
}
