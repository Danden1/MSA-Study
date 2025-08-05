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
        .headers((headers) -> headers.frameOptions(frammeOptions -> frameOptions.sameOrigin())); // H2 콘솔을 위한 프레임 옵션 설정
}

```

### 섹션 5 후기

마이크로 서비스에 대한 내용은 크게 없었음. 유저 서비스를 구현하는 내용이 대부분. 그래서 빠르게 스킵함.

## 섹션 6 Catalog and Orders Service

