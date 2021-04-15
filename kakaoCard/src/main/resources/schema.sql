CREATE TABLE PAY_INFO_ENTY ( 
  pay_mgn_no 	CHAR(20) 		NOT NULL  			-- 관리번호
  ,card_info 	VARCHAR(100) 	NOT NULL 			-- 카드정보 (카드번호,유효기간,구분자) 암호화
  ,pay_amt 		INT 			NOT NULL	 		-- 결제_취소금액(100원 이상, 10억원 이하, 숫자)
  ,vat_amt 		INT 			NOT NULL DEFAULT 0 	-- 부가가치세(계산해서 생성)
  ,pay_msg 		VARCHAR(10) 	NOT NULL 			-- 결제/취소 구분 메시지
  ,ins_mth 		VARCHAR(2) 		NOT NULL 			-- 할부개월수
  ,pay_sts 		VARCHAR(10) 	NOT NULL 			-- 진행중, 완료 상태메세지
  ,input_dt 	DATETIME  							-- 입력일시
);

CREATE TABLE CANCEL_INFO_ENTY ( 
  cancel_mgn_no	CHAR(20) 		NOT NULL  			-- 취소 관리번호
  ,pay_mgn_no	CHAR(20) 		NOT NULL  			-- 관리번호
  ,card_info 	VARCHAR(100) 	NOT NULL 			-- 카드정보 (카드번호,유효기간,구분자) 암호화
  ,pay_amt 		INT 			NOT NULL	 		-- 결제_취소금액(100원 이상, 10억원 이하, 숫자)
  ,vat_amt 		INT 			NOT NULL DEFAULT 0	-- 부가가치세(계산해서 생성)
  ,pay_msg 		VARCHAR(10) 	NOT NULL 			-- 결제/취소 구분 메시지
  ,pay_sts 		VARCHAR(10) 	NOT NULL 			-- 진행중, 완료 상태메세지
  ,input_dt 	DATETIME  							-- 입력일시
);

CREATE TABLE FULL_TEXT_ENTY ( 
   mgn_no 		CHAR(20) 		NOT NULL  			-- 관리번호
  ,full_text 	VARCHAR(450) 	NOT NULL 			-- FULL_TEXT
  ,input_dt 	DATETIME  							-- 입력일시
);


CREATE TABLE PAY_UNIQ_SEQ_ENTY (
  pay_uniq_seq BIGINT AUTO_INCREMENT NOT NULL 		-- 시퀀스
  ,input_dt DATETIME 								-- 날짜
);