package com.yyc.paycore;import android.text.TextUtils;/** * ========================== * * @author yuanyanchao <a href="mailto:lenglong110@qq.com">Contact me.</a> * @date 2018-08-02 * ========================== */public class AliPayResp {	private String resultStatus;	private String result;	private String memo;	public AliPayResp(String rawResult) {		if (TextUtils.isEmpty(rawResult)){			return;		}		String[] resultParams = rawResult.split(";");		for (String resultParam : resultParams) {			if (resultParam.startsWith("resultStatus")) {				resultStatus = getValue(resultParam, "resultStatus");			}			if (resultParam.startsWith("result")) {				result = getValue(resultParam, "result");			}			if (resultParam.startsWith("memo")) {				memo = getValue(resultParam, "memo");			}		}	}	@Override	public String toString() {		return "resultStatus={" + resultStatus + "};memo={" + memo				+ "};result={" + result + "}";	}	private String getValue(String content, String key) {		String prefix = key + "={";		return content.substring(content.indexOf(prefix) + prefix.length(),				content.lastIndexOf("}"));	}	/**	 * @return the resultStatus	 */	public String getResultStatus() {		return resultStatus;	}	/**	 * @return the memo	 */	public String getMemo() {		return memo;	}	/**	 * @return the result	 */	public String getResult() {		return result;	}}