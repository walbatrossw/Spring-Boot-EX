package com.doubles.myboot08.security;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true) // mvc 애너테이션 처리
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Autowired
    UsersService usersService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        log.info("Security config....");

        // 모든 사용자
        http.authorizeRequests().antMatchers("/guest/**").permitAll();

        // MANAGER 권한을 가진 사용자
        http.authorizeRequests().antMatchers("/manager/**").hasRole("MANAGER");

        // ADMIN 권한을 가진 사용자
        http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN");

        // 로그인 페이지 보여주기
        http.formLogin().loginPage("/login");

        // 접근 권한 없음 페이지 처리
        http.exceptionHandling().accessDeniedPage("/accessDenied");

        // 로그아웃 처리 페이지 설정
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

        //
        //http.userDetailsService(usersService);

        // Remember-me
        http.rememberMe()
                .key("myboot08")
                .userDetailsService(usersService)
                .tokenRepository(getJDBCRepository())
                .tokenValiditySeconds(60*60*24);
    }

    private PersistentTokenRepository getJDBCRepository() {

        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    // PasswordEncoder 사용하기
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        log.info("build Auth Global...");
        auth.userDetailsService(usersService).passwordEncoder(passwordEncoder());
    }

    // JDBC를 이용한 인증처리
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//
//        log.info("build Auth global ...");
//
//        String query1 = "SELECT uid username, upw password, true enabled FROM tbl_members WHERE uid = ?";
//
//        String query2 = "SELECT member uid, role_name role FROM tbl_member_roles WHERE member = ?";
//
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery(query1)
//                .rolePrefix("ROLE_")
//                .authoritiesByUsernameQuery(query2);
//    }


    // 로그인 정보 설정 하기 inMemory
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        log.info("build auth global....");
//        auth.inMemoryAuthentication().withUser("manager").password("1111").roles("MANAGER");
//    }

}
