package com.kakaopay.exception;

/**
 * 제약조건
 * 1. 결제 : 하나의 카드번호로 동시에 결제를 할 수 없습니다.
 * 2. 전체취소 : 결제 한 건에 대한 전체취소를 동시에 할 수 없습니다.
 * 3. 부분취소 : 결제 한 건에 대한 부분취소를 동시에 할 수 없습니다.
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
public class CstsPayExcn extends RuntimeException {
	
	private static final long serialVersionUID = 7316958519521706516L;
	
	public CstsPayExcn(String msg) {
		super(msg);
	}

}
