package com.example.footballfashion.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.footballfashion.ControllerApplication;
import com.example.footballfashion.R;
import com.example.footballfashion.activity.MainActivity;
import com.example.footballfashion.constant.GlobalFunction;
import com.example.footballfashion.databinding.FragmentFeedbackBinding;
import com.example.footballfashion.model.Feedback;
import com.example.footballfashion.util.StringUtil;

public class FeedbackFragment extends BaseFragment {

    private FragmentFeedbackBinding mFragmentFeedbackBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentFeedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false);

        mFragmentFeedbackBinding.tvSendFeedback.setOnClickListener(v -> onClickSendFeedback());

        return mFragmentFeedbackBinding.getRoot();
    }

    private void onClickSendFeedback() {
        if (getActivity() == null) {
            return;
        }
        MainActivity activity = (MainActivity) getActivity();

        String strName = mFragmentFeedbackBinding.edtName.getText().toString();
        String strPhone = mFragmentFeedbackBinding.edtPhone.getText().toString();
        String strEmail = mFragmentFeedbackBinding.edtEmail.getText().toString();
        String strComment = mFragmentFeedbackBinding.edtComment.getText().toString();

        if (StringUtil.isEmpty(strName)) {
            GlobalFunction.showToastMessage(activity, getString(R.string.name_require));
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFunction.showToastMessage(activity, getString(R.string.comment_require));
        } else {
            activity.showProgressDialog(true);
            Feedback feedback = new Feedback(strName, strPhone, strEmail, strComment);
            ControllerApplication.get(getActivity()).getFeedbackDatabaseReference()
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(feedback, (databaseError, databaseReference) -> {
                        activity.showProgressDialog(false);
                        sendFeedbackSuccess();
                    });
        }
    }

    public void sendFeedbackSuccess() {
        GlobalFunction.hideSoftKeyboard(getActivity());
        GlobalFunction.showToastMessage(getActivity(), getString(R.string.send_feedback_success));
        mFragmentFeedbackBinding.edtName.setText("");
        mFragmentFeedbackBinding.edtPhone.setText("");
        mFragmentFeedbackBinding.edtEmail.setText("");
        mFragmentFeedbackBinding.edtComment.setText("");
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, getString(R.string.feedback));
        }
    }
}