# hee-commerce
## 🙇‍♀️ 프로젝트 개요
- hee-commerce는 티몬 과 같은 이커머스 서비스에서 운영될 수 있는 타임딜 서버로, 10분동안 TPS 3000를 감당할 수 있도록 구현 중입니다. 
- 서버 시나리오를 [구글 Docs](https://docs.google.com/document/d/1dzybvtKYLEijSoLKOdGt6cXu-6n38Y_qH8jO7ran8WU/edit)에 상황을 구체화한 후 프로젝트를 시작하였고, 특히 “주문 API”에 집중하여 프로젝트를 진행했습니다.

## 🚀 기술 스택
- JAVA 17, SpringBoot 3, MyBatis 3.5, MySQL 8, Redis 7.0, Flyway 9.5.1, RestDocs, Docker

## ✨ 문제 해결 및 개선 사례
### 1. Redis 저장 구조 변경(딜 상품과 재고 통합형 -> 분리형), DECR 명령어와 사후 검증 로직으로 재고 차감에 대한 동시성 문제 해결 (관련 [상세 내용 링크](https://github.com/f-lab-edu/hee-commerce/wiki/Redis-%EC%A0%80%EC%9E%A5-%EA%B5%AC%EC%A1%B0-%EB%B3%80%EA%B2%BD,-DECR-%EB%AA%85%EB%A0%B9%EC%96%B4%EC%99%80-%EC%82%AC%ED%9B%84-%EA%B2%80%EC%A6%9D-%EB%A1%9C%EC%A7%81%EC%9C%BC%EB%A1%9C-%EC%9E%AC%EA%B3%A0-%EC%B0%A8%EA%B0%90-%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0))
### 2. 영속성이 불필요한 필드 제거로 재고에 따라 상품의 상태 변경에 대한 동시성 이슈 해결 및 TX 필요없는 로직으로 개선 (관련 [상세 내용 링크](https://github.com/f-lab-edu/hee-commerce/wiki/%EC%98%81%EC%86%8D%EC%84%B1%EC%9D%B4-%ED%95%84%EC%9A%94%EC%97%86%EB%8A%94-%ED%95%84%EB%93%9C-%EC%A0%9C%EA%B1%B0%EB%A1%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88-%ED%95%B4%EA%B2%B0-%EB%B0%8F-TX-%ED%95%84%EC%9A%94%EC%97%86%EB%8A%94-%EB%A1%9C%EC%A7%81%EC%9C%BC%EB%A1%9C-%EA%B0%9C%EC%84%A0))
### 3. 비정규화, 컬럼명 변경 및 Null 허용으로 로직 및 테이블 구조 단순화 (Tx 또는 Join 필요 O -> X) (관련 [상세 내용 링크](https://github.com/f-lab-edu/hee-commerce/wiki/%EB%B9%84%EC%A0%95%EA%B7%9C%ED%99%94,-%EC%BB%AC%EB%9F%BC%EB%AA%85-%EB%B3%80%EA%B2%BD-%EB%B0%8F-Null-%ED%97%88%EC%9A%A9%EC%9C%BC%EB%A1%9C-%EB%A1%9C%EC%A7%81-%EB%B0%8F-%ED%85%8C%EC%9D%B4%EB%B8%94-%EA%B5%AC%EC%A1%B0-%EB%8B%A8%EC%88%9C%ED%99%94(Tx-%EB%98%90%EB%8A%94-Join-%ED%95%84%EC%9A%94-O-%E2%80%90--X)))

## 🍏 기타 포인트
#### (1) 테스트 코드 작성
- RestDocs를 이용하여 API 개발 시 TDD 원칙 적용 ([초기 주문 완료 API](https://github.com/f-lab-edu/hee-commerce/pull/40), [주문 사전 저장 API](https://github.com/f-lab-edu/hee-commerce/pull/98/commits/01af70dcaabeeaef363f0a3bf37d0759e36456cc), [주문 승인 API](https://github.com/f-lab-edu/hee-commerce/commit/66c0e341fe797054d3b8455d4f6e803133ef3cec), [딜 상풍 목록 조회 API](https://github.com/f-lab-edu/hee-commerce/pull/38), [딜 상품 상세보기 API](https://github.com/f-lab-edu/hee-commerce/pull/50))
- `테스트 코드`로 `비즈니스 로직의 문서화`를 위해 `서비스 클래스`가 아닌 `도메인 모델`에게 `비즈니스 로직 책임` 변경([#167](https://github.com/f-lab-edu/hee-commerce/pull/167))
- `가독성`과 `유지보수성`을 고려해서 `fixture` 과 `함수` 활용해서 테스트 코드 리팩토링([#146](https://github.com/f-lab-edu/hee-commerce/pull/146))

#### (2) 유지보수성을 고려한 코드
- `유지보수성`을 고려해서 재고 증가/감소 및 재고 히스토리 저장 로직을 `응집력` 있게 묶어서 관리([#158](https://github.com/f-lab-edu/hee-commerce/pull/158))
- `유지보수성`을 고려해서 RedisUtils 클래스를 만들어서  Redis의 key를 한 곳에서 `응집력`있게 관리 ([#153](https://github.com/f-lab-edu/hee-commerce/pull/153))
- `유연한 코드`를 위해 `의존성 주입`을 활용하여 인증 로직 구현([#171](https://github.com/f-lab-edu/hee-commerce/pull/171))
- int 대신 Integer 사용하여 `동적쿼리`를 단순한 `정적 쿼리`로 변경([#133](https://github.com/f-lab-edu/hee-commerce/pull/133), [#144](https://github.com/f-lab-edu/hee-commerce/pull/144))

#### (3) 고가용성을 고려한 시스템 설계 (진행 중)
- [시스템 설계 구조](https://app.diagrams.net/#G1uWe-LuEP5ayfhteyHAtZFGyv2nvTm1-m#%7B%22pageId%22%3A%2261v5buV0yLBeLvqyWT7O%22%7D)

#### (4) 비동기로 진행되는 리뷰 상황에 대해 효율적인 소통을 위해 Github의 다양한 기능(이슈, 주석, 코멘트, PR)을 활용하여 문서화
- PR에 이 작업이 무엇인지, 왜 그렇게 했는지 등 맥락을 전달하기 위해 노력했습니다. (예시 : [#72](https://github.com/f-lab-edu/hee-commerce/pull/72))

## 🎈 DB 스키마
- [DB 스키마](https://dbdiagram.io/d/64ca132402bd1c4a5e1a4066)
![스크린샷 2023-08-29 오후 4 22 12](https://github.com/f-lab-edu/hee-commerce/assets/60481383/7d37cb63-9709-4c70-b7ac-f3740c53ae7e)

## 🎁 API 문서
- [주문 API 문서](https://darling-sorbet-0583ef.netlify.app/#_%EC%A3%BC%EB%AC%B8)

## 🍿 주문 API WorkFlow
![스크린샷 2023-08-28 오후 7 25 25](https://github.com/f-lab-edu/hee-commerce/assets/60481383/03facf90-9175-4e27-a7eb-ebecfd6be993)

