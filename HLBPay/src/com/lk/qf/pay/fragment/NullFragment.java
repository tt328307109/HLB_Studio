package com.lk.qf.pay.fragment;

import com.lk.bhb.pay.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NullFragment extends BaseFragment {
		View layoutView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.fragment_null, container, false);
		
		return layoutView;
	}
}
