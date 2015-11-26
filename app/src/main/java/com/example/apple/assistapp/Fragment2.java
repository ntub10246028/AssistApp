

package com.example.apple.assistapp;


import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Fragment2 extends Fragment {

    private Context ctxt;
    private ListView lv_bbb_datas;
    private ImageButton imgbt_add;
    private int position;

    public static Fragment2 newInstance(int pos) {
        Fragment2 fragment = new Fragment2();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        fragment.setArguments(b);
        return fragment;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt("pos") : 2;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctxt = getActivity();
        View v = inflater.inflate(R.layout.fragment_2, container, false);
        lv_bbb_datas = (ListView) v.findViewById(R.id.lv_bbb_datas);
        imgbt_add = (ImageButton) v.findViewById(R.id.imgbt_add);
        String[] datas = { "BBB1", "BBB2", "BBB3", "BBB4", "BBB5", "BBB6",
                "BBB7", "BBB8", "BBB9", "BBB10" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctxt,
                android.R.layout.simple_list_item_1, datas);
        lv_bbb_datas.setAdapter(adapter);
        // lv_bbb_datas.setOnScrollListener(scrolllistener);
        lv_bbb_datas.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                String text = ((TextView)v).getText().toString();
                Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
            }});
        imgbt_add.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ctxt, "add", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

//	OnScrollListener scrolllistener = new OnScrollListener() {
//		int mLastFirstVisibleItem;
//		boolean mIsScrollingUp = false;
//		boolean isScroll = false;
//
//		public void onScroll(AbsListView v, int first, int arg2, int arg3) {
//			if (!isScroll) {
//				isScroll = true;
//				ListView lv = lv_bbb_datas;
//
//				if (v.getId() == lv_bbb_datas.getId()) {
//
//					if (first > mLastFirstVisibleItem) {
//						mIsScrollingUp = false;
//					} else if (first < mLastFirstVisibleItem) {
//						mIsScrollingUp = true;
//					}
//					mLastFirstVisibleItem = first;
//					if (mIsScrollingUp) {
//						Animation am = AnimationUtils.loadAnimation(
//								getActivity(), R.layout.animation_appear);
//						imgbt_add.startAnimation(am);
//						imgbt_add.setVisibility(View.INVISIBLE);
//					} else {
//						Animation am = AnimationUtils.loadAnimation(
//								getActivity(), R.layout.animation_disappear);
//						imgbt_add.startAnimation(am);
//						imgbt_add.setVisibility(View.VISIBLE);
//					}
//				}
//			}
//		}
//
//		public void onScrollStateChanged(AbsListView v, int scrollState) {
//
//			if (scrollState == 0) { // stop
//				isScroll = false;
//			}
//
//		}
//
//	};

}
