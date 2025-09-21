# 섹션 10 이후 강의 정리 내용


## 섹션 10 MicroService 간의 통신

MSA에서는 다른 서비스의 데이터를 가져와서 이용할 때가 많음.

http 통신을 이용한다면?

java의 `RestTemplate`이나 `Feign` 등 이용할 수 있음.

### RestTemplate

```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    @LoadBalanced // Ribbon을 이용한 로드 밸런싱 활성화
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
```

```yaml
order_service:
  url: http://order-service/order-service/%s/orders
```

이처럼 url에 ip 가 아니라 서비스 이름을 넣어주려면, `@LoadBalanced` 어노테이션을 붙여주어야 함.

### Feign

`RestTemplate` 보다 더 간단하게 쓸 수 있음.

(내가 알기로는 버그 수정 정도만 하고, 더 신규 기능이 개발 안되는 것으로 알고 있음...)
https://github.com/spring-cloud/spring-cloud-openfeign

```java
@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable("userId") String userId);
}
```

이런 식으로 간단하게 사용 가능.

error decoder를 이용하여 400 등 다양한 코드에 대해 처리 가능

(강의에서도 설명해줌)

궁금한 점은 유저 정보를 가져왔는데, 이 때 유저 서비스에서 해당 유저 정보가 변경되면, 데이터 정합성을 보장할 수 없을 것 같은데...?

### 데이터 동기화 문제

만약 order service가 여러 개 기동이 된다면?

-> 데이터가 분산 저장되고 동기화 문제가 발생한다고 함

(강의에서는 서버 1대에 db 1대로 보임. MSA에서 서비스 하나 당 db 1대인 줄 알았는데, 그게 아닌가 봄)

1.  db하나만 이용하면 된다고 함. 하지만 MSA에서는 각각 db를 가지는 게 좋다고 함.
2. db 간의 동기화
   - MQ server 를 이용하여 db 간 동기화하는 방법이 있음.
3. kafka connector
   - CRUD 작업이 발생하면, MQ에 메시지 보냄. 이 메시지리를 db 하나에 저장하도록 하는 방식.
   - 다른 서비스와의 통신 뿐만이 아니라 이런 용도로 사용할 수 있다는 점이 신기함.

### 섹션 10 후기

kafka connector에 얼추 들어본 적은 있지만, 무슨 역할인지는 몰랐음.

MSA에서 kafka를 이용하여 서비스 간의 통신을 주로 하는 줄 알았는데, db 동기화에도 쓸 수 있다는 것이 인상 깊었음.

특히 MSA에서는 서버 : db = 1: 1 로 주로 처리하는 줄 몰랐었음.

서비스 하나 당 db를 하나 가지고 있는 줄 알았음.


## 섹션 11 (암호화, 건너뜀)

## 섹션 12,13 데이터 동기화를 위한 kafka 통신

섹션 10에서 http 통신을 이용하면, 데이터 정합성 관련해서 문제가 생길 수 있다고 함.(나도 그렇게 생각을 했었고)

이를 해결하기 위해 kafka 를 이용함.


### kafka connect

이를 통해 data를 import / export 가능함.

코드 없이 configuration으로 데이터 이동 가능.

standalone mode, distributed mode 지원
- restful api를 통해 지원
- stream or batch 형태로 데이터 전송 가능
- s3, db 등 다양한 plugin 지원

kafka connect source -> kafka cluster -> kafka connect sink -> target (s3, db 등) 로 전달하게 됨.


(상세 설정은 docs 참고하는 것이 더 좋을 것 같음)


# 강의 총 수강 후기

### 듣기 좋은 사람

- MSA에 대해 전반적으로 모르는 사람
   - 전체적으로 한 번 설명을 함. 전반적으로 MSA에 대해 고려해야하는 점을 알 수 있음.
- 스프링 클라우드에 대해 호기심이 있는 사람
  - 스프링 클라우드를 왜 MSA에서 많이 쓰는지 실습을 통해 알 수 있었음. 실제로 적용하면 좋을 것 같음

### 아쉬운 점

- 실습을 따라가는 것이 생각보다 힘듦. 강의에서 하나하나 타이핑 보다는 강사님의 깃허브에서 코드를 가져오고 해야 함. 물론 이러한 부분을 좋아하시는 분도 있을 것 같음.
- ~~MSA에 대해서 깊게 알고 싶은 사람?~~
  - ~~데이터 정합성이 중요한 환경에서 트랜잭션을 어떻게 처리하는 지에 대한 설명은 없어서 아쉬웠음.~~ 제가 놓쳤었네요...
  - kafka connect를 통해 여러 DB의 정합성을 보장할 수 있다는 것은 좋았음.











