package com.yyc.paycore;

/**
 * ==========================
 *
 * @author yuanyanchao <a href="mailto:lenglong110@qq.com">Contact me.</a>
 * @date 2018-08-02
 * ==========================
 */
public interface PayCallback {
    void onPayBegin();

    void onPayCallback(PayResult result, String msg);
}
