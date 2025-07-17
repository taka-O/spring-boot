package com.example.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 認証サービス
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * 認証リクエスト
     * 
     * @param email メールアドレス
     * @param password パスワード
     */
    public record AuthenticationRequest(String email, String password) {
    }

    /**
     * 認証レスポンス
     * 
     * @param token アクセストークン
     */
    public record AuthenticationResponse(String token) {
    }

    /**
     * 認証
     * 
     * @param request 認証リクエスト
     * @return 認証レスポンス
     */
    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtService.generateToken(authentication);

        return new AuthenticationResponse(jwtToken);
    }
}
