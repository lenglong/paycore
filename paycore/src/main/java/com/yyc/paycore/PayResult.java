package com.yyc.paycore;

/**
 * ==========================
 *
 * @author yuanyanchao <a href="mailto:lenglong110@qq.com">Contact me.</a>
 * @date 2020-03-16
 * ==========================
 */
public enum PayResult {
    /**
     * 成功
     */
    SUCCESS(0),
    /**
     * 失败
     */
    FAILED(-1);

    int resulteCode;

    PayResult(int resulteCode) {
        this.resulteCode = resulteCode;
    }
}
