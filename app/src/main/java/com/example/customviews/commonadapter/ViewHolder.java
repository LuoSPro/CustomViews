package com.example.customviews.commonadapter;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder的优化
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    /**
     * 用于缓存已找到的界面
     */
    private SparseArray<View> mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = new SparseArray<>();
    }

    /**
     * itemView中获取里面的View
     * @param viewId itemView中包含的id
     * @param <T> View
     * @return
     */
    public <T extends View> T getView(int viewId){
        //多次findViewById()，对已有的findViewById做一个缓存
        View view = mView.get(viewId);
        //使用缓存减少findViewById的次数
        if (view == null){
            view =itemView.findViewById(viewId);
            mView.put(viewId,view);
        }
        //这样我们拿的时候就不需要再强制类型转换了，现在的findViewById()就是通过这种方法使我们不在对找到的View进行强转的
        /**
         * protected <T extends View> T findViewTraversal(@IdRes int id) {
         *     if (id == mID) {
         *         return (T) this;
         *     }
         *     return null;
         * }
         */
        return (T) view;
    }

    //通用功能进行封装：设置文本、设置条目点击事件、设置图片
    public ViewHolder setText(int viewId,CharSequence text){
        TextView tv = getView(viewId);
        tv.setText(text);
        //链式调用
        return this;
    }

    /**
     * 设置图片资源
     * @param viewId
     * @param resourceId
     * @return
     */
    public ViewHolder setImageResource(int viewId,int resourceId){
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    // 图片处理问题  路径问题  用到第三方的  ImageLoader  Glide Picasso(毕加索)
    //不能直接写一个第三方的图片加载，写的东西不光给自己用，还可能给其他人用
    //最终采用的方式是采用自己的一套规范 HolderImageHolder
    public ViewHolder setImagePath(int viewId,HolderImageLoader imageLoader){
//        ImageView imageView = getView(viewId);
//        Glide.with(context).load(path).into(imageView);
        ImageView imageView = getView(viewId);
        imageLoader.loadImage(imageView,imageLoader.getPath());
        return this;
    }

    /**
     * 图片的加载
     * 这样也是一种解耦的方式
     */
    public abstract static class HolderImageLoader{
        private String mPath;

        public HolderImageLoader(String path){
            this.mPath = path;
        }

        /**
         * 需要去覆写这个方法去加载图片
         * @param imageView
         * @param path
         */
        public abstract void loadImage(ImageView imageView,String path);


        public String getPath(){
            return mPath;
        }
    }
}