package com.kakaopay.exception;

/**
 * 데이터 조회 API 호출시 결과가 없는경우 Exception
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
public class NoHavResultExcn extends RuntimeException {


	private static final long serialVersionUID = -8055719103517937211L;

	public NoHavResultExcn(String msg) {
		super(msg);
	}

}
