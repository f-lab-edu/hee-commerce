# hee-commerce
## 프로젝트 개요
- hee-commerce는 티몬 과 같은 이커머스 서비스에서 운영될 수 있는 타임딜 서버로, 10분동안 TPS 3000를 감당할 수 있는 서버입니다. 
- 문제 해결점을 찾기에 앞서 문제 정의부터 명확히하는 것이 중요하다고 생각하여, 서버 시나리오를 [구글 Docs](https://docs.google.com/document/d/1JlKVZVZQwWjxF3bbVW5a4K3YmnfC6gYYoMHhAn5JKLs/edit)에 상황을 구체화한 후 프로젝트를 시작하였고, 특히 “주문 API”에 집중하여 프로젝트를 진행했습니다.

## 주문 API WorkFlow (수정 필요)
> 어떤 과정을 거쳐 v3까지 나왔는지 궁금하시면 [wiki](https://github.com/f-lab-edu/hee-commerce/wiki/%EC%A3%BC%EB%AC%B8-API-%EB%A1%9C%EC%A7%81)를 참고해주세요!

![스크린샷 2023-08-28 오후 7 25 25](https://github.com/f-lab-edu/hee-commerce/assets/60481383/03facf90-9175-4e27-a7eb-ebecfd6be993)

> 예외처리는 어디까지 생각했는지 궁금하시면 [draw.io](https://app.diagrams.net/#G12p5UVtXs0pfISlmnFDNGlt4g1ignoKgO#%7B%22pageId%22%3A%22T2qS1Wb9VKf4C0Zg0WWn%22%7D)를 참고해주세요!

## 기술 스택
- JAVA 17, SpringBoot 3, MyBatis 3.5, MySQL 8, Redis 7.0, Flyway 9.5.1, RestDocs, Docker

## DB 스키마
- [DB 스키마](https://dbdiagram.io/d/64ca132402bd1c4a5e1a4066)
<img alt="스크린샷 2023-08-02 오후 5 24 57" src="https://github.com/f-lab-edu/hee-commerce/assets/60481383/e331ab79-69d4-4b4e-a5d0-48c37110006f">

## API 문서
- [주문 API 문서](https://darling-sorbet-0583ef.netlify.app/#_%EC%A3%BC%EB%AC%B8)


## 프로젝트하면서 중요시 했던 포인트
### 1. 다양하고 새로운 기술보다 `문제`에 집중해서 오버엔지니어링 하지 않기
- int 대신 Integer 사용하여 `동적쿼리`를 `정적 쿼리`로 `심플화`([#133](https://github.com/f-lab-edu/hee-commerce/pull/133), [#144](https://github.com/f-lab-edu/hee-commerce/pull/144))
- 트랜잭션이 필요한 구조 및 로직을 `영속성이 필요 없는 필드`를 제거하고 `새로운 클래스`를 만들어서 트랜잭션 필요 없는 구조로 변경([#72](https://github.com/f-lab-edu/hee-commerce/pull/72)) 

### 2. 테스트 코드 작성
- RestDocs를 이용하여 API 개발 시 TDD 원칙 적용 ([초기 주문 완료 API](https://github.com/f-lab-edu/hee-commerce/pull/40), [주문 사전 저장 API](https://github.com/f-lab-edu/hee-commerce/pull/98/commits/01af70dcaabeeaef363f0a3bf37d0759e36456cc), [주문 승인 API](https://github.com/f-lab-edu/hee-commerce/commit/66c0e341fe797054d3b8455d4f6e803133ef3cec), [딜 상풍 목록 조회 API](https://github.com/f-lab-edu/hee-commerce/pull/38), [딜 상품 상세보기 API](https://github.com/f-lab-edu/hee-commerce/pull/50))
- `테스트 코드`로 `비즈니스 로직의 문서화`를 위해 `서비스 클래스`가 아닌 `도메인 모델`에게 `비즈니스 로직 책임` 변경([#167](https://github.com/f-lab-edu/hee-commerce/pull/167))
- `가독성`과 `유지보수성`을 고려해서 `fixture` 과 `함수` 활용해서 테스트 코드 리팩토링([#146](https://github.com/f-lab-edu/hee-commerce/pull/146))

### 3. 유지보수성을 고려한 코드
- `유지보수성`을 고려해서 재고 증가/감소 및 재고 히스토리 저장 로직을 `응집력` 있게 묶어서 관리([#158](https://github.com/f-lab-edu/hee-commerce/pull/158))
- `유지보수성`을 고려해서 RedisUtils 클래스를 만들어서  Redis의 key를 한 곳에서 `응집력`있게 관리 ([#153](https://github.com/f-lab-edu/hee-commerce/pull/153))
- `유연한 코드`를 위해 `의존성 주입`을 활용하여 인증 로직 구현([#171](https://github.com/f-lab-edu/hee-commerce/pull/171))

### 4. 확장성을 고려한 코드 및 설계 (작성 예정) 
### 5. 고가용성을 고려한 시스템 설계 및 구현(진행 중)
### 6. 비동기로 진행되는 리뷰 상황에 대해 효율적인 소통을 위해 Github의 다양한 기능(이슈, 주석, 코멘트, PR)을 활용하여 문서화
- PR에 이 작업이 무엇인지, 왜 그렇게 했는지 등 맥락을 전달하기 위해 노력했습니다.

## 프로젝트하면서 했던 고민 포인트 (작성 예정)


