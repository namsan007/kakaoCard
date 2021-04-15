package com.kakaopay;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.dto.CancelReqDto;
import com.kakaopay.dto.CancelResDto;
import com.kakaopay.dto.PayReqDto;
import com.kakaopay.dto.PayResDto;
import com.kakaopay.repository.CancelInfoRep;
import com.kakaopay.repository.PayInfoRep;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PayTest {

	
	String testData = "";
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	Date dt = new Date();

	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	// 기본 payReq셋팅
	public PayReqDto getTestPayReqDto1(String temp1,String temp2,String temp3,int temp4,int temp5,int temp6) {
		PayReqDto payReqDto = new PayReqDto();

		payReqDto.setCardNo(temp1);
		payReqDto.setExtMmYy(temp2);
		payReqDto.setCvcNo(temp3);
		payReqDto.setInsMth(temp4);
		payReqDto.setPayAmt(temp5);
		payReqDto.setVatAmt(temp6);
		return payReqDto;
	}
	
	// 기본 cancelReq셋팅
	public CancelReqDto getTestCancelReqDto1(String temp1,int temp4,int temp5) { // 취소할 관리번호
		CancelReqDto cancelReqDto = new CancelReqDto();

		cancelReqDto.setCancelMgnNo(temp1);
		cancelReqDto.setPayAmt(temp4);
		cancelReqDto.setVatAmt(temp5);
		return cancelReqDto;
	}
	
	@Test
	public void 과제테스트_리스트1() throws Exception{
		
//		testcase1						
//		결제1		11,000	1,000	성공	11,000	1,000	11,000(1,000)원 결제 성공
//		부분취소1	1,100	100	성공	9,900	900	1,100(100)원 취소 성공
//		부분취소2	3,300	0	성공	6,600	600	3,300원 취소 성공
//		부분취소3	7,000	0	실패	6,600	600	7,000원 취소하려 했으나 남은 결제금액 보다 커서 실패 
//		부분취소4	6,600	700	실패	6,600	600	6,600(700)원 취소하려 했으나 남은 부 가가치세보다 취소요청 부가가치세가 커 서 실패
//		부분취소5	6,600	600	성공	0	0	6,600(600)원 성공 부분취소
//		부분취소6	100	0	실패	0	0	100원 취소하려했으나 남은
		String testCaseCardNo1 = "1234567890123456";
		
		
		PayReqDto payTestCase1 = getTestPayReqDto1(testCaseCardNo1,"0420","666",0,11000,1000);
		PayResDto res1 = paymentReqAction(payTestCase1,"결제 case1");
		
		CancelReqDto payTestCase1_1 = getTestCancelReqDto1(res1.getMgnNo(),1100,100);
		CancelReqDto payTestCase1_2 = getTestCancelReqDto1(res1.getMgnNo(),3300,0);
//		CancelReqDto payTestCase1_3 = getTestCancelReqDto1(res1.getMgnNo(),7000,0);
//		CancelReqDto payTestCase1_4 = getTestCancelReqDto1(res1.getMgnNo(),6600,700);
		CancelReqDto payTestCase1_5 = getTestCancelReqDto1(res1.getMgnNo(),6600,600);
		CancelReqDto payTestCase1_6 = getTestCancelReqDto1(res1.getMgnNo(),100,0);
		
		CancelResDto canRes1 = cancelReqAction(payTestCase1_1,"취소 case1-1");	//성공 -> 일치
		CancelResDto canRes2 = cancelReqAction(payTestCase1_2,"취소 case1-2");	//성공 -> 일치
//		CancelResDto canRes3 = cancelReqAction(payTestCase1_3,"취소 case1-3");	//실패 -> 일치
//		CancelResDto canRes4 = cancelReqAction(payTestCase1_4,"취소 case1-4");	//실패 -> 일치
		CancelResDto canRes5 = cancelReqAction(payTestCase1_5,"취소 case1-5");	//성공 -> 일치
		CancelResDto canRes6 = cancelReqAction(payTestCase1_6,"취소 case1-6");	//실패 -> 일치

	}
	@Test
	public void 과제테스트_리스트2() throws Exception{
		
//		testcase2						
//		결제1	20,000	909	성공	20,000	909	20,000(909)원 결제성공
//		부분취소1	10,000	0	성공	10,000	909	10,000(0)원 취소성공
//		부분취소2	10,000	0	실패	10,000	909	10,000(0)원 취소하려했으나 남은부가 가치세 금액(909)이더크므로실패
//		부분취소3	10,000	909	성공	0	0	10,000(909)원 취소성공

		String testCaseCardNo2 = "2234567890123456";
		
		
		PayReqDto payTestCase2 = getTestPayReqDto1(testCaseCardNo2,"0420","666",0,20000,909);
		PayResDto res2 = paymentReqAction(payTestCase2,"결제 case2");
		CancelReqDto payTestCase2_1 = getTestCancelReqDto1(res2.getMgnNo(),10000,0);
		CancelReqDto payTestCase2_2 = getTestCancelReqDto1(res2.getMgnNo(),10000,0);
		CancelReqDto payTestCase2_3 = getTestCancelReqDto1(res2.getMgnNo(),10000,909);
		
//		CancelResDto canRes2_1 = cancelReqAction(payTestCase2_1,"취소 case2-1");	//성공 -> 불일치 10,000(909)원 취소 성공  관리번호의 취소후 남은 총 결제금액 = (총 결제금액 + 총 부가세금액) 10000 = (10000 + 0)원
//		CancelResDto canRes2_2 = cancelReqAction(payTestCase2_2,"취소 case2-2");	//실패 -> 불일치 10,000(909)원 취소 성공  관리번호의 취소후 남은 총 결제금액 = (총 결제금액 + 총 부가세금액) 10000 = (10000 + 0)원
		CancelResDto canRes2_3 = cancelReqAction(payTestCase2_3,"취소 case2-3");	//성공 -> 불일치 10,000(909)원 취소 성공  관리번호의 취소후 남은 총 결제금액 = (총 결제금액 + 총 부가세금액) 10000 = (10000 + 0)원
		

	}
	
	@Test
	public void 과제테스트_리스트3() throws Exception{
		
//		testcase3						
//		결제1	20,000	null	성공	20,000	1,818	20,000원 결제 성공, 부가가치세 (1,818) 자동계산
//		부분취소1	10,000	1,000	성공	10,000	818	10,000(1,000)원 취소 성공
//		부분취소2	10,000	909	실패	10,000	818	10,000(909)원 취소하려했으나남은 부가가치세가 더 작으므로 실패
//		부분취소3	10,000	null	성공	0	0	10,000원 취소,남은 부가가치세는 818원으로 자동계산되어 성공
		
		String testCaseCardNo3 = "3234567890123456";
		
		
		PayReqDto payTestCase3 = getTestPayReqDto1(testCaseCardNo3,"0420","666",0,20000,0);
		PayResDto res3 = paymentReqAction(payTestCase3,"결제 case3");
		
		CancelReqDto payTestCase3_1 = getTestCancelReqDto1(res3.getMgnNo(),10000,1000);
		CancelReqDto payTestCase3_2 = getTestCancelReqDto1(res3.getMgnNo(),10000,909);
		CancelReqDto payTestCase3_3 = getTestCancelReqDto1(res3.getMgnNo(),10000,0);
		

		CancelResDto canRes3_1 = cancelReqAction(payTestCase3_1,"취소 case3-1");		//성공 -> 일치
		//CancelResDto canRes3_2 = cancelReqAction(payTestCase3_2,"취소 case3-2");	//성공 -> 일치
		CancelResDto canRes3_3 = cancelReqAction(payTestCase3_3,"취소 case3-3");		//성공 -> 불일치... 부가가치세가 null인경우 자동 계산됨

		
	}
	//결제 요청
	public PayResDto paymentReqAction(PayReqDto payReqDto ,String caseStr) throws Exception {
		RequestBuilder payBuiler = payBuilder(payReqDto);
		MockHttpServletResponse payResponse = mockMvc.perform(payBuiler).andReturn().getResponse();
		PayResDto pay = convertJsonBytesToObject(payResponse.getContentAsString(), PayResDto.class);

		log.info("로그확인 "+caseStr+": ->>>>> : "+ pay.getMsg());
		
		return pay;
	}
	//취소 요청
	public CancelResDto cancelReqAction(CancelReqDto cancelReqDto,String caseStr) throws Exception {

		RequestBuilder cancelBuiler = cancelBuilder(cancelReqDto);
		MockHttpServletResponse cancelResponse = mockMvc.perform(cancelBuiler).andReturn().getResponse();
		CancelResDto cancel = convertJsonBytesToObject(cancelResponse.getContentAsString(), CancelResDto.class);
		
		log.info("로그확인 "+caseStr+": ->>>>> : "+ cancel.getMsg());
		
		return cancel;
	}
	
	
	
	/**
	 * payment request builder
	 * 
	 * @param paymentRequest
	 * @return
	 */
	public static RequestBuilder payBuilder(PayReqDto payReqDto) {
		return MockMvcRequestBuilders.post("/pay").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(convertObjectToJsonBytes(payReqDto));

	}

	/**
	 * cancel request builder
	 * 
	 * @param cancelRequest
	 * @return
	 */
	public static RequestBuilder cancelBuilder(CancelReqDto cancelReqDto) {
		return MockMvcRequestBuilders.post("/cancel").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(convertObjectToJsonBytes(cancelReqDto));
	}

	public static byte[] convertObjectToJsonBytes(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			return mapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T convertJsonBytesToObject(String json, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
}
