package com.example.demo00.view;

import com.example.demo00.utils.Log;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.app.Context;

public class MyFlowLayout extends ComponentContainer {
    public static final String TAG = MyFlowLayout.class.getSimpleName();

    public MyFlowLayout(Context context) {
        this(context, null);
    }

    public MyFlowLayout(Context context, AttrSet attrSet) {
        this(context, attrSet, null);
    }

    public MyFlowLayout(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        init();
    }

    private void init() {
        setArrangeListener(new ArrangeListener() {
            @Override
            public boolean onArrange(int left, int top, int width, int height) {
                Log.debug(TAG,
                        "onArrange left=%{public}d, top=%{public}d, width=%{public}d, height=%{public}d",
                        left, top, width, height);
                int useHeight = 0;

                for (int i = 0; i < getChildCount(); i++) {
                    Component child = getComponentAt(i);
                    Log.debug(TAG,
                            "onArrange child index=%{public}d, left=%{public}d, top=%{public}d, width=%{public}d, height=%{public}d",
                            i, 0, useHeight, child.getEstimatedWidth(), child.getEstimatedHeight());

                    child.arrange(0, useHeight, child.getEstimatedWidth(), getEstimatedHeight());
                    useHeight += child.getEstimatedHeight();
                }
                return true;
            }
        });
    }
}
