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












