# android-shopping-order
# Step 1
## 기능 요구 사항
- [x] 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.
- [x] 서버의 장바구니 데이터를 조회한다.
- [x] 서버의 장바구니 데이터의 상품 개수를 업데이트 한다.
- [x] 서버의 장바구니에 아이템을 추가한다.
- [x] 서버의 장바구니에 아이템을 삭제한다.

## 프로그래밍 요구 사항
- [x] 사용자 인증 정보를 저장한다.
  
- [x] 메인 액티비티가 로딩되기 전, 서버를 선택하여 교체할 수 있다.
  - [x] 서버를 선택할 수 있는 액티비티를 만든다.

# Step 2
## 기능 요구 사항
- [x] 서버 통신을 Retrofit으로 리팩터링한다.

- [ ] 주문 목록 화면으로 이동할 수 있다.
  - [x] 툴바에서 아이콘을 추가한다.
  - [ ] 아이콘을 클릭하면 주문 목록 화면으로 이동한다.

- [x] 장바구니에 담은 상품을 주문할 수 있다.
  - [x] 주문하기 버튼을 클릭하면 선택한 상품의 cart id를 주문하기 화면으로 넘긴다.
  - [x] 주문하기 버튼을 클릭하면 주문 화면으로 이동한다.

- [x] 상품 주문 시 현실 세계의 쇼핑 서비스가 제공하는 재화 관련 요소를 최소 1가지 이상 추가한다.
  - [x] 재화는 포인트로 한다.
  - [x] 사용 가능한 포인트는 서버에서 받아온다.
  - [x] 예상 적립 포인트를 서버에서 받아온다.

- [x] 주문 화면
  - [x] 선택한 장바구니 아이템을 보여준다.
  - [x] 사용 가능한 포인트를 서버에서 받아와 보여준다.
  - [x] 예상 적립 포인트를 서버에서 받아와 보여준다.
  - [x] 총 금액을 보여준다.
  - [x] 입력한 사용 포인트를 보여준다.
  - [x] 총 금액에서 사용 포인트를 뺀 최종 결제 금액을 보여준다.
  - [x] 주문하기 버튼을 클릭하면 주문이 서버로 전송된다.
  - [x] 주문이 완료되면 상세 주문 페이지로 넘어간다.

- [ ] 사용자 별로 주문 목록을 확인할 수 있다.
  - [ ] 주문 목록 화면에서 전체 주문 목록을 확인할 수 있다.
    - [x] 주문 날짜를 확인할 수 있다.
    - [x] 주문한 상품 목록을 확인할 수 있다.
    - [ ] 결제 상세 내역을 확인할 수 있다.
    - [ ] 결제금액을 확인할 수 있다.

- [ ] 특정 주문의 상세 정보를 확인할 수 있다.
  - [ ] 상세보기 버튼을 클릭하면 상세한 주문 내역을 확인할 수 있다.