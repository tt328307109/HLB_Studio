package com.lk.qf.pay.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.bhb.pay.R.array;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.wedget.MyClickListener;
import com.lk.qf.pay.wedget.MyLongClickListener;
import com.lk.qf.pay.wedget.XYKDJClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

public class CreditCardAdapter extends MyBaseAdapter<Xinyongkainfo> {

	private XYKDJClickListener mListener;
	MyLongClickListener mLongClickLitener;

	public CreditCardAdapter(Context context, List<Xinyongkainfo> list,
			XYKDJClickListener mListener, MyLongClickListener mLongClickLitener) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.mListener = mListener;
		this.mLongClickLitener = mLongClickLitener;
	}

	public void sendSata(List<Xinyongkainfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater
					.inflate(R.layout.credit_card_list_item_layout, null);
			holder.rl_CreditCard = (RelativeLayout) view
					.findViewById(R.id.rl_credit_card);

			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_creditCard_bankIcon1);
			holder.tvUserName = (TextView) view
					.findViewById(R.id.tv_creditCard_userName);
			holder.tvCardNum = (TextView) view
					.findViewById(R.id.tv_creditCard_bankCardNum);
			holder.tvBankName = (TextView) view
					.findViewById(R.id.tv_creditCard_bankName);
			holder.rlToSeeInfo = (TextView) view
					.findViewById(R.id.rl_creditCard_toInfo);
			holder.tvType = (TextView) view
					.findViewById(R.id.tv_creditCard_bankCardType);
			holder.tvPoundage = (TextView) view
					.findViewById(R.id.tv_creditCard_bankCardPoundage);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Xinyongkainfo info = list.get(position);
		holder.tvUserName.setText(info.getUserName());
		// 信用卡状态：0已经赎回,1信用卡审核中，2信用卡审核成功，3信用卡审核失败4申请信用卡还款，5公司已经收到信用卡（准备还款）6账单日生成了账单（代还款中）7信用卡持卡人还款成功8可代还款
		String type = info.getType();
		String billTime = info.getBilltime();
		if (!type.equals("") && type != null) {
			int iType = Integer.parseInt(type);
			switch (iType) {
			case 0:
				type = "赎回中";
				break;
			case 1:
				type = "审核中";
				break;
			case 2:
				type = "初审通过";
				break;
			case 3:
				type = "审核失败";
				break;
			case 4:
				type = "贷款审批中";
				break;
			case 5:
				type = "正常";
				String billMoney = info.getBillmoney();
				if (!billMoney.equals("")) {
					holder.tvPoundage.setText("账单金额:" + billMoney);
				}
				break;
			case 6:
				type = "贷款成功";
				String reimMoney = info.getReimmoney();
				if (!reimMoney.equals("")) {
					holder.tvPoundage.setText("已贷金额:" + reimMoney);
				}
				break;
			case 7:
				type = "贷款完成";
				break;
			case 8:
				String today = TimeUtils.getFormatDate("dd");// 今天
				String tomorrow = TimeUtils.getTomorrowOrOther("dd", 1);// 明日
				String afterTomorrow = TimeUtils.getTomorrowOrOther("dd", 2);// 后日
				if (today.equals(billTime)) {
					type = "今日账单";
				} else if (tomorrow.equals(billTime)) {
					type = "明日账单";
				} else if (afterTomorrow.equals(billTime)) {
					type = "后日账单";
				} else {
					type = "请申请贷款";
				}
				String zdMoney = info.getBillmoney();
				if (!zdMoney.equals("")) {
					holder.tvPoundage.setText("账单金额:" + zdMoney);
				}
				break;
			case 9:
				type = "此卡无效";
				break;
			default:
				break;
			}

			holder.tvType.setText(type);
		} else {
			holder.tvType.setVisibility(View.GONE);
		}
		holder.tvCardNum.setText(info.getCardNum().substring(
				info.getCardNum().length() - 4, info.getCardNum().length()));

		String bankName = info.getBankName();
		if (bankName.length() > 8) {
			bankName = bankName.substring(0, 6) + "***";
		}
		holder.tvBankName.setText(bankName);

		if (info.getImgUrl() == null || info.getImgUrl().equals("")) {
			Picasso.with(context).load(MyUrls.ROOT_URL1 + "/bank/yl.png").fit()
					.into(holder.imgLogo);
		} else {
			Picasso.with(context).load(MyUrls.ROOT_URL1 + info.getImgUrl())
					.fit().into(holder.imgLogo);
		}
//		holder.rlToSeeInfo.setTag(position);
//		holder.rlToSeeInfo.setOnClickListener(mListener);
		holder.rl_CreditCard.setOnClickListener(mListener);
		holder.rl_CreditCard.setTag(R.id.tag_first, position);
		holder.rl_CreditCard.setOnLongClickListener(mLongClickLitener);//
		return view;
	}

	class ViewHolder {
		RelativeLayout rl_CreditCard;
		ImageView imgLogo;// 银行logo
		TextView tvUserName;// 用户名
		TextView tvCardNum;// 卡号
		TextView tvBankName;// 银行名
		TextView rlToSeeInfo;// 查看详情
		TextView tvType;// 状态
		TextView tvPoundage;// 手续费
	}

}
