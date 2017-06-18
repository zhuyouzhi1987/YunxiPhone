package com.yunxi.phone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.yunxi.phone.bean.SortModel;
import com.yunxi.phone.utils.SwipeMenuListView;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class SortAdapter extends BaseSwipListAdapter implements SectionIndexer{
	private List<SortModel> list = null;
	private Context mContext;
	private static final int TYPE_COUNT = 2;//item类型的总数
	private static final int TYPE_COMSUM = 1;//标题类型
	private static final int TYPE_CHARGE = 0;//内容类型
	int currentType;
	SwipeMenuListView sortListView;
	public SortAdapter(Context mContext, List<SortModel> list, SwipeMenuListView sortListView) {
		this.mContext = mContext;
		this.list = list;
		this.sortListView = sortListView;
	}
	
	/**
	 * �
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public List<SortModel>  getdateList(){
		return this.list;
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
//		ViewHolder viewHolder = null;
		final SortModel sort = list.get(position);





		View comsumView =null;
		View friendView=null ;
		currentType = getItemViewType(position);
		if(currentType== TYPE_COMSUM){
			TitleViewHolder titleViewHolder;
			//标题
			if (convertView == null) {
				titleViewHolder = new TitleViewHolder();
				comsumView = LayoutInflater.from(mContext).inflate(
						R.layout.zimu_title, null);
				titleViewHolder.tv_zimu= (TextView) comsumView.findViewById(R.id.catalog);
				comsumView.setTag(titleViewHolder);
				convertView = comsumView;

				AutoUtils.autoSize(convertView);
			} else {
				titleViewHolder = (TitleViewHolder) convertView.getTag();
			}
			titleViewHolder.tv_zimu.setText(sort.getSortLetters());
		}else if(currentType == TYPE_CHARGE){
			FriendHolder friendHolder;
			if (convertView == null) {
				friendHolder = new FriendHolder();
				friendView = LayoutInflater.from(mContext).inflate(
						R.layout.itme_contacts, null);
				friendHolder.tvTitle = (TextView) friendView.findViewById(R.id.title);
				friendHolder.tvLetter = (TextView) friendView.findViewById(R.id.catalog);
				friendHolder.more = (ImageView) friendView
						.findViewById(R.id.more);
				friendHolder.note = (TextView) friendView.findViewById(R.id.note);
				friendHolder.friend_item_layout=(AutoLinearLayout)friendView.findViewById(R.id.friend_item_layout);

				friendView.setTag(friendHolder);

				convertView = friendView;

				AutoUtils.autoSize(convertView);
			} else {
				friendHolder = (FriendHolder) convertView.getTag();
			}

			friendHolder.tvTitle.setText(sort.getName());
			friendHolder.note.setText(sort.getNote()+"");
			friendHolder.more.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sortListView.smoothOpenMenu(position);
				}
			});

		}
		return convertView;
	}
	


	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		TextView note;
		ImageView iv_editor;
	}


	/**
	 * �根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 *  提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;//总共有两个类型
	}

	@Override
	public int getItemViewType(int position) {
		if(TextUtils.isEmpty(list.get(position).getTag())){
			return TYPE_CHARGE;// 详细类型
		}
		else if(list.get(position).getTag().equals("1")){
			return TYPE_COMSUM;// 标题类型
		}
		return -1;
	}

	class TitleViewHolder {
		TextView tv_zimu;
	}

	class FriendHolder {
		TextView tvLetter;
		TextView tvTitle;
		TextView note;
		ImageView more;

		AutoLinearLayout friend_item_layout;
	}

	@Override
	public boolean getSwipEnableByPosition(int position) {
		if(getItemViewType(position)==TYPE_COMSUM){
			return false;
		}
		return true;
	}

	public void updateItem(int index,ListView mListView,boolean flag )
	{
		if (mListView == null)
		{
			return;
		}

		// 获取当前可以看到的item位置
		int visiblePosition = mListView.getFirstVisiblePosition();
		// 如添加headerview后 firstview就是hearderview
		// 所有索引+1 取第一个view
		// View view = listview.getChildAt(index - visiblePosition + 1);
		// 获取点击的view
		View view = mListView.getChildAt(index - visiblePosition);
		ImageView more = (ImageView) view.findViewById(R.id.more);
		if(more.getVisibility()==View.GONE){
			more.setVisibility(View.VISIBLE);
		}else{
			more.setVisibility(View.GONE);
		}
	}
}