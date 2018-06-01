package com.hao.base.adapter;

import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hao.base.utils.UIUtil;


/**
 * @Package com.hao.common.adapter
 * @作 用:
 * @创 建 人: linguoding 邮箱：linggoudingg@gmail.com
 * @日 期: 2017年08月16日  20:41
 */


public class BaseGridDivider extends RecyclerView.ItemDecoration {
    private int mSpace;

    private BaseGridDivider(int space) {
        mSpace = space;
    }

    /**
     * 设置间距资源 id
     *
     * @param resId
     * @return
     */
    public static BaseGridDivider newInstanceWithSpaceRes(@DimenRes int resId) {
        return new BaseGridDivider(UIUtil.getDimensionPixelOffset(resId));
    }

    /**
     * 设置间距
     *
     * @param spaceDp 单位为 dp
     * @return
     */
    public BaseGridDivider newInstanceWithSpaceDp(int spaceDp) {
        return new BaseGridDivider(UIUtil.dp2px(spaceDp));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.top = mSpace;
        outRect.bottom = mSpace;
    }
}
