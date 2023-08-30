# hee-commerce
## 🙇‍♀️ 프로젝트 개요
- hee-commerce는 티몬 과 같은 이커머스 서비스에서 운영될 수 있는 타임딜 서버로, 10분동안 TPS 3000를 감당할 수 있도록 구현 중입니다. 
- 문제 해결점을 찾기에 앞서 문제 정의부터 명확히하는 것이 중요하다고 생각하여, 서버 시나리오를 [구글 Docs](https://docs.google.com/document/d/1dzybvtKYLEijSoLKOdGt6cXu-6n38Y_qH8jO7ran8WU/edit)에 상황을 구체화한 후 프로젝트를 시작하였고, 특히 “주문 API”에 집중하여 프로젝트를 진행했습니다.

## 🚀 기술 스택
- JAVA 17, SpringBoot 3, MyBatis 3.5, MySQL 8, Redis 7.0, Flyway 9.5.1, RestDocs, Docker

## ✨ 프로젝트하면서 중요시 했던 포인트
### 1. 다양하고 새로운 기술보다 `문제`에 집중해서 오버엔지니어링 하지 않기
- `트랜잭션이 필요한 엔티티 구조`을 `영속성이 필요 없는 필드`를 제거하고 `새로운 클래스`를 만들어서 `트랜잭션 필요 없는 엔티티 구조`로 변경([#72](https://github.com/f-lab-edu/hee-commerce/pull/72)) 
- 다중 서버 환경에서 재고 차감과 관련된 `동시성 이슈 문제`를 `분산락`이 아닌 `딜 상품과 재고 분리형 저장 구조`와 `재고 사후 검증 단계 도입`으로 해결([wiki](https://github.com/f-lab-edu/hee-commerce/wiki/%EC%A3%BC%EB%AC%B8-%EC%B2%98%EB%A6%AC%EB%A1%9C-%EC%9D%B8%ED%95%9C-%EC%9E%AC%EA%B3%A0-%EC%B0%A8%EA%B0%90%EC%97%90%EC%84%9C-%EB%B0%9C%EC%83%9D%ED%95%98%EB%8A%94-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88-%EB%AC%B8%EC%A0%9C%EB%A5%BC-%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%95%B4%EA%B2%B0%ED%95%A0%EA%B9%8C%3F))

### 2. 테스트 코드 작성
- RestDocs를 이용하여 API 개발 시 TDD 원칙 적용 ([초기 주문 완료 API](https://github.com/f-lab-edu/hee-commerce/pull/40), [주문 사전 저장 API](https://github.com/f-lab-edu/hee-commerce/pull/98/commits/01af70dcaabeeaef363f0a3bf37d0759e36456cc), [주문 승인 API](https://github.com/f-lab-edu/hee-commerce/commit/66c0e341fe797054d3b8455d4f6e803133ef3cec), [딜 상풍 목록 조회 API](https://github.com/f-lab-edu/hee-commerce/pull/38), [딜 상품 상세보기 API](https://github.com/f-lab-edu/hee-commerce/pull/50))
- `테스트 코드`로 `비즈니스 로직의 문서화`를 위해 `서비스 클래스`가 아닌 `도메인 모델`에게 `비즈니스 로직 책임` 변경([#167](https://github.com/f-lab-edu/hee-commerce/pull/167))
- `가독성`과 `유지보수성`을 고려해서 `fixture` 과 `함수` 활용해서 테스트 코드 리팩토링([#146](https://github.com/f-lab-edu/hee-commerce/pull/146))

### 3. 유지보수성을 고려한 코드
- `유지보수성`을 고려해서 재고 증가/감소 및 재고 히스토리 저장 로직을 `응집력` 있게 묶어서 관리([#158](https://github.com/f-lab-edu/hee-commerce/pull/158))
- `유지보수성`을 고려해서 RedisUtils 클래스를 만들어서  Redis의 key를 한 곳에서 `응집력`있게 관리 ([#153](https://github.com/f-lab-edu/hee-commerce/pull/153))
- `유연한 코드`를 위해 `의존성 주입`을 활용하여 인증 로직 구현([#171](https://github.com/f-lab-edu/hee-commerce/pull/171))
- int 대신 Integer 사용하여 `동적쿼리`를 단순한 `정적 쿼리`로 변경([#133](https://github.com/f-lab-edu/hee-commerce/pull/133), [#144](https://github.com/f-lab-edu/hee-commerce/pull/144))

### 4. 고가용성을 고려한 시스템 설계 (진행 중)
- [시스템 설계 구조](https://app.diagrams.net/#G12p5UVtXs0pfISlmnFDNGlt4g1ignoKgO#%7B%22pageId%22%3A%227dibCqtFsK05N7zEinu2%22%7D)

### 5. 비동기로 진행되는 리뷰 상황에 대해 효율적인 소통을 위해 Github의 다양한 기능(이슈, 주석, 코멘트, PR)을 활용하여 문서화
- PR에 이 작업이 무엇인지, 왜 그렇게 했는지 등 맥락을 전달하기 위해 노력했습니다. (예시 : [#72](https://github.com/f-lab-edu/hee-commerce/pull/72))

## 🤔 프로젝트하면서 했던 고민 포인트 
### 1. 부분 주문 처리가 된 경우, 실제 사용자가 요청한 주문 수량과 실제 주문 처리 가능한 수량이 달라진다. 모두 DB에 저장되어 있어야 하는데, 어떻게 저장할까? ([wiki](https://github.com/f-lab-edu/hee-commerce/wiki/%EB%B6%80%EB%B6%84-%EC%A3%BC%EB%AC%B8-%EC%B2%98%EB%A6%AC%EC%99%80-%EA%B4%80%EB%A0%A8%EB%90%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5))

### 2. 재고를 Redis에서 관리하고 있지만, Redis에 장애가 날 경우 등을 대비하여 백업용으로 MySQL에도 저장해야 한다. 어떻게 저장할까? ([wiki](https://github.com/f-lab-edu/hee-commerce/wiki/%EC%9E%AC%EA%B3%A0%EB%A5%BC-Redis%EC%97%90%EC%84%9C-%EA%B4%80%EB%A6%AC%ED%95%98%EA%B3%A0-%EC%9E%88%EC%A7%80%EB%A7%8C,-Redis%EC%97%90-%EC%9E%A5%EC%95%A0%EA%B0%80-%EB%82%A0-%EA%B2%BD%EC%9A%B0-%EB%93%B1%EC%9D%84-%EB%8C%80%EB%B9%84%ED%95%98%EC%97%AC-%EB%B0%B1%EC%97%85%EC%9A%A9%EC%9C%BC%EB%A1%9C-MySQL%EC%97%90%EB%8F%84-%EC%A0%80%EC%9E%A5%ED%95%B4%EC%95%BC-%ED%95%9C%EB%8B%A4.-%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%A0%80%EC%9E%A5%ED%95%A0%EA%B9%8C%3F))

### 3. 필터, 인터셉터, AOP 중 어떤 것으로 로그인 체크 기능을 구현할 것인가? ([wiki](https://github.com/f-lab-edu/hee-commerce/wiki/%ED%95%84%ED%84%B0,-%EC%9D%B8%ED%84%B0%EC%85%89%ED%84%B0,-AOP-%EC%A4%91-%EC%96%B4%EB%96%A4-%EA%B2%83%EC%9C%BC%EB%A1%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%B2%B4%ED%81%AC-%EA%B8%B0%EB%8A%A5%EC%9D%84-%EA%B5%AC%ED%98%84%ED%95%A0-%EA%B2%83%EC%9D%B8%EA%B0%80%3F))

## 🎈 DB 스키마
- [DB 스키마](https://dbdiagram.io/d/64ca132402bd1c4a5e1a4066)
![스크린샷 2023-08-29 오후 4 22 12](https://github.com/f-lab-edu/hee-commerce/assets/60481383/7d37cb63-9709-4c70-b7ac-f3740c53ae7e)

## 🎁 API 문서
- [주문 API 문서](https://darling-sorbet-0583ef.netlify.app/#_%EC%A3%BC%EB%AC%B8)

## 🍿 주문 API WorkFlow
![스크린샷 2023-08-28 오후 7 25 25](https://github.com/f-lab-edu/hee-commerce/assets/60481383/03facf90-9175-4e27-a7eb-ebecfd6be993)

> 예외처리는 어디까지 생각했는지 궁금하시면 [draw.io](https://app.diagrams.net/#G12p5UVtXs0pfISlmnFDNGlt4g1ignoKgO#%7B%22pageId%22%3A%22T2qS1Wb9VKf4C0Zg0WWn%22%7D)를 참고해주세요!
