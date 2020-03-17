package com.yyc.paycore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * ==========================
 *
 * @author yuanyanchao <a href="mailto:lenglong110@qq.com">Contact me.</a>
 * @date 2018-08-02
 * ==========================
 */
public class PayCore {

    private static PayCore mInstance;
    private static class PayCoreHolder{
        public static PayCore getInstance(){
            return new PayCore();
        }
    }
    public static PayCore getInstance(){
        return PayCoreHolder.getInstance();
    }
    private WeakReference<Activity> context;
    private WeakReference<PayCallback> payCallback;
    private IWXAPI msgApi;
    public static String WEIXIN_APPID = "wx9cd2a892bfd3bb1f";
    public static final String EXTRA_WX_PAY_ERROR_CODE = "extra_wx_pay_error_code";
    public static final int PAY_TYPE_ALISECUREPAY = 11;
    public static final int PAY_TYPE_ALIWAPPAY = 21;
    public static final int PAY_TYPE_TENPAY = 31;
    private static final int SDK_PAY_FLAG = 1000;

    private PayCore(){

    }

//    private BroadcastReceiver wxReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if("com.anban.paylibrary.WX".equals(action)) {
//                int errorCode = intent.getIntExtra(EXTRA_WX_PAY_ERROR_CODE, 99);
//                switch(errorCode) {
//                    case -2:
//                        PayCore.this.payFail((String)null);
//                        break;
//                    case 0:
//                        PayCore.this.paySuccess();
//                        break;
//                    default:
//                        PayCore.this.payFail("在线支付失败！请重新选择支付方");
//                }
//            }
//
//        }
//    };
//
//    public PayCore(Activity activity){
//        this.context = new WeakReference(activity);
//        if(WEIXIN_APPID != null) {
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("com.anban.paylibrary.WX");
//            activity.registerReceiver(this.wxReceiver, filter);
//        }
//    }

    /**
     * 微信支付
     * @param activity
     * @param paySignStr
     * @param callback
     */
    public void wxPay(Activity activity,String paySignStr,PayCallback callback) {
        this.context = new WeakReference(activity);
        this.payCallback = new WeakReference(callback);
        if (this.context.get() != null) {
            try {
                JSONObject json = new JSONObject(paySignStr);
                if (null != json) {
                    PayReq req = new PayReq();
                    String appIdOnLine = json.getString("appId");
                    if(!TextUtils.isEmpty(appIdOnLine)){
                        WEIXIN_APPID = appIdOnLine;
                    }
                    this.msgApi = WXAPIFactory.createWXAPI(this.context.get(), WEIXIN_APPID);
                    this.msgApi.registerApp(WEIXIN_APPID);
                    req.appId = json.getString("appId");
                    req.partnerId = json.getString("partnerId");
                    req.prepayId = json.getString("prepayId");
                    req.nonceStr = json.getString("nonceStr");
                    req.timeStamp = json.getString("timeStamp");
                    req.packageValue = "Sign=WXPay";
                    req.sign = json.getString("sign");

                    this.msgApi.registerApp(WEIXIN_APPID);
                    this.msgApi.sendReq(req);
                } else {
                    this.payFail("在线支付失败！请重新选择支付方");
                }
            } catch (Exception var4) {
                this.payFail("在线支付失败！请重新选择支付方");
            }

        }
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     * 阿里支付
     * @param activity
     * @param paySignStr
     * @param callback
     */
    public void alipay(Activity activity,String paySignStr,PayCallback callback) {
        this.context = new WeakReference(activity);
        this.payCallback = new WeakReference(callback);
        if (this.context.get() == null) {
            return;
        }
        String tempPayInfo = "";
        try {
            JSONObject json = new JSONObject(paySignStr);
            if (null != json) {
                tempPayInfo = json.getString("payInfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(tempPayInfo)) {
            return;
        }
        final String payInfo = tempPayInfo;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) PayCore.this.context.get());
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);

            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    AliPayResp payResult = new AliPayResp((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    try {
                        if (TextUtils.equals(resultStatus, "9000")) {
                            PayCore.this.paySuccess();
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            PayCore.this.payFail((String) null);
                        } else {
                            PayCore.this.payFail("支付失败。交易状态码：" + resultStatus + " 交易结果：" + payResult);
                        }
                    } catch (Exception var2) {
                        PayCore.this.payFail((String) null);
                    }
                default:
                    break;
            }

            return false;
        }
    });

    private void payFail(String message) {
        if(this.payCallback != null && this.payCallback.get() != null) {
            ((PayCallback)this.payCallback.get()).onPayCallback(PayResult.FAILED, message);
        }

    }

    private void paySuccess() {
        if(this.payCallback != null && this.payCallback.get() != null) {
            ((PayCallback)this.payCallback.get()).onPayCallback(PayResult.SUCCESS, (String)null);
        }

    }

    public void destroy() {
//        if(this.context.get() != null) {
//            ((Activity)this.context.get()).unregisterReceiver(this.wxReceiver);
//        }
    }

    public void setBaseRespCode(int errorCode){
        switch(errorCode) {
            case -2:
                PayCore.this.payFail((String)null);
                break;
            case 0:
                PayCore.this.paySuccess();
                break;
            default:
                PayCore.this.payFail("在线支付失败！请重新选择支付方");
        }
    }

}
