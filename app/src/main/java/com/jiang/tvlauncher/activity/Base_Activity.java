package com.jiang.tvlauncher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.jiang.tvlauncher.utils.AnimUtils;

/**
 * @author: jiangadmin
 * @date: 2017/7/3.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 公共
 */

public class Base_Activity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void enlargeAnim(View v) {
        AnimUtils.S(v, 1, 1.2F);
//        Animation a = AnimationUtils.loadAnimation(v.getContext(), R.anim.uikit_enlarge);
//        a.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//            }
//        });
//        a.setFillAfter(true);
//        v.clearAnimation();
//        v.setAnimation(a);
//        a.start();
    }

    public void reduceAnim(View v) {

        AnimUtils.S(v, 1.2F, 1);
//        Animation a = AnimationUtils.loadAnimation(v.getContext(), R.anim.uikit_reduce);
//        a.setFillAfter(true);
//        v.clearAnimation();
//        v.startAnimation(a);
//        a.start();
    }

}
