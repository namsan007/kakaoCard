package com.kakaopay.exception;

/**
 * 결제취소 API 호출시 취소 실패되는 경우 Exception
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
public class CancelFailExcn extends RuntimeException {

	private static final long serialVersionUID = -4033940626385122270L;
	
	public CancelFailExcn(String msg) {
		super(msg);
	}
}

