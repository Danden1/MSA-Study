# MSA-Study


## 섹션 3 Gateway

클라이언트에서 요청을 하면, `gateway hanlder mapping` -> `predicate`에 따라 해당 서비스로 요청을 전달

이 때, `filter` 를 추가하여 추가 조작 가능.

`prefilter` 와 `postfilter` 가 있음.

- `prefilter` : 요청이 서비스로 전달되기 전에 실행
- `postfilter` : 서비스로부터 응답을 할 때, 실행

spring 에서 `filter` 는 proxy 패턴을 이용하여 구현되어 있다.


```yaml
routes:
- id: first-service
  uri: lb://MY-FIRST-SERVICE
  predicates:
    - Path=/first-service/**
```

처럼 lb:// 뒤에 서비스 이름을 넣으면 유레카 서버를 통해 해당 서비스를 찾아 요청을 전달함.

-> 해당 서비스의 ip나 port가 변경이 되어도, 자유롭게 찾아갈 수 있음 -> 그래서 MSA에서 많이 쓰는 것 같음.

```groovy
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
```

만약 서비스를 찾지 못하거나 spring eureka 화면에 게이트웨이 정보가 없으면, 위의 내용을 꼭 쓰자!

### 섹션 3 후기

아직까지는 잘 모르겠음. 보통 nginx 를 이용해서 lb 할 수 있게 함.

spring cloud gateway는 nginx와 달리 필터 같은 기능을 넣을 수 있는 것 같음.

-> 검색해보니, 필터나 로깅, 인증 등 스프링 클라우드가 구현하기가 쉽다고 함.

왜 MSA에 많이 쓰는 지 어느 정도 이해가 가기 시작함.

좀 더 강의를 듣고 느낄 수 있을 것 같음.


## 섹션 5 user service

(25.08.05)코드 전부 따라치기에는 비효율적인 부분이 많아서, 일부 내용만 README에 정리할 예정.

### spring security

- Authentication : 인증
- Authorization : 인가

```java
// security version 6.1 이상.
// 이전 버전이랑 다르게 상속을 할 필요 없음
import java.beans.BeanProperty;

@Bean
protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf( csrf -> csrf.disable()) // csrf 비활성화
        .authorizeHttpRequests(authorize -> authorize
            .antMatchers("/h2-console/**").permitAll() // 특정 경로 허용
            .anyRequest().authnticated() // 인증 필요
        )
        .httpBasic(Customizer.withDefaults()) // HTTP Basic 인증 사용
        .headers((headers) -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // H2 콘솔을 위한 프레임 옵션 설정
}

```

### 섹션 5 후기

마이크로 서비스에 대한 내용은 크게 없었음. 유저 서비스를 구현하는 내용이 대부분. 그래서 빠르게 스킵함.

## 섹션 6 Catalog and Orders Service

큰 내용 없었음. 서비스 구현하고, 유레카 서버에 등록하는 내용이 끝.

## 섹션 7 User Service

### security

`WebExpressionAuthorizationManager` 를 이용하여 인증을 구현할 수 있음.

특정 경로에 대해서는 특정 ip만 접근할 수 있도록 설정할 수 있음.


```java
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String userName = ((User)authResult.getPrincipal()).getUsername(); //email 정보.
        UserDto userDetails = userService.getUserDetailsByEmail(userName); // 유저 정보 가져오기
        
        //jwt 토큰 생성
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());
    }
}

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/health_check/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress(env.getProperty("gateway.ip")) // <- IP 변경
                .and()
                .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);

        return authenticationFilter;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}

```

위의 filter를 이용하여 클래스를 정의하고 filter를 등록하여 사용할 수 있음.

1. email, password를 받으면, `AuthenticationFilter` 의 attemptAuthentication 메소드가 실행됨.
2. `UserDetailsService` 의 `loadUserByUsername()` 이용하여 email로 유저 정보를 가져옴.(db 등)
3. `successfulAuthentication` 메소드가 실행되어, jwt 토큰을 생성하고 응답 헤더에 추가함.



### Rewrite Path

사용자가 입력한 path를 다른 path로 전달할 수 있음.

```yaml
filters:
  - RewritePath=/user-service/(?<segment>.*), /${segment}
```

/user-service/ 뒤에 오는 path를 /${segment}로 변경하여 전달함.

### jwt

기존에는 아이디, 패스워드 등을 입력하면, 쿠키에 sessionId를 저장하여 인증을 했음.

단점으로는 모바일 애플리케이션에서 유효하게 사용할 수 없음(공유 불가)

<br>

토큰 기반 인증 시스템이 나옴.

아이디, 패스워드를 입력하면, 서버에서 토큰(jwt)을 생성하여 클라이언트에 전달함.

클라이언트는 이를 가지고 있다가 헤더에 포함해서 토큰을 보내게 됨.

보통 jwt를 많이 씀. 디코딩을 통해 name 이나 메타 정보를 얻을 수 있음.

장점
- stateless -> 서버 여러 대 중, 한 대가 다운되어도 서비스 지속적으로 이용 가능.
- CDN
- 쿠키-세션 이 아님. (CSRF X, 사이트간 요청 위조)
- 지속적인 토큰 저장


### api gateway 인증

api gateway에서 jwt 가 유효한 지 확인하기 위해 복호화를 해야 함.

## 섹션 8 Configuration Server

config 서버를 통해서 각 서비스의 설정 파일을 관리할 수 있음.

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: # 여기에 git repository 주소를 넣으면 됨.(로컬 파일도 가능. 단, file:// 경로를 사용해야 함.)
```
만약 기존 서비스의 config와 중복되는 설정이 있다면, 기본으로 config server의 설정을 가져옴.

### config 변경 되면?

기존에는 config가 변경되면, 서버를 재기동함.

- spring boot의 `actuator` 의 refresh를 사용하면 환경설정 정보만 갱신 가능.(서버는 재기동하지 않음) (~~모니터링만 있는 줄 알았는데....~~)
- spring cloud bus 사용 가능.
  - 서비스가 많아지면, 서비스 마다 refresh를 해야 함. bus를 이용하면 한 번에 적용 가능



## 섹션 9 Spring Cloud Bus

Spring Cloud Config Server 에 Spring Cloud Bus를 추가로 구현

AMQP(메시지 브로커)를 이용하여 config 변경 시, 모든 서비스에 변경 사항을 알림

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

management:
  endpoints:
    web:
      exposure:
        include: 
          - busrefresh
```

이런 식으로 rabbitmq 설정을 해주면 됨.

user -> G/W -> config server 로 요청 됨. 설정이 변경 되면, rabbitmq를 통해서 모든 서비스에 변경 사항을 알림.


설정을 바꾸고, actuator의 /busrefresh 엔드포인트로 POST 요청을 보내면, raabitmq를 통해 모든 서비스에 전달이 됨.

그리고 각 서비스 설정이 바뀜.

### 섹션 9 후기

이런 기능이 있을 줄은 전혀 몰랐음.

평소에 actuator를 이용하고 있음에도 이런 기능까지 있을 줄은 몰랐음.

