# MSA-Study


## Gateway

클라이언트에서 요청을 하면, `gateway hanlder mapping` -> `predicate`에 따라 해당 서비스로 요청을 전달

이 때, `filter` 를 추가하여 추가 조작 가능.

`prefilter` 와 `postfilter` 가 있음.

- `prefilter` : 요청이 서비스로 전달되기 전에 실행
- `postfilter` : 서비스로부터 응답을 할 때, 실행


