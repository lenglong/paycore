package com.yyc.test;

import android.app.Application;

import com.yyc.pay_annotation.WXPayEntry;
import com.yyc.paycore.BaseWXEntryActivity;

/**
 * ==========================
 *
 * @author yuanyanchao <a href="mailto:lenglong110@qq.com">Contact me.</a>
 * @date 2019-06-17
 * ==========================
 */
@WXPayEntry(packageName = "com.yyc.test")
public class MyApplication extends Application {

}
