# H-OUR API 문서

본 문서는 현재 Spring Boot 프로젝트의 Controller, DTO, SuccessCode, ErrorCode 기준으로 작성한 API 문서입니다.

## 공통 규칙

### Base URL

```text
http://localhost:8080
```

### 공통 Response Body

```json
{
  "success": true,
  "code": "SUCCESS_CODE",
  "message": "처리 결과 메시지",
  "data": {}
}
```

### 인증 Header

인증이 필요한 API는 아래 Header를 사용합니다.

```http
Authorization: Bearer {accessToken}
```

### 공통 에러

## 400 Bad Request

### Body

```json
{
  "success": false,
  "code": "VALIDATION_FAILED",
  "message": "검증에 실패했습니다.",
  "data": null
}
```

### 실패 조건

- Request Body 검증 실패
- 필수 Request Parameter 누락
- 잘못된 enum 값 또는 잘못된 요청 값 전달

## 401 Unauthorized

### Body

```json
{
  "success": false,
  "code": "UNAUTHORIZED",
  "message": "계정 인증이 필요합니다.",
  "data": null
}
```

### 실패 조건

- 로그인하지 않은 상태
- Access Token 누락
- 만료 또는 비정상 토큰 사용

## 403 Forbidden

### Body

```json
{
  "success": false,
  "code": "ACCESS_DENIED",
  "message": "권한이 없습니다.",
  "data": null
}
```

### 실패 조건

- 관리자 권한이 필요한 API를 일반 사용자가 호출
- 본인 소유가 아닌 리소스 접근

---

# Auth API

## 로그인

### Method

POST

### Endpoint

```text
/api/auth/login
```

### 설명

이메일과 비밀번호로 로그인하고 Access Token, Refresh Token을 발급받는다.

# Request

---

### HEADER

- 없음

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| email | String | Y | 로그인 이메일 |
| password | String | Y | 비밀번호 |

#### 예제 코드

```json
{
  "email": "user@example.com",
  "password": "password1234"
}
```

# Response

---

## 200 OK

### Body

```json
{
  "success": true,
  "code": "AUTH_LOGIN_SUCCESS",
  "message": "로그인에 성공했습니다.",
  "data": {
    "accessToken": "access-token",
    "refreshToken": "refresh-token"
  }
}
```

### 성공 조건

- 이메일과 비밀번호가 일치한다.

## 401 Unauthorized

### 실패 조건

- 이메일 또는 비밀번호가 일치하지 않는다.

---

## OAuth 회원가입 정보 조회

### Method

GET

### Endpoint

```text
/api/auth/oauth/signup
```

### 설명

OAuth 가입 완료 전 signupToken으로 가입 후보 정보를 조회한다.

# Request

---

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| signupToken | String | Y | OAuth 회원가입 임시 토큰 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_READ",
  "message": "계정이 정상적으로 조회되었습니다.",
  "data": {
    "email": "oauth@example.com",
    "userName": "OAuth User",
    "provider": "google"
  }
}
```

---

## OAuth 회원가입

### Method

POST

### Endpoint

```text
/api/auth/oauth/signup
```

### 설명

OAuth 인증 후 추가 정보를 입력하여 회원가입을 완료한다.

# Request

---

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| signupToken | String | Y | OAuth 회원가입 임시 토큰 |
| birthDate | LocalDate | Y | 생년월일 |
| gender | Gender | Y | `MALE`, `FEMALE` |
| phoneNumber | String | Y | 전화번호 |
| nationality | String | Y | 국적 |

#### 예제 코드

```json
{
  "signupToken": "signup-token",
  "birthDate": "2000-01-01",
  "gender": "MALE",
  "phoneNumber": "010-1234-5678",
  "nationality": "대한민국"
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "USER_CREATED",
  "message": "계정이 정상적으로 생성되었습니다.",
  "data": {
    "accessToken": "access-token",
    "refreshToken": "refresh-token"
  }
}
```

---

## 토큰 갱신

### Method

POST

### Endpoint

```text
/api/auth/refresh
```

### 설명

Refresh Token으로 새로운 토큰 쌍을 발급받는다.

# Request

---

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| refreshToken | String | Y | 리프레시 토큰 |

#### 예제 코드

```json
{
  "refreshToken": "refresh-token"
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "AUTH_REFRESH_SUCCESS",
  "message": "토큰 갱신에 성공했습니다.",
  "data": {
    "accessToken": "new-access-token",
    "refreshToken": "new-refresh-token"
  }
}
```

---

## 로그아웃

### Method

POST

### Endpoint

```text
/api/auth/logout
```

### 설명

Refresh Token을 삭제하여 로그아웃한다.

# Request

---

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| refreshToken | String | Y | 리프레시 토큰 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "AUTH_LOGOUT_SUCCESS",
  "message": "로그아웃에 성공했습니다.",
  "data": null
}
```

---

# User API

## 회원가입

### Method

POST

### Endpoint

```text
/api/users/signup
```

### 설명

일반 회원가입을 수행한다.

# Request

---

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| userName | String | Y | 사용자 이름, 최대 50자 |
| email | String | Y | 이메일 |
| password | String | Y | 비밀번호, 최소 8자 |
| birthDate | LocalDate | Y | 생년월일 |
| gender | Gender | Y | `MALE`, `FEMALE` |
| phoneNumber | String | Y | 전화번호 |
| nationality | String | Y | 국적 |

#### 예제 코드

```json
{
  "userName": "홍길동",
  "email": "user@example.com",
  "password": "password1234",
  "birthDate": "2000-01-01",
  "gender": "MALE",
  "phoneNumber": "010-1234-5678",
  "nationality": "대한민국"
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "USER_CREATED",
  "message": "계정이 정상적으로 생성되었습니다.",
  "data": {
    "userId": 1
  }
}
```

## 409 Conflict

### 실패 조건

- 이미 등록된 이메일
- 이미 등록된 전화번호

---

## 내 정보 조회

### Method

GET

### Endpoint

```text
/api/users/me
```

### 설명

로그인한 사용자의 기본 정보를 조회한다.

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_READ",
  "message": "계정이 정상적으로 조회되었습니다.",
  "data": {
    "userId": 1,
    "userName": "홍길동",
    "email": "user@example.com",
    "birthDate": "2000-01-01",
    "gender": "MALE",
    "role": "USER",
    "phoneNumber": "010-1234-5678",
    "nationality": "대한민국",
    "isAuthLinked": false
  }
}
```

---

## 마이페이지 조회

### Method

GET

### Endpoint

```text
/api/users/me/mypage
```

### 설명

내 정보와 주소 목록을 함께 조회한다.

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_READ",
  "message": "계정이 정상적으로 조회되었습니다.",
  "data": {
    "userInfo": {
      "userId": 1,
      "userName": "홍길동",
      "email": "user@example.com"
    },
    "addresses": []
  }
}
```

---

## 내 정보 수정

### Method

PATCH

### Endpoint

```text
/api/users/me
```

### 설명

로그인한 사용자의 프로필을 수정한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| userName | String | N | 사용자 이름 |
| birthDate | LocalDate | N | 생년월일 |
| gender | Gender | N | `MALE`, `FEMALE` |
| phoneNumber | String | N | 전화번호 |
| nationality | String | N | 국적 |

#### 예제 코드

```json
{
  "userName": "수정된이름",
  "birthDate": "2000-01-01",
  "gender": "FEMALE",
  "phoneNumber": "010-1111-2222",
  "nationality": "대한민국"
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_UPDATED",
  "message": "계정이 정상적으로 수정되었습니다.",
  "data": {
    "userId": 1
  }
}
```

---

## 비밀번호 변경

### Method

PATCH

### Endpoint

```text
/api/users/me/password
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| currentPassword | String | Y | 현재 비밀번호 |
| newPassword | String | Y | 새 비밀번호, 최소 8자 |

#### 예제 코드

```json
{
  "currentPassword": "password1234",
  "newPassword": "newPassword1234"
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_PASSWORD_CHANGED",
  "message": "비밀번호가 정상적으로 변경되었습니다.",
  "data": null
}
```

---

## 회원 탈퇴

### Method

DELETE

### Endpoint

```text
/api/users/me
```

### 설명

로그인한 계정을 탈퇴 처리한다.

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_DELETED",
  "message": "계정이 정상적으로 탈퇴되었습니다.",
  "data": null
}
```

---

# Address API

## 주소 생성

### Method

POST

### Endpoint

```text
/api/addresses
```

### 설명

로그인한 사용자의 주소를 생성한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| zipCode | String | Y | 우편번호 |
| oldAddress | String | N | 지번 주소 |
| roadAddress | String | Y | 도로명 주소 |
| addressDetail | String | Y | 상세 주소 |
| isMain | Boolean | N | 대표 주소 여부 |

#### 예제 코드

```json
{
  "zipCode": "06234",
  "oldAddress": "서울시 강남구 역삼동",
  "roadAddress": "서울시 강남구 테헤란로",
  "addressDetail": "101호",
  "isMain": true
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "ADDRESS_CREATED",
  "message": "주소가 정상적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "zipCode": "06234",
    "oldAddress": "서울시 강남구 역삼동",
    "roadAddress": "서울시 강남구 테헤란로",
    "addressDetail": "101호",
    "isMain": true
  }
}
```

---

## 내 주소 목록 조회

### Method

GET

### Endpoint

```text
/api/addresses
```

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ADDRESS_READ",
  "message": "주소가 정상적으로 조회되었습니다.",
  "data": []
}
```

---

## 대표 주소 조회

### Method

GET

### Endpoint

```text
/api/addresses/main
```

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ADDRESS_READ",
  "message": "주소가 정상적으로 조회되었습니다.",
  "data": {
    "id": 1,
    "zipCode": "06234",
    "roadAddress": "서울시 강남구 테헤란로",
    "addressDetail": "101호",
    "isMain": true
  }
}
```

---

## 주소 단건 조회

### Method

GET

### Endpoint

```text
/api/addresses/{addressId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| addressId | Long | Y | 주소 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ADDRESS_READ",
  "message": "주소가 정상적으로 조회되었습니다.",
  "data": {
    "id": 1,
    "zipCode": "06234",
    "roadAddress": "서울시 강남구 테헤란로",
    "addressDetail": "101호",
    "isMain": true
  }
}
```

## 404 Not Found

### 실패 조건

- 주소가 존재하지 않는다.

---

## 주소 수정

### Method

PATCH

### Endpoint

```text
/api/addresses/{addressId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| addressId | Long | Y | 주소 ID |

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| zipCode | String | N | 우편번호 |
| oldAddress | String | N | 지번 주소 |
| roadAddress | String | N | 도로명 주소 |
| addressDetail | String | N | 상세 주소 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ADDRESS_UPDATED",
  "message": "주소가 정상적으로 수정되었습니다.",
  "data": {
    "id": 1
  }
}
```

---

## 대표 주소 변경

### Method

PATCH

### Endpoint

```text
/api/addresses/{addressId}/main
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| addressId | Long | Y | 대표 주소로 설정할 주소 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ADDRESS_MAIN_UPDATED",
  "message": "대표 주소가 정상적으로 변경되었습니다.",
  "data": {
    "id": 1,
    "isMain": true
  }
}
```

---

## 주소 삭제

### Method

DELETE

### Endpoint

```text
/api/addresses/{addressId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| addressId | Long | Y | 주소 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ADDRESS_DELETED",
  "message": "주소가 정상적으로 삭제되었습니다.",
  "data": null
}
```

---

# Category API

## 카테고리 목록 조회

### Method

GET

### Endpoint

```text
/api/categories
```

### 설명

사용자에게 노출할 카테고리 목록을 조회한다.

# Request

---

### HEADER

- 없음

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CATEGORY_READ",
  "message": "카테고리가 정상적으로 조회되었습니다.",
  "data": [
    {
      "id": 1,
      "name": "가죽공예",
      "thumbnail": "https://..."
    }
  ]
}
```

---

# Product API

## 상품 목록 조회

### Method

GET

### Endpoint

```text
/api/products
```

### 설명

판매 중인 상품 목록을 조회한다.

# Request

---

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| page | int | N | 페이지 번호, 기본값 0 |
| size | int | N | 페이지 크기, 기본값 20, 최대 50 |
| categoryName | String | N | 카테고리명 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_READ",
  "message": "상품이 정상적으로 조회되었습니다.",
  "data": {
    "content": [
      {
        "productId": 1,
        "name": "가죽 지갑",
        "price": 30000,
        "thumbnail": "https://...",
        "productStatus": "ACTIVATED",
        "summary": "상품 요약",
        "categoryName": "가죽공예",
        "isMain": true,
        "viewCount": 0,
        "salesCount": 0
      }
    ]
  }
}
```

---

## 상품 단건 조회

### Method

GET

### Endpoint

```text
/api/products/{productId}
```

# Request

---

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| productId | Long | Y | 상품 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_READ",
  "message": "상품이 정상적으로 조회되었습니다.",
  "data": {
    "productId": 1,
    "name": "가죽 지갑",
    "price": 30000,
    "thumbnail": "https://...",
    "productStatus": "ACTIVATED",
    "summary": "상품 요약",
    "description": "상품 상세 설명",
    "viewCount": 1,
    "salesCount": 0,
    "categoryName": "가죽공예"
  }
}
```

## 404 Not Found

### 실패 조건

- 해당 상품이 존재하지 않는다.

---

# Cart API

## 장바구니 조회

### Method

GET

### Endpoint

```text
/api/carts
```

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CART_RETRIEVE_SUCCESS",
  "message": "장바구니 조회에 성공했습니다.",
  "data": {
    "cartId": 1,
    "userId": 1,
    "products": [],
    "totalPrice": 0
  }
}
```

## 400 Bad Request

### 실패 조건

- 사용자의 장바구니가 없다.

---

## 장바구니 생성

### Method

POST

### Endpoint

```text
/api/carts
```

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "CART_CREATED_SUCCESS",
  "message": "장바구니가 정상적으로 생성되었습니다.",
  "data": {
    "cartId": 1,
    "userId": 1,
    "products": [],
    "totalPrice": 0
  }
}
```

---

## 장바구니 상품 추가

### Method

POST

### Endpoint

```text
/api/carts/{cartId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| cartId | Long | Y | 장바구니 ID |

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| productId | Long | Y | 추가할 상품 ID |
| amount | Long | Y | 수량 |

#### 예제 코드

```json
{
  "productId": 1,
  "amount": 2
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CART_ADDITION_SUCCESS",
  "message": "장바구니에 상품이 정상적으로 추가되었습니다.",
  "data": {
    "cartId": 1,
    "products": [
      {
        "cartProductId": 1,
        "amount": 2,
        "productName": "가죽 지갑",
        "price": 30000,
        "totalPrice": 60000
      }
    ],
    "totalPrice": 60000
  }
}
```

---

## 장바구니 상품 수정

### Method

PUT

### Endpoint

```text
/api/carts/{cartId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| cartId | Long | Y | 장바구니 ID |

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| cartProductId | Long | Y | 장바구니 상품 ID |
| amount | Long | Y | 수정 수량, 0이면 삭제 |
| option | String | N | 상품 옵션 |

#### 예제 코드

```json
{
  "cartProductId": 1,
  "amount": 3,
  "option": "브라운"
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CART_UPDATE_SUCCESS",
  "message": "장바구니의 상품이 정상적으로 변경되었습니다.",
  "data": {
    "cartId": 1
  }
}
```

---

## 장바구니 삭제

### Method

DELETE

### Endpoint

```text
/api/carts/{cartId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| cartId | Long | Y | 장바구니 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CART_DELETE_SUCCESS",
  "message": "장바구니가 정상적으로 삭제되었습니다.",
  "data": null
}
```

---

# Order API

## 상품 단건 주문 생성

### Method

POST

### Endpoint

```text
/api/orders/product
```

### 설명

상품 상세 페이지에서 단건 주문을 생성한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| productId | Long | Y | 주문할 상품 ID |
| amount | Long | Y | 주문 수량 |
| option | String | N | 상품 옵션 |
| address | String | Y | 배송 주소 |
| postalCode | String | Y | 우편번호 |
| receiverName | String | N | 수령인 이름, 비어 있으면 주문자명 사용 |
| request | String | N | 배송 요청사항 |
| receiverPhoneNumber | String | Y | 수령인 전화번호 |

#### 예제 코드

```json
{
  "productId": 1,
  "amount": 2,
  "option": "브라운",
  "address": "서울시 강남구 테헤란로",
  "postalCode": "06234",
  "receiverName": "홍길동",
  "request": "문 앞에 놓아주세요.",
  "receiverPhoneNumber": "010-1234-5678"
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "ORDER_CREATED_SUCCESS",
  "message": "주문이 정상적으로 생성되었습니다.",
  "data": {
    "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
    "orderProducts": [],
    "totalPrice": 64000,
    "deliveryFee": 4000,
    "address": "서울시 강남구 테헤란로",
    "postalCode": "06234",
    "receiverName": "홍길동",
    "receiverPhoneNumber": "010-1234-5678",
    "phoneNumber": "010-1234-5678",
    "ordererName": "홍길동",
    "orderStatus": "ORDERED",
    "createdAt": "2026-07-10T10:00:00"
  }
}
```

## 404 Not Found

### 실패 조건

- 상품이 존재하지 않는다.
- 활성 배송정책이 없다.

---

## 장바구니 주문 생성

### Method

POST

### Endpoint

```text
/api/orders/cart
```

### 설명

장바구니에 담긴 상품들로 주문을 생성한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| address | String | Y | 배송 주소 |
| postalCode | String | Y | 우편번호 |
| receiverName | String | N | 수령인 이름 |
| request | String | N | 배송 요청사항 |
| receiverPhoneNumber | String | Y | 수령인 전화번호 |

#### 예제 코드

```json
{
  "address": "서울시 강남구 테헤란로",
  "postalCode": "06234",
  "receiverName": "홍길동",
  "request": "문 앞에 놓아주세요.",
  "receiverPhoneNumber": "010-1234-5678"
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "ORDER_CREATED_SUCCESS",
  "message": "주문이 정상적으로 생성되었습니다.",
  "data": {
    "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
    "totalPrice": 64000,
    "deliveryFee": 4000,
    "orderStatus": "ORDERED"
  }
}
```

## 400 Bad Request

### 실패 조건

- 장바구니가 없다.
- 장바구니가 비어 있다.

---

## 내 주문 상세 조회

### Method

GET

### Endpoint

```text
/api/orders/{orderNumber}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| orderNumber | UUID | Y | 주문 번호 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ORDER_READ_SUCCESS",
  "message": "주문이 정상적으로 조회되었습니다.",
  "data": {
    "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
    "totalPrice": 64000,
    "orderStatus": "ORDERED",
    "deliveryFee": 4000,
    "address": "서울시 강남구 테헤란로",
    "postalCode": "06234",
    "receiverName": "홍길동",
    "receiverPhoneNumber": "010-1234-5678",
    "orderProducts": []
  }
}
```

---

## 내 주문 목록 조회

### Method

GET

### Endpoint

```text
/api/orders/search
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| page | int | N | 페이지 번호, 기본값 0 |
| size | int | N | 페이지 크기, 기본값 20 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ORDER_READ_SUCCESS",
  "message": "주문이 정상적으로 조회되었습니다.",
  "data": {
    "content": [
      {
        "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
        "totalPrice": 64000,
        "orderStatus": "ORDERED"
      }
    ]
  }
}
```

---

## 주문 결제완료 상태 변경

### Method

PATCH

### Endpoint

```text
/api/orders/{orderNumber}/purchased
```

### 설명

주문 상태를 `PURCHASED`로 변경한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| orderNumber | UUID | Y | 주문 번호 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ORDER_PURCHASED",
  "message": "주문이 정상적으로 결제 상태로 전환 되었습니다.",
  "data": null
}
```

---

## 주문 취소

### Method

DELETE

### Endpoint

```text
/api/orders/{orderNumber}/canceled
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| orderNumber | UUID | Y | 주문 번호 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ORDER_CANCELED",
  "message": "주문이 정상적으로 취소 상태로 전환 되었습니다.",
  "data": null
}
```

---

# Payment API

## 결제 승인

### Method

POST

### Endpoint

```text
/api/payments/confirm
```

### 설명

토스 결제 승인 요청을 처리하고, 내부 결제 상태를 확정한다. `paymentType`에 따라 주문 결제 또는 예약 결제를 수행한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| paymentKey | String | Y | 토스 paymentKey |
| orderId | String | Y | 토스 orderId |
| orderNumber | String | N | 주문 UUID, 주문 결제 시 사용 |
| reservationNumber | String | N | 예약 UUID, 예약 결제 시 사용 |
| paymentType | PaymentType | N | `ORDER`, `RESERVATION`, 생략 시 `ORDER` |
| amount | Long | Y | 결제 금액 |

#### 주문 결제 예제

```json
{
  "paymentKey": "toss-payment-key",
  "orderId": "toss-order-id",
  "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
  "paymentType": "ORDER",
  "amount": 64000
}
```

#### 예약 결제 예제

```json
{
  "paymentKey": "toss-payment-key",
  "orderId": "toss-order-id",
  "reservationNumber": "550e8400-e29b-41d4-a716-446655440000",
  "paymentType": "RESERVATION",
  "amount": 10000
}
```

# Response

---

## 200 OK

### Body

```text
success
```

### 성공 조건

- 로그인 상태이다.
- 주문 결제의 경우 주문이 존재하고 결제 가능 상태이다.
- 예약 결제의 경우 예약이 존재하고 결제 준비 상태이다.
- 요청 금액과 서버 계산 금액이 일치한다.
- 토스 결제 승인 API 호출이 성공한다.

## 200 OK

### Body

```text
fail
```

### 실패 조건

- 결제 금액 불일치
- 토스 승인 실패
- 내부 결제 확정 처리 실패

---

## 결제 상세 조회

### Method

GET

### Endpoint

```text
/api/payments/{paymentId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| paymentId | Long | Y | 결제 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PAYMENT_READ_SUCCESS",
  "message": "결제 조회에 성공했습니다.",
  "data": {
    "paymentId": 1,
    "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
    "paymentStatus": "COMPLETED",
    "paymentMethod": "CARD",
    "pgReceiptUrl": "https://...",
    "requestedAt": "2026-07-10T10:00:00",
    "approvedAt": "2026-07-10T10:01:00"
  }
}
```

---

## 주문번호 기반 결제 상세 조회

### Method

GET

### Endpoint

```text
/api/payments/orders/{orderNumber}/detail
```

### 설명

구매 완료된 주문 결제 정보를 주문번호로 조회한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| orderNumber | UUID | Y | 주문 번호 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PAYMENT_READ_SUCCESS",
  "message": "결제 조회에 성공했습니다.",
  "data": {
    "paymentId": 1,
    "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
    "paymentStatus": "COMPLETED",
    "paymentMethod": "CARD",
    "pgReceiptUrl": "https://...",
    "requestedAt": "2026-07-10T10:00:00",
    "approvedAt": "2026-07-10T10:01:00"
  }
}
```

---

## 결제 목록 조회

### Method

GET

### Endpoint

```text
/api/payments
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| page | int | N | 페이지 번호, 기본값 0 |
| size | int | N | 페이지 크기, 기본값 20 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PAYMENT_READ_SUCCESS",
  "message": "결제 조회에 성공했습니다.",
  "data": {
    "content": [
      {
        "paymentId": 1,
        "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
        "paymentStatus": "COMPLETED",
        "paymentMethod": "CARD",
        "pgReceiptUrl": "https://...",
        "requestedAt": "2026-07-10T10:00:00",
        "approvedAt": "2026-07-10T10:01:00"
      }
    ]
  }
}
```

---

## 영수증 조회

### Method

GET

### Endpoint

```text
/api/payments/{paymentId}/receipt
```

### 설명

결제 완료된 결제 건의 영수증 데이터를 조회한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| paymentId | Long | Y | 결제 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PAYMENT_READ_SUCCESS",
  "message": "결제 조회에 성공했습니다.",
  "data": {
    "paymentId": 1,
    "paymentStatus": "COMPLETED",
    "paymentMethod": "CARD",
    "pgReceiptUrl": "https://..."
  }
}
```

## 400 Bad Request

### 실패 조건

- 결제 완료 상태가 아니어서 영수증을 조회할 수 없다.

---

## 결제 환불

### Method

DELETE

### Endpoint

```text
/api/payments/{paymentId}
```

### 설명

결제 건을 환불 처리하고 토스 결제 취소 API를 호출한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| paymentId | Long | Y | 결제 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PAYMENT_REFUND_COMPLETE",
  "message": "환불에 성공했습니다.",
  "data": null
}
```

---

# Reservation API

## 예약 생성

### Method

POST

### Endpoint

```text
/api/reservations
```

### 설명

예약 페이지에서 예약을 생성한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| date | LocalDate | Y | 예약 날짜 |
| startTime | LocalTime | Y | 예약 시작 시간 |
| endTime | LocalTime | Y | 예약 종료 시간 |
| deposit | Integer | Y | 예약금 |
| price | Integer | Y | 수업 가격 |
| request | String | N | 요청사항 |
| lessonId | Long | Y | 수업 ID |

#### 예제 코드

```json
{
  "date": "2026-07-20",
  "startTime": "10:00:00",
  "endTime": "13:00:00",
  "deposit": 10000,
  "price": 68000,
  "request": "초보자입니다.",
  "lessonId": 1
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "RESERVATION_CREATED",
  "message": "예약이 정상적으로 생성되었습니다.",
  "data": null
}
```

### 성공 조건

- 로그인 상태이다.
- 수업이 존재한다.
- 예약 정책에 맞는 날짜와 시간이다.
- 기존 예약과 시간이 겹치지 않는다.
- 요청한 예약금과 수업 가격이 서버 정책/수업 정보와 일치한다.

## 409 Conflict

### 실패 조건

- 이미 예약된 시간과 겹친다.

---

## 예약 가능 시간 조회

### Method

GET

### Endpoint

```text
/api/reservations
```

### 설명

특정 기간 내 이미 존재하는 예약 시간을 조회한다.

# Request

---

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| fromDate | LocalDate | Y | 조회 시작일 |
| toDate | LocalDate | Y | 조회 종료일 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "RESERVATION_READ",
  "message": "예약이 정상적으로 조회되었습니다.",
  "data": [
    {
      "id": 1,
      "date": "2026-07-20",
      "startTime": "10:00:00",
      "endTime": "13:00:00"
    }
  ]
}
```

---

## 내 예약 목록 조회

### Method

GET

### Endpoint

```text
/api/reservations/my/reservations
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| isOngoing | Boolean | N | 진행 중 예약만 조회 여부, 기본값 true |
| page | Integer | N | 페이지 번호, 기본값 1 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "RESERVATION_READ",
  "message": "예약이 정상적으로 조회되었습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "date": "2026-07-20",
        "startTime": "10:00:00",
        "endTime": "13:00:00",
        "deposit": 10000,
        "price": 68000,
        "request": "초보자입니다.",
        "reservationNumber": "550e8400-e29b-41d4-a716-446655440000",
        "status": "PENDING",
        "lesson": {
          "id": 1,
          "name": "레더 카드지갑 클래스",
          "price": 68000,
          "duration": 3
        }
      }
    ]
  }
}
```

---

## 예약 결제번호 조회

### Method

GET

### Endpoint

```text
/api/reservations/payment/{reservationId}
```

### 설명

예약 결제에 사용할 `reservationNumber`를 조회한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| reservationId | Long | Y | 예약 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "RESERVATION_READ",
  "message": "예약이 정상적으로 조회되었습니다.",
  "data": "550e8400-e29b-41d4-a716-446655440000"
}
```

## 409 Conflict

### 실패 조건

- 예약 상태가 `PENDING`이 아니다.

---

## 내 예약 상세 조회

### Method

GET

### Endpoint

```text
/api/reservations/my/reservations/{reservationId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| reservationId | Long | Y | 예약 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "RESERVATION_READ",
  "message": "예약이 정상적으로 조회되었습니다.",
  "data": {
    "id": 1,
    "date": "2026-07-20",
    "status": "PENDING"
  }
}
```

---

# Lesson API

## 수업 목록 조회

### Method

GET

### Endpoint

```text
/api/lessons
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "LESSON_READ",
  "message": "수업이 정상적으로 조회되었습니다.",
  "data": [
    {
      "id": 1,
      "name": "레더 카드지갑 클래스",
      "price": 68000,
      "duration": 3
    }
  ]
}
```

---

## 수업 단건 조회

### Method

GET

### Endpoint

```text
/api/lessons/{lessonId}
```

# Request

---

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| lessonId | Long | Y | 수업 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "LESSON_READ",
  "message": "수업이 정상적으로 조회되었습니다.",
  "data": {
    "id": 1,
    "name": "레더 카드지갑 클래스",
    "price": 68000,
    "duration": 3
  }
}
```

---

## 수업 정책 조회

### Method

GET

### Endpoint

```text
/api/lessons/policy
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "LESSON_POLICY_READ",
  "message": "수업정책이 정상적으로 조회되었습니다.",
  "data": {
    "reservationAvailableDays": 21,
    "reservationDeadlineDays": 3,
    "cancelDeadlineDays": 1,
    "depositAmount": 10000,
    "startTime": "09:00:00",
    "endTime": "18:00:00",
    "regularDays": ["SATURDAY", "SUNDAY"]
  }
}
```

---

# Shipping Policy API

## 현재 배송비 조회

### Method

GET

### Endpoint

```text
/api/shppingpolicy
```

### 설명

현재 활성 배송비를 조회한다. Endpoint의 `shppingpolicy`는 현재 코드 기준 오탈자를 그대로 따른다.

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "DELIVERY_FEE_READED_SUCCESS",
  "message": "활성화 중인 배송비를 조회하였습니다.",
  "data": {
    "currentDeliveryFee": 4000
  }
}
```

---

# Admin API

관리자 API는 관리자 권한이 필요하다.

## 대시보드 조회

### Method

GET

### Endpoint

```text
/api/admin/dashboard
```

# Request

---

### HEADER

- Authorization : Bearer ~

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ADMIN_DASHBOARD_READ",
  "message": "대시보드 정보가 정상적으로 조회되었습니다.",
  "data": {
    "todaySales": 0,
    "totalSales": 0,
    "todayOrderCount": 0,
    "paidOrderCount": 0,
    "inDeliveryOrderCount": 0,
    "totalUserCount": 0,
    "todayUserCount": 0,
    "activeProductCount": 0,
    "soldOutProductCount": 0,
    "recentOrders": [],
    "topProducts": []
  }
}
```

---

## 관리자 회원 목록 조회

### Method

GET

### Endpoint

```text
/api/admin/users
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| page | int | N | 페이지 번호, 기본값 0 |
| size | int | N | 페이지 크기, 기본값 20 |
| keyword | String | N | 이름/이메일/전화번호 검색어 |
| role | Role | N | `USER`, `ADMIN`, `SUPER_ADMIN` |
| gender | Gender | N | `MALE`, `FEMALE` |
| blacklisted | Boolean | N | 차단 여부 |
| includeDeleted | Boolean | N | 탈퇴 회원 포함 여부, 기본값 false |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_READ",
  "message": "계정이 정상적으로 조회되었습니다.",
  "data": {
    "content": [
      {
        "userId": 1,
        "userName": "홍길동",
        "email": "user@example.com",
        "phoneNumber": "010-1234-5678",
        "role": "USER",
        "gender": "MALE",
        "nationality": "대한민국",
        "isAuthLinked": false,
        "blacklisted": false
      }
    ]
  }
}
```

---

## 관리자 회원 상세 조회

### Method

GET

### Endpoint

```text
/api/admin/users/{userId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| userId | Long | Y | 회원 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_READ",
  "message": "계정이 정상적으로 조회되었습니다.",
  "data": {
    "userId": 1,
    "userName": "홍길동",
    "email": "user@example.com",
    "role": "USER",
    "blacklisted": false
  }
}
```

---

## 회원 권한 변경

### Method

PATCH

### Endpoint

```text
/api/admin/users/{userId}/role
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| userId | Long | Y | 회원 ID |

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| role | Role | Y | 변경할 역할 |

#### 예제 코드

```json
{
  "role": "ADMIN"
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_ROLE_UPDATED",
  "message": "계정 권한이 정상적으로 변경되었습니다.",
  "data": {
    "userId": 1,
    "role": "ADMIN"
  }
}
```

---

## 회원 블랙리스트 변경

### Method

PATCH

### Endpoint

```text
/api/admin/users/{userId}/blacklist
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| blacklisted | Boolean | Y | 차단 여부 |

#### 예제 코드

```json
{
  "blacklisted": true
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "USER_UPDATED",
  "message": "계정이 정상적으로 수정되었습니다.",
  "data": {
    "userId": 1,
    "blacklisted": true
  }
}
```

---

## 관리자 상품 생성

### Method

POST

### Endpoint

```text
/api/admin/products
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| name | String | N | 상품명, 최대 50자 |
| price | Long | Y | 상품 가격 |
| summary | String | N | 상품 요약 |
| description | String | N | 상품 상세 |
| categoryId | Long | Y | 카테고리 ID |

#### 예제 코드

```json
{
  "name": "가죽 지갑",
  "price": 30000,
  "summary": "핸드메이드 지갑",
  "description": "상품 상세 설명",
  "categoryId": 1
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "PRODUCT_CREATED_SUCCESS",
  "message": "상품이 정상적으로 생성되었습니다.",
  "data": {
    "productName": "가죽 지갑",
    "price": 30000,
    "productId": 1
  }
}
```

---

## 관리자 상품 목록 조회

### Method

GET

### Endpoint

```text
/api/admin/products
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| page | int | N | 페이지 번호 |
| size | int | N | 페이지 크기 |
| keyword | String | N | 상품명 검색어 |
| categoryName | String | N | 카테고리명 |
| status | ProductStatus | N | `ACTIVATED`, `SOLD_OUT`, `DEACTIVATED`, `DELETED` |
| isMain | Boolean | N | 메인 상품 여부 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_READ_SUCCESS",
  "message": "상품이 정상적으로 조회되었습니다.",
  "data": {
    "content": []
  }
}
```

---

## 관리자 상품 상세 조회

### Method

GET

### Endpoint

```text
/api/admin/products/{productId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| productId | Long | Y | 상품 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_READ_SUCCESS",
  "message": "상품이 정상적으로 조회되었습니다.",
  "data": {
    "productId": 1,
    "name": "가죽 지갑",
    "price": 30000,
    "status": "ACTIVATED",
    "categoryName": "가죽공예",
    "isMain": false
  }
}
```

---

## 관리자 상품 수정

### Method

PUT

### Endpoint

```text
/api/admin/products/{productId}
```

### 설명

상품 정보를 수정한다. 현재 컨트롤러는 `multipart/form-data`의 `request` part와 선택적 `file` part를 받는다.

# Request

---

### HEADER

- Authorization : Bearer ~
- Content-Type : multipart/form-data

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| productId | Long | Y | 상품 ID |

### Request Part

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| request | ProductUpdateRequest | Y | 상품 수정 데이터 |
| file | MultipartFile | N | 썸네일 파일 |

### ProductUpdateRequest

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| name | String | N | 상품명 |
| price | Long | N | 가격 |
| summary | String | N | 요약 |
| description | String | N | 상세 설명 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_UPDATE_SUCCESS",
  "message": "상품이 정상적으로 수정되었습니다.",
  "data": null
}
```

---

## 관리자 상품 삭제

### Method

DELETE

### Endpoint

```text
/api/admin/products/{productId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| productId | Long | Y | 상품 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_DELETE_SUCCESS",
  "message": "상품이 정상적으로 삭제되었습니다.",
  "data": null
}
```

---

## 관리자 상품 상태 변경

### Method

PATCH

### Endpoint

```text
/api/admin/products/{productId}/status
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| status | ProductStatus | Y | 변경할 상품 상태 |

#### 예제 코드

```json
{
  "status": "SOLD_OUT"
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_UPDATE_SUCCESS",
  "message": "상품이 정상적으로 수정되었습니다.",
  "data": null
}
```

---

## 관리자 메인 상품 등록

### Method

PATCH

### Endpoint

```text
/api/admin/products/{productId}/main
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| productId | Long | Y | 상품 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "PRODUCT_UPDATE_SUCCESS",
  "message": "상품이 정상적으로 수정되었습니다.",
  "data": null
}
```

---

## 관리자 카테고리 생성

### Method

POST

### Endpoint

```text
/api/admin/categories
```

### 설명

카테고리를 생성한다. `multipart/form-data`의 `request` part와 선택적 `file` part를 받는다.

# Request

---

### HEADER

- Authorization : Bearer ~
- Content-Type : multipart/form-data

### Request Part

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| request | CategoryRequest | Y | 카테고리 생성 데이터 |
| file | MultipartFile | N | 썸네일 파일 |

### CategoryRequest

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| name | String | Y | 카테고리명, 최대 20자 |

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "CATEGORY_CREATED",
  "message": "카테고리가 정상적으로 생성되었습니다.",
  "data": null
}
```

---

## 관리자 카테고리 목록 조회

### Method

GET

### Endpoint

```text
/api/admin/categories
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| page | int | N | 페이지 번호 |
| size | int | N | 페이지 크기 |
| keyword | String | N | 카테고리명 검색어 |
| includeDeleted | Boolean | N | 삭제 카테고리 포함 여부 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CATEGORY_READ",
  "message": "카테고리가 정상적으로 조회되었습니다.",
  "data": {
    "content": [
      {
        "categoryId": 1,
        "name": "가죽공예",
        "thumbnail": "https://...",
        "totalProductCount": 10,
        "activeProductCount": 8,
        "soldOutProductCount": 1,
        "mainProductCount": 1
      }
    ]
  }
}
```

---

## 관리자 카테고리 상세 조회

### Method

GET

### Endpoint

```text
/api/admin/categories/{categoryId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| categoryId | Long | Y | 카테고리 ID |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CATEGORY_READ",
  "message": "카테고리가 정상적으로 조회되었습니다.",
  "data": {
    "categoryId": 1,
    "name": "가죽공예",
    "thumbnail": "https://...",
    "totalProductCount": 10,
    "activeProductCount": 8,
    "soldOutProductCount": 1,
    "deactivatedProductCount": 1,
    "deletedProductCount": 0,
    "mainProductCount": 1
  }
}
```

---

## 관리자 카테고리 수정

### Method

PUT

### Endpoint

```text
/api/admin/categories/{categoryId}
```

# Request

---

### HEADER

- Authorization : Bearer ~
- Content-Type : multipart/form-data

### Request Part

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| request | CategoryRequest | Y | 카테고리 수정 데이터 |
| file | MultipartFile | N | 썸네일 파일 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CATEGORY_UPDATED",
  "message": "카테고리가 정상적으로 수정되었습니다.",
  "data": null
}
```

---

## 관리자 카테고리 삭제

### Method

DELETE

### Endpoint

```text
/api/admin/categories/{categoryId}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "CATEGORY_DELETED",
  "message": "카테고리가 정상적으로 삭제되었습니다.",
  "data": null
}
```

---

## 관리자 주문 목록 조회

### Method

GET

### Endpoint

```text
/api/admin/orders
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| page | int | N | 페이지 번호 |
| size | int | N | 페이지 크기 |
| status | OrderStatus | N | `ORDERED`, `PURCHASED`, `IN_DELIVERY`, `DELIVERED`, `COMPLETE`, `CANCELED` |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ORDER_READ_SUCCESS",
  "message": "주문이 정상적으로 조회되었습니다.",
  "data": {
    "content": [
      {
        "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
        "ordererName": "홍길동",
        "phoneNumber": "010-1234-5678",
        "totalPrice": 64000,
        "orderStatus": "PURCHASED"
      }
    ]
  }
}
```

---

## 관리자 주문 상세 조회

### Method

GET

### Endpoint

```text
/api/admin/orders/{orderNumber}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| orderNumber | UUID | Y | 주문 번호 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ORDER_READ_SUCCESS",
  "message": "주문이 정상적으로 조회되었습니다.",
  "data": {
    "orderNumber": "550e8400-e29b-41d4-a716-446655440000",
    "orderStatus": "PURCHASED",
    "totalPrice": 64000,
    "deliveryFee": 4000,
    "ordererName": "홍길동",
    "products": []
  }
}
```

---

## 관리자 주문 상태 변경

### Method

PATCH

### Endpoint

```text
/api/admin/orders/{orderNumber}/indelivery
/api/admin/orders/{orderNumber}/delivered
/api/admin/orders/{orderNumber}/complete
/api/admin/orders/{orderNumber}/cancel
```

### 설명

주문 상태를 배송중, 배송완료, 주문완료, 취소 상태로 변경한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| orderNumber | UUID | Y | 주문 번호 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "ORDER_IN_DELIVERY",
  "message": "주문이 정상적으로 배송 중 상태로 전환 되었습니다.",
  "data": null
}
```

---

## 관리자 예약 목록 조회

### Method

GET

### Endpoint

```text
/api/admin/reservations
```

### 설명

관리자가 예약 목록을 날짜 또는 주차 기준으로 조회한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| date | LocalDate | N | 특정 날짜 조회 |
| year | Integer | N | 주차 조회 연도 |
| month | Integer | N | 주차 조회 월 |
| week | Integer | N | 주차 조회 주차 |
| status | String | N | 예약 상태, 기본값 `ALL` |
| page | Integer | N | 페이지 번호, 기본값 1 |

### 요청 조건

- `date`가 없으면 `year`, `month`, `week`가 모두 필요하다.

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "RESERVATION_READ",
  "message": "예약이 정상적으로 조회되었습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "date": "2026-07-20",
        "startTime": "10:00:00",
        "endTime": "13:00:00",
        "deposit": 10000,
        "price": 68000,
        "reservationNumber": "550e8400-e29b-41d4-a716-446655440000",
        "state": "PENDING",
        "customer": {},
        "lesson": {}
      }
    ]
  }
}
```

---

## 관리자 예약 상태 변경

### Method

PATCH

### Endpoint

```text
/api/admin/reservations/status/{reservationId}
```

### 설명

관리자가 예약 상태를 `COMPLETED` 또는 `NO_SHOW`로 변경한다.

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| reservationId | Long | Y | 예약 ID |

### Request Parameter

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| status | ReservationStatus | Y | `COMPLETED`, `NO_SHOW` |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "RESERVATION_UPDATED",
  "message": "예약이 정상적으로 수정되었습니다.",
  "data": null
}
```

---

## 관리자 수업 생성

### Method

POST

### Endpoint

```text
/api/admin/lessons
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| name | String | Y | 수업명 |
| price | Integer | Y | 수업 가격 |
| duration | Integer | Y | 수업 시간 |

#### 예제 코드

```json
{
  "name": "레더 카드지갑 클래스",
  "price": 68000,
  "duration": 3
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "LESSON_CREATED",
  "message": "수업이 정상적으로 생성되었습니다.",
  "data": null
}
```

---

## 관리자 수업 수정

### Method

PUT

### Endpoint

```text
/api/admin/lessons/{lessonId}
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| lessonId | Long | Y | 수업 ID |

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| name | String | Y | 수업명 |
| price | Integer | Y | 수업 가격 |
| duration | Integer | Y | 수업 시간 |

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "LESSON_UPDATED",
  "message": "수업이 정상적으로 수정되었습니다.",
  "data": null
}
```

---

## 관리자 수업 삭제

### Method

DELETE

### Endpoint

```text
/api/admin/lessons/{lessonId}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "LESSON_DELETED",
  "message": "수업이 정상적으로 삭제되었습니다.",
  "data": null
}
```

---

## 관리자 수업 정책 수정

### Method

PUT

### Endpoint

```text
/api/admin/lessons/policy
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| reservationAvailableDays | Integer | Y | 예약 가능 기간 |
| reservationDeadlineDays | Integer | Y | 최소 예약 가능 기간 |
| cancelDeadlineDays | Integer | Y | 최소 예약 취소 기간 |
| depositAmount | Integer | Y | 예약금 |
| startTime | LocalTime | Y | 오픈 시간 |
| endTime | LocalTime | Y | 마감 시간 |
| regularDays | Set<DayOfWeek> | Y | 정기 휴무일 |

#### 예제 코드

```json
{
  "reservationAvailableDays": 21,
  "reservationDeadlineDays": 3,
  "cancelDeadlineDays": 1,
  "depositAmount": 10000,
  "startTime": "09:00:00",
  "endTime": "18:00:00",
  "regularDays": ["SATURDAY", "SUNDAY"]
}
```

# Response

---

## 200 OK

```json
{
  "success": true,
  "code": "LESSON_POLICY_UPDATED",
  "message": "수업정책이 정상적으로 수정되었습니다.",
  "data": null
}
```

---

## 관리자 배송비 정책 생성

### Method

POST

### Endpoint

```text
/api/admin/shippingpolicy
```

# Request

---

### HEADER

- Authorization : Bearer ~

### Request Body

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| deliveryFee | Integer | Y | 기본 배송비 |
| extraFee | Integer | N | 추가 배송비 |

#### 예제 코드

```json
{
  "deliveryFee": 3000,
  "extraFee": 1000
}
```

# Response

---

## 201 Created

```json
{
  "success": true,
  "code": "DELIVERY_FEE_SAVEED_SUCCESS",
  "message": "배송비를 저장하였습니다.",
  "data": {
    "currentDeliveryFee": 4000
  }
}
```

---

# Enum Reference

| 이름 | 값 |
| --- | --- |
| Role | `USER`, `ADMIN`, `SUPER_ADMIN` |
| Gender | `MALE`, `FEMALE` |
| ProductStatus | `ACTIVATED`, `SOLD_OUT`, `DEACTIVATED`, `DELETED` |
| OrderStatus | `ORDERED`, `PURCHASED`, `IN_DELIVERY`, `DELIVERED`, `COMPLETE`, `CANCELED` |
| PaymentType | `ORDER`, `RESERVATION` |
| PaymentStatus | `PENDING`, `COMPLETED`, `FAILED`, `CANCELED`, `REFUNDED` |
| ReservationStatus | `PENDING`, `APPROVED`, `COMPLETED`, `CANCELED`, `NO_SHOW` |

