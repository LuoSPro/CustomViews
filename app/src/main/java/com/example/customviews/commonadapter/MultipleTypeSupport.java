package com.example.customviews.commonadapter;

/**
 * 多条目支持布局
 * @param <DATA>
 */
public interface MultipleTypeSupport<DATA> {

    int getLayoutId(DATA item);

}
