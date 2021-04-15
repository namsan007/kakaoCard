package com.kakaopay.exception;

import javax.validation.ValidationException;

/**
 * 요청 값이 잘못 되었을 경우
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
public class PayReqValidateExcn extends ValidationException {

	private static final long serialVersionUID = 8573841449920441309L;

	public PayReqValidateExcn(String msg) {
		super(msg);
	}
}

