//package org.library.thelibraryj;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.DeferredCsrfToken;
//
//@Profile(value = {"test"})
//@Configuration
//@RequiredArgsConstructor
//class TestSecurityConfig {
//
//    @Profile(value = {"test"})
//    @Bean(name="csrfTokenRepository")
//    CsrfTokenRepository testCookieCsrfTokenRepository() {
//        final CsrfToken token = new CsrfToken() {
//            @Override
//            public String getHeaderName() {
//                return "X-XSRF-TOKEN";
//            }
//
//            @Override
//            public String getParameterName() {
//                return "_csrf";
//            }
//
//            @Override
//            public String getToken() {
//                return "csrf-token";
//            }
//        };
//        return new CsrfTokenRepository() {
//            @Override
//            public CsrfToken generateToken(HttpServletRequest request) {
//               return token;
//            }
//
//            @Override
//            public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {}
//
//            @Override
//            public CsrfToken loadToken(HttpServletRequest request) {
//                return token;
//            }
//
//            @Override
//            public DeferredCsrfToken loadDeferredToken(HttpServletRequest request, HttpServletResponse response) {
//                return new DeferredCsrfToken() {
//                    @Override
//                    public CsrfToken get() {
//                        return token;
//                    }
//
//                    @Override
//                    public boolean isGenerated() {
//                        return false;
//                    }
//                };
//            }
//        };
//    }
//}
