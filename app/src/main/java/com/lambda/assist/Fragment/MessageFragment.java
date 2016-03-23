package com.lambda.assist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Adapter.MessageRVAdapter;
import com.lambda.assist.Asyn.AddMessage;
import com.lambda.assist.Asyn.LoadingMessage;
import com.lambda.assist.Model.MessageItem;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Listener.OnRcvScrollListener;
import com.lambda.assist.Other.IsVaild;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;

import java.util.List;

/**
 * Created by asus on 2016/2/27.
 */
public class MessageFragment extends MissionBaseFragment {

    private Context ctxt;
    //
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecycleview;
    private EditText et_message;
    private Button bt_send;
    //
    private MessageRVAdapter msg_adapter;
    //
    private List<MessageItem> list_messages;//
    private Mission mMission;

    public static MessageFragment newInstance(String title, int indicatorColor, int dividerColor) {
        MessageFragment fragment = new MessageFragment();
        fragment.setTitle(title);
        fragment.setIndicatorColor(indicatorColor);
        fragment.setDividerColor(dividerColor);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.ctxt = activity;
        mMission = ((Act_Mission) activity).getMissionData();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitialSomething();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitialUI(getView());
        InitialAction();
    }

    private void LoadMessage(String missionid) {
        if (Net.isNetWork(ctxt)) {
            LoadingMessage task = new LoadingMessage(new LoadingMessage.OnLoadingMessageListener() {
                public void finish(Integer result, List<MessageItem> list) {
                    switch (result) {
                        case TaskCode.Success:
                            list_messages.clear();
                            list_messages.addAll(list);
                            refreshMessages();
                            break;
                        case TaskCode.Empty:
                            list_messages.clear();
                            refreshMessages();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            });
            task.execute(missionid);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void AddMessage(String message) {
        if (Net.isNetWork(ctxt)) {
            AddMessage task = new AddMessage(new AddMessage.OnAddMessageListener() {
                public void finish(Integer result) {
                    switch (result) {
                        case TaskCode.Success:
                            et_message.setText("");
                            LoadMessage(mMission.getMissionid() + "");
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
            task.execute(mMission.getMissionid() + "", message);
        } else {

        }
    }

    private void refreshMessages() {
        if (msg_adapter != null) {
            msg_adapter.notifyDataSetChanged();
            mRecycleview.scrollToPosition(list_messages.size() - 1);
        }
    }

    private void InitialUI(View v) {
        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.srfl_message);
        mRecycleview = (RecyclerView) v.findViewById(R.id.rv_message);
        et_message = (EditText) v.findViewById(R.id.et_message_message);
        bt_send = (Button) v.findViewById(R.id.bt_message_send);
    }

    private void InitialAction() {
        // SwipeRefreshLayout Setting
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //Loading
                LoadMessage(mMission.getMissionid() + "");
                mSwipeLayout.setRefreshing(false);
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.black);
        //  RecyclerView Setting
        mRecycleview.setOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        // 2. set layoutManger
        GridLayoutManager manager = new GridLayoutManager(ctxt, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                return 2;
            }
        });
        mRecycleview.setLayoutManager(manager);
        // item between item
//        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(10);
//        mRecycleview.addItemDecoration(itemDecoration);
        // 3. create an adapter
        msg_adapter = new MessageRVAdapter(ctxt, list_messages);
        // 4. set adapter
        mRecycleview.setAdapter(msg_adapter);
        mRecycleview.scrollToPosition(list_messages.size() - 1);
        // 5. set item animator to DefaultAnimator
        mRecycleview.setItemAnimator(new DefaultItemAnimator());
        // button setting
        bt_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String message = et_message.getText().toString();
                if (IsVaild.isVaild_Message(message)) {
                    AddMessage(message);
                }
            }
        });
    }

    private void InitialSomething() {
        list_messages = mMission.getMessages();
    }

}
