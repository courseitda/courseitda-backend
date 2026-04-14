# REST API 명세

## 에러 처리

**일반적인 에러 상태 코드:**

- `400 Bad Request`: 잘못된 요청 (유효성 검증 실패 등)
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스를 찾을 수 없음
- `409 Conflict`: 중복 등의 충돌
- `500 Internal Server Error`: 서버 내부 오류

---

## 1. 인증 (Auth)

### 1.1 로그인

이메일과 비밀번호로 로그인하여 액세스 토큰을 발급받습니다. 발급받은 토큰은 이후 인증이 필요한 API 호출 시 `Authorization` 헤더에 포함하여 사용합니다.

```http
POST /api/auth/login HTTP/1.1
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 2. 회원 (Member)

### 2.1 회원가입

새로운 회원을 등록합니다. 닉네임과 이메일은 중복 확인 API를 통해 사전에 검증할 수 있습니다.

```http
POST /api/members HTTP/1.1
Content-Type: application/json

{
  "nickname": "닉네임",
  "email": "user@example.com",
  "password": "password123"
}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "nickname": "닉네임",
  "email": "user@example.com"
}
```

### 2.2 닉네임 중복 확인

회원가입 시 사용할 닉네임의 중복 여부를 확인합니다.

```http
GET /api/members/validations/nickname?value=닉네임 HTTP/1.1
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "isDuplicated": false
}
```

### 2.3 이메일 중복 확인

회원가입 시 사용할 이메일의 중복 여부를 확인합니다.

```http
GET /api/members/validations/email?value=user@example.com HTTP/1.1
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "isDuplicated": false
}
```

---

## 3. 내 정보 (Me)

### 3.1 네비게이터 정보 조회

네비게이션 영역에 표시할 현재 로그인한 사용자의 닉네임을 조회합니다.

```http
GET /api/me/navigator HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "nickname": "닉네임"
}
```

### 3.2 드롭다운 정보 조회

드롭다운 메뉴에 표시할 현재 로그인한 사용자의 닉네임과 이메일을 조회합니다.

```http
GET /api/me/dropdown HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "nickname": "닉네임",
  "email": "user@example.com"
}
```

### 3.3 프로필 조회

프로필 페이지에 표시할 현재 로그인한 사용자의 닉네임과 이메일을 조회합니다.

```http
GET /api/me/profile HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "nickname": "닉네임",
  "email": "user@example.com"
}
```

### 3.4 내 워크스페이스 목록 조회

현재 로그인한 사용자가 소유한 워크스페이스 목록을 커서 기반 페이징으로 조회합니다.

```http
GET /api/me/workspaces?cursor={cursor}&size={size} HTTP/1.1
Authorization: Bearer {accessToken}
```

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| cursor | Long | X | - | 이전 응답의 nextCursor 값 |
| size | int | X | 10 | 조회 개수 (1~100) |

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "workspaces": [
    {
      "identifier": "abc123",
      "title": "워크스페이스 제목",
      "modifiedAt": "2025-10-31T12:00:00+09:00"
    }
  ],
  "hasNext": true,
  "nextCursor": 5
}
```

### 3.5 내 보관 카테고리 목록 조회

현재 로그인한 사용자가 보관한 카테고리 목록을 커서 기반 페이징으로 조회합니다.

```http
GET /api/me/saved-categories?cursor={cursor}&size={size} HTTP/1.1
Authorization: Bearer {accessToken}
```

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| cursor | Long | X | - | 이전 응답의 nextCursor 값 |
| size | int | X | 10 | 조회 개수 (1~100) |

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "savedCategories": [
    {
      "id": 1,
      "name": "보관 카테고리 이름",
      "placeCount": 3,
      "modifiedAt": "2025-10-31T12:00:00+09:00"
    }
  ],
  "hasNext": true,
  "nextCursor": 1
}
```

### 3.6 내 공유 카테고리 목록 조회

현재 로그인한 사용자가 공유한 카테고리 목록을 커서 기반 페이징으로 조회합니다.

```http
GET /api/me/shared-categories?cursor={cursor}&size={size} HTTP/1.1
Authorization: Bearer {accessToken}
```

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| cursor | Long | X | - | 이전 응답의 nextCursor 값 |
| size | int | X | 10 | 조회 개수 (1~100) |

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "sharedCategories": [
    {
      "id": 1,
      "name": "공유 카테고리 이름",
      "createdAt": "2025-10-31T12:00:00+09:00",
      "placeCount": 3,
      "likeCount": 12
    }
  ],
  "hasNext": false,
  "nextCursor": null
}
```

### 3.7 내가 찜한 공유 카테고리 목록 조회

현재 로그인한 사용자가 찜한 공유 카테고리 목록을 커서 기반 페이징으로 조회합니다. 커서는 찜(SharedCategoryLike) ID 기준입니다.

```http
GET /api/me/liked-shared-categories?cursor={cursor}&size={size} HTTP/1.1
Authorization: Bearer {accessToken}
```

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| cursor | Long | X | - | 이전 응답의 nextCursor 값 |
| size | int | X | 10 | 조회 개수 (1~100) |

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "sharedCategories": [
    {
      "id": 1,
      "name": "공유 카테고리 이름",
      "createdAt": "2025-10-31T12:00:00+09:00",
      "placeCount": 3,
      "likeCount": 12,
      "isDeleted": false
    }
  ],
  "hasNext": false,
  "nextCursor": null
}
```

---

### 3.8 내가 찜한 공유 카테고리 ID 조회

주어진 공유 카테고리 ID 목록 중 현재 로그인한 사용자가 찜한 항목의 ID 목록을 반환합니다.

```http
GET /api/me/liked-shared-categories/contains?sharedCategoryIds=1,2,3 HTTP/1.1
Authorization: Bearer {accessToken}
```

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| sharedCategoryIds | List\<Long\> | O | 찜 여부를 확인할 공유 카테고리 ID 목록 |

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "likedSharedCategoryIds": [1, 3]
}
```

---

## 4. 장소 검색 (Place Search)

### 4.1 장소 검색

키워드로 장소를 검색합니다. 검색 결과에는 장소명, 주소, 좌표 정보가 포함됩니다.

```http
GET /api/places/search?keyword=강남역 HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "searchedPlaces": [
    {
      "name": "장소 이름",
      "url": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.5665,
      "longitude": 126.9780
    }
  ]
}
```

---

## 5. 워크스페이스 (Workspace)

### 5.1 워크스페이스 생성

새로운 워크스페이스를 생성합니다. 생성된 워크스페이스는 고유한 식별자(identifier)를 가집니다.

```http
POST /api/workspaces HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "title": "워크스페이스 제목"
}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "identifier": "abc123",
  "title": "워크스페이스 제목",
  "modifiedAt": "2025-10-31T12:00:00+09:00"
}
```

### 5.2 워크스페이스 조회

특정 워크스페이스의 상세 정보를 조회합니다.

```http
GET /api/workspaces/{workspaceIdentifier} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "identifier": "abc123",
  "title": "워크스페이스 제목",
  "modifiedAt": "2025-10-31T12:00:00+09:00"
}
```

### 5.3 워크스페이스 제목 수정

워크스페이스의 제목을 수정합니다.

```http
PATCH /api/workspaces/{workspaceIdentifier} HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "title": "새로운 제목"
}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "identifier": "abc123",
  "title": "새로운 제목",
  "modifiedAt": "2025-10-31T12:30:00+09:00"
}
```

### 5.4 워크스페이스 삭제

워크스페이스를 삭제합니다. 워크스페이스에 포함된 모든 카테고리와 장소 정보도 함께 삭제됩니다.

```http
DELETE /api/workspaces/{workspaceIdentifier} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 204 No Content
```

### 5.5 워크스페이스 제목 중복 확인

현재 사용자의 워크스페이스 중 동일한 제목이 존재하는지 확인합니다.

```http
GET /api/workspaces/validations/title?value=워크스페이스제목 HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "isDuplicated": false
}
```

### 5.6 카테고리 생성

특정 워크스페이스에 새로운 카테고리를 생성합니다. 카테고리는 장소를 그룹화하는 단위입니다.

```http
POST /api/workspaces/{workspaceIdentifier}/categories HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "카테고리 이름",
  "color": "#FF5733"
}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "name": "카테고리 이름",
  "color": "#FF5733",
  "sequence": 1
}
```

### 5.7 카테고리 목록 조회

특정 워크스페이스의 모든 카테고리 목록과 각 카테고리에 포함된 장소 정보를 조회합니다.

```http
GET /api/workspaces/{workspaceIdentifier}/categories HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "categories": [
    {
      "id": 1,
      "name": "카테고리 이름",
      "color": "#FF5733",
      "sequence": 1,
      "representativePlaceId": 5,
      "categoryPlaces": {
        "categoryPlaces": [
          {
            "id": 5,
            "name": "장소 이름",
            "placeUrl": "카카오 장소 URL",
            "addressName": "지번 주소",
            "roadAddressName": "도로명 주소",
            "latitude": 37.5665,
            "longitude": 126.9780,
            "isRepresentative": true
          }
        ]
      }
    }
  ]
}
```

### 5.8 카테고리 순서 변경

워크스페이스 내 카테고리들의 표시 순서를 변경합니다. 드래그 앤 드롭 등의 UI에서 사용됩니다.

```http
POST /api/workspaces/{workspaceIdentifier}/categories/sequence HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "categories": [
    {
      "id": 1,
      "sequence": 2
    },
    {
      "id": 2,
      "sequence": 1
    }
  ]
}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "categories": [
    {
      "id": 1,
      "sequence": 2
    },
    {
      "id": 2,
      "sequence": 1
    }
  ]
}
```

---

## 6. 카테고리 (Category)

### 6.1 카테고리 조회

특정 카테고리의 상세 정보와 포함된 장소 목록을 조회합니다.

```http
GET /api/categories/{categoryId} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "카테고리 이름",
  "color": "#FF5733",
  "sequence": 1,
  "representativePlaceId": 5,
  "categoryPlaces": {
    "categoryPlaces": [
      {
        "id": 5,
        "name": "장소 이름",
        "placeUrl": "카카오 장소 URL",
        "addressName": "지번 주소",
        "roadAddressName": "도로명 주소",
        "latitude": 37.5665,
        "longitude": 126.9780,
        "isRepresentative": true
      }
    ]
  }
}
```

### 6.2 카테고리 수정

카테고리의 이름과 색상을 수정합니다.

```http
PATCH /api/categories/{categoryId} HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "새로운 카테고리 이름",
  "color": "#00FF00"
}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "새로운 카테고리 이름",
  "color": "#00FF00"
}
```

### 6.3 카테고리 삭제

카테고리를 삭제합니다. 카테고리에 포함된 모든 장소 정보도 함께 삭제됩니다.

```http
DELETE /api/categories/{categoryId} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 204 No Content
```

### 6.4 대표 장소 설정

카테고리의 대표 장소를 설정합니다. 대표 장소는 카테고리를 대표하는 장소로 표시됩니다.

```http
PUT /api/categories/{categoryId}/representative-place HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "categoryPlaceId": 5
}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "representativeCategoryPlaceId": 5
}
```

### 6.5 대표 장소 삭제

카테고리에 설정된 대표 장소를 해제합니다.

```http
DELETE /api/categories/{categoryId}/representative-place HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 204 No Content
```

---

## 7. 카테고리 장소 (Category Place)

### 7.1 카테고리 장소 생성

특정 카테고리에 새로운 장소를 추가합니다.

```http
POST /api/categories/{categoryId}/places HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "장소 이름",
  "placeUrl": "카카오 장소 URL",
  "roadAddressName": "도로명 주소",
  "addressName": "지번 주소",
  "latitude": 37.5665,
  "longitude": 126.9780
}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 5,
  "placeId": 10,
  "name": "장소 이름",
  "placeUrl": "카카오 장소 URL",
  "roadAddressName": "도로명 주소",
  "addressName": "지번 주소",
  "latitude": 37.5665,
  "longitude": 126.9780
}
```

### 7.2 카테고리 장소 목록 조회

특정 카테고리에 포함된 모든 장소 목록을 조회합니다.

```http
GET /api/categories/{categoryId}/places HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "categoryPlaceResponses": [
    {
      "id": 5,
      "name": "장소 이름",
      "placeUrl": "카카오 장소 URL",
      "addressName": "지번 주소",
      "roadAddressName": "도로명 주소",
      "latitude": 37.5665,
      "longitude": 126.9780,
      "isRepresentative": true
    }
  ]
}
```

### 7.3 카테고리 장소 삭제

카테고리에서 특정 장소를 삭제합니다.

```http
DELETE /api/categories/{categoryId}/places/{categoryPlaceId} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 204 No Content
```

---

## 8. 보관 카테고리 (Saved Category)

### 8.1 보관 카테고리 생성

새로운 보관 카테고리를 이름만으로 생성합니다. 최초 장소 입력은 생성 후 8.5 장소 추가 API를 사용하고, 이후 전체 목록 수정은 8.6 장소 동기화 API를 사용합니다.

```http
POST /api/saved-categories HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "보관 카테고리 이름"
}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "name": "보관 카테고리 이름"
}
```

### 8.2 보관 카테고리 단건 조회

특정 보관 카테고리의 상세 정보와 포함된 장소 목록을 조회합니다.

```http
GET /api/saved-categories/{savedCategoryId} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "보관 카테고리 이름",
  "savedCategoryPlaces": [
    {
      "id": 1,
      "name": "장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.5665,
      "longitude": 126.9780
    }
  ]
}
```

### 8.3 보관 카테고리 수정

보관 카테고리의 이름을 수정합니다. 장소 수정은 8.6 장소 동기화 API를 사용합니다.

```http
PATCH /api/saved-categories/{savedCategoryId} HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "새로운 카테고리 이름"
}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "새로운 카테고리 이름"
}
```

### 8.4 보관 카테고리 삭제

보관 카테고리를 삭제합니다. 포함된 모든 장소 정보도 함께 삭제됩니다.

```http
DELETE /api/saved-categories/{savedCategoryId} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 204 No Content
```

### 8.5 보관 카테고리 초기 장소 추가

보관 카테고리 생성 직후 최초로 장소 목록을 입력할 때 사용합니다. 기존 장소를 유지하거나 삭제하는 동기화 목적이 아니라, 새 장소를 추가하는 API입니다.

```http
POST /api/saved-categories/{savedCategoryId}/places HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "savedCategoryPlaces": [
    {
      "name": "장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.5665,
      "longitude": 126.9780
    }
  ]
}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "savedCategoryPlaces": [
    {
      "id": 1,
      "name": "장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.5665,
      "longitude": 126.9780
    }
  ]
}
```

### 8.6 보관 카테고리 장소 동기화

이미 장소가 있는 보관 카테고리의 전체 목록을 최종 상태 기준으로 반영할 때 사용합니다. `savedCategoryPlaceId`가 있으면 기존 장소 유지, `null`이면 새 장소로 추가됩니다. 요청에 포함되지 않은 기존 장소는 삭제됩니다.

```http
PATCH /api/saved-categories/{savedCategoryId}/places HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "savedCategoryPlaces": [
    {
      "savedCategoryPlaceId": 1,
      "name": "기존 장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.5665,
      "longitude": 126.9780
    },
    {
      "savedCategoryPlaceId": null,
      "name": "새 장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.1234,
      "longitude": 127.1234
    }
  ]
}
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "savedCategoryPlaces": [
    {
      "id": 1,
      "name": "기존 장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.5665,
      "longitude": 126.9780
    },
    {
      "id": 2,
      "name": "새 장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.1234,
      "longitude": 127.1234
    }
  ]
}
```

---

## 9. 공유 카테고리 (Shared Category)

### 9.1 공유 카테고리 생성

보관 카테고리를 기반으로 공유 카테고리를 생성합니다. 보관 카테고리의 이름과 장소 목록이 그대로 복사됩니다.

```http
POST /api/shared-categories HTTP/1.1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "savedCategoryId": 1
}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "name": "공유 카테고리 이름"
}
```

### 9.2 전체 공유 카테고리 목록 조회

공유된 모든 카테고리 목록을 최신순으로 조회합니다. 커서 기반 페이지네이션을 지원합니다.

```http
GET /api/shared-categories?cursor=50&size=10 HTTP/1.1
```

| 파라미터 | 필수 여부 | 기본값 | 설명 |
|---|---|---|---|
| cursor | 선택 | - | 이전 응답의 `nextCursor` 값. 없으면 첫 페이지 |
| size | 선택 | 10 | 한 번에 조회할 개수 (1~100) |

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "sharedCategories": [
    {
      "id": 1,
      "name": "공유 카테고리 이름",
      "authorNickname": "닉네임",
      "createdAt": "2025-10-31T12:00:00+09:00",
      "placeCount": 3,
      "likeCount": 12
    }
  ],
  "hasNext": true,
  "nextCursor": 42
}
```

### 9.3 공유 카테고리 검색

카테고리 이름으로 공유 카테고리를 검색합니다. 커서 기반 페이지네이션을 지원합니다.

```http
GET /api/shared-categories/search?keyword=맛집&cursor=50&size=10 HTTP/1.1
```

| 파라미터 | 필수 여부 | 기본값 | 설명 |
|---|---|---|---|
| keyword | 필수 | - | 검색할 카테고리 이름 (공백만 입력 시 400 오류) |
| cursor | 선택 | - | 이전 응답의 `nextCursor` 값. 없으면 첫 페이지 |
| size | 선택 | 10 | 한 번에 조회할 개수 (1~100) |

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "sharedCategories": [
    {
      "id": 1,
      "name": "공유 카테고리 이름",
      "authorNickname": "닉네임",
      "createdAt": "2025-10-31T12:00:00+09:00",
      "placeCount": 3,
      "likeCount": 12
    }
  ],
  "hasNext": true,
  "nextCursor": 42
}
```

### 9.4 공유 카테고리 단건 조회

특정 공유 카테고리의 상세 정보와 포함된 장소 목록을 조회합니다.

```http
GET /api/shared-categories/{sharedCategoryId} HTTP/1.1
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "공유 카테고리 이름",
  "authorNickname": "닉네임",
  "createdAt": "2025-10-31T12:00:00+09:00",
  "likeCount": 12,
  "sharedCategoryPlaces": [
    {
      "id": 1,
      "name": "장소 이름",
      "placeUrl": "카카오 장소 URL",
      "roadAddressName": "도로명 주소",
      "addressName": "지번 주소",
      "latitude": 37.5665,
      "longitude": 126.9780
    }
  ]
}
```

### 9.5 공유 카테고리 삭제

공유 카테고리를 삭제합니다. 본인이 생성한 공유 카테고리만 삭제할 수 있습니다.

```http
DELETE /api/shared-categories/{sharedCategoryId} HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 204 No Content
```

---

## 10. 공유 카테고리 찜 (Shared Category Like)

### 10.1 공유 카테고리 찜 생성

공유 카테고리를 찜합니다.

```http
POST /api/shared-categories/{sharedCategoryId}/likes HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1
}
```

### 10.2 공유 카테고리 찜 삭제

공유 카테고리 찜을 취소합니다.

```http
DELETE /api/shared-categories/{sharedCategoryId}/likes HTTP/1.1
Authorization: Bearer {accessToken}
```

**성공 응답:**

```http
HTTP/1.1 204 No Content
```

---

## 11. 추천 카테고리 (Recommended Category)

### 11.1 추천 카테고리 목록 조회

관리자가 피처링한 추천 카테고리 목록을 최신순으로 조회합니다. 각 추천 카테고리는 공유 카테고리를 참조합니다.

```http
GET /api/recommended-categories HTTP/1.1
```

**성공 응답:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "recommendedCategories": [
    {
      "id": 1,
      "imageUrl": "https://courseitda-bucket.s3.ap-northeast-2.amazonaws.com/recommended/hongdae.jpg",
      "sharedCategoryId": 10,
      "name": "홍대 맛집 코스",
      "authorNickname": "닉네임",
      "createdAt": "2025-10-31T12:00:00+09:00",
      "placeCount": 5,
      "likeCount": 23
    }
  ]
}
```

---

## API 통계

- **전체 엔드포인트**: 43개
- **HTTP 메서드별**:
    - GET: 21개
    - POST: 10개
    - PATCH: 4개
    - DELETE: 7개
    - PUT: 1개
- **인증 필요**: 34개
- **공개 엔드포인트**: 8개
