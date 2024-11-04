package org.library.thelibraryj.jwtAuth.domain;

import org.library.thelibraryj.jwtAuth.JwtService;
import org.springframework.stereotype.Service;

@Service
record JwtServiceImpl() implements JwtService {
    @Override
    public String generateToken(String subject) {
        return "token"; //TO DO
    }
}
