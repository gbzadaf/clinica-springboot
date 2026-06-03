package com.gabrielf.clinica.services;

import com.gabrielf.clinica.dto.LoginRequest;
import com.gabrielf.clinica.dto.RegisterRequest;
import com.gabrielf.clinica.dto.TokenResponse;
import com.gabrielf.clinica.exceptions.BusinessException;
import com.gabrielf.clinica.exceptions.ResourceNotFoundException;
import com.gabrielf.clinica.model.User;
import com.gabrielf.clinica.repository.UserRepository;
import com.gabrielf.clinica.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Email já cadastrado");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new TokenResponse(token, user.getEmail(), user.getRole().name());

    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        //gera um token JWT assinado com a secret, usando o e-mail do usuário como identificador dentro do token
        String token = jwtService.generateToken(user.getEmail());
        return new TokenResponse(token, user.getEmail(), user.getRole().name());
        //monta o objeto de resposta que vai ser devolvido pro cliente.

    }
}
