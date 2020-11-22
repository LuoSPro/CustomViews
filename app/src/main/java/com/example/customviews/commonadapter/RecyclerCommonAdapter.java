package com.example.customviews.commonadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * RecyclerView的通用适配器
 */
public abstract class RecyclerCommonAdapter<DATA> extends RecyclerView.Adapter<ViewHolder> {

    /**
     * 条目id不一样，只能通过参数传递
     */
    private int mLayoutId;

    /**
     * 参数通用，那么就只能用泛型来接收
     */
    protected List<DATA> mData;

    /**
     * 实例化View的LayoutInflate
     */
    private LayoutInflater mInflater;

    /**
     * 条目点击监听事件
     */
    private ItemClickListener mItemClickListener;

    /**
     * 条目长按事件监听
     */
    private ItemLongClickListener mItemLongClickListener;

    /**
     * 多类型条目支持
     */
    private MultipleTypeSupport mTypeSupport;

    protected Context mContext;

    public RecyclerCommonAdapter(Context context,List<DATA> data, int layoutId){
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    public RecyclerCommonAdapter(Context context, List<DATA> data, MultipleTypeSupport typeSupport){
        this(context,data,-1);
        this.mTypeSupport = typeSupport;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mTypeSupport != null){
            //需要多布局
            mLayoutId = viewType;
        }

        //创建View，需要context
        View itemView = mInflater.inflate(mLayoutId, parent, false);
        //实例化View的方式很多种(三种),但是最终都是调用的第三种
        //View.inflate(mContext,mLayoutId,null);
        //LayoutInflater.from(mContext).inflate(mLayoutId,parent);
        //LayoutInflater.from(mContext).inflate(mLayoutId,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //ViewHolder优化
        convert(holder,mData.get(position),position);

        //条目点击事件
        if (mItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(position);
                }
            });
        }

        if (mItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onItemLongClick(position);
                }
            });
        }
    }

    /**
     * 设置监听
     * @param itemClickListener
     */
    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    /**
     * 把必要方法抛出去，供子类扩展
     * @param holder ViewHolder
     * @param item 当前位置的条目
     * @param position 当前的位置
     */
    protected abstract void convert(ViewHolder holder, DATA item,int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        //多布局问题
        if (mTypeSupport != null){
            //先回调拿到布局
            return mTypeSupport.getLayoutId(mData.get(position));
        }
        return super.getItemViewType(position);
    }
}
