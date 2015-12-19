package com.sugarya.example.alphabetdemo.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sugarya.example.alphabetdemo.R;
import com.sugarya.example.alphabetdemo.event.ItemIndexEvent;
import com.sugarya.example.alphabetdemo.utils.LogUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Sugarya on 2015/12/5.
 */
public class AlphabetLinearLayout extends LinearLayout implements View.OnTouchListener {

    private static final String[] mLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final String TAG = AlphabetLinearLayout.class.getSimpleName();
    private static final int mLetterScreenVerticalSpan = 16;
    private static final int mLetterHorizontalSpan = 10;

    //widgets
    //private Context mContext;
    private PopupWindow mPopupWindow;
    private TextView mTxtInPopup;

    //Arguments
    private ItemIndexEvent mItemIndexEvent;
    private List<String> mLetterList;
    private List<TextView> mTxtViewList;

    private int mScreenWidth;
    private float mLastY;
    private float mVerticalSpanTotal;
    private float mCurrentY;
    private String mTouchLetter;
    private int mTouchLetterIndex;
    private float mLetterVerticalSpan;
    private int mLastReactionPosition;

    public AlphabetLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public AlphabetLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlphabetLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {

        //EventBus.getDefault().register(this);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mItemIndexEvent = new ItemIndexEvent();
        //LogUtils.e(TAG, "mScreenWidth=" + mScreenWidth);

        mLetterList = Arrays.asList(mLetters);
        mTxtViewList = new LinkedList<>();

        int length = mLetters.length;
        TextView txtLetter;
        for (int i = 0; i < length; i++) {

            txtLetter = new TextView(context);
            txtLetter.setPadding(mLetterHorizontalSpan, 0, mLetterScreenVerticalSpan, 0);
            txtLetter.setText(mLetters[i]);
            txtLetter.setTextColor(getResources().getColor(android.R.color.black));
            txtLetter.setTextSize(13);
            txtLetter.setOnTouchListener(this);
            txtLetter.setGravity(Gravity.CENTER_HORIZONTAL);

            mTxtViewList.add(txtLetter);
            this.addView(txtLetter);
        }

        mTxtInPopup = new TextView(context);
        mTxtInPopup.setTextSize(36);
        mTxtInPopup.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        mTxtInPopup.setGravity(Gravity.CENTER);

        mPopupWindow = new PopupWindow(mScreenWidth / 4, mScreenWidth / 4);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_alphabet));
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setContentView(mTxtInPopup);

        mLetterVerticalSpan = getDpToPx(context, mLetterScreenVerticalSpan);



    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v != null) {
            if (v instanceof TextView) {
                TextView txtLetter = (TextView) v;
                if (event != null) {


                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            this.setBackgroundColor(getResources().getColor(R.color.alphabetLinearLayout_bg));

                            mTouchLetter = txtLetter.getText().toString().trim();
                            mTouchLetterIndex = mLetterList.indexOf(mTouchLetter);
                            mTxtInPopup.setText(mTouchLetter);

                            mItemIndexEvent.currentIndex = mTouchLetterIndex;
                            EventBus.getDefault().post(mItemIndexEvent);


                            if (!mPopupWindow.isShowing()) {
                                mPopupWindow.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
                            }

                            break;
                        case MotionEvent.ACTION_MOVE:

                            mCurrentY = event.getRawY();

                            if(mLastY > 0) {
                                float value = mCurrentY - mLastY;
                                mVerticalSpanTotal += value;
                                int spanNumber = (int) (mVerticalSpanTotal / mLetterVerticalSpan);

                                int order = mTouchLetterIndex + spanNumber;
                                if (order < 0) {
                                    order = 0;
                                }
                                if (order >= mLetterList.size()) {
                                    order = mLetterList.size()-1;
                                }

                                mTouchLetter = mLetterList.get(order);
                                mTxtInPopup.setText(mTouchLetter);

                                mItemIndexEvent.currentIndex = order;
                                EventBus.getDefault().post(mItemIndexEvent);
                            }

                            mLastY = mCurrentY;

                            break;

                        case MotionEvent.ACTION_UP:
                            if (mPopupWindow.isShowing()) {
                                mPopupWindow.dismiss();
                            }
                            mVerticalSpanTotal = 0;
                            mLastY = 0;
                            this.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            setLetterColor(mTouchLetter);
                            break;
                        case MotionEvent.ACTION_CANCEL:

                            break;
                        default:

                    }


                }
            }
        }
        return true;
    }

    private int getDpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dp * scale + 0.5);
    }


    public void setLetterColor(String letter){
        setLetterColor(letter,0);
    }

    public void setLetterColor(String letter, int color){
        if(!TextUtils.isEmpty(letter)){

            if(mLetterList.contains(letter)){
                int currentPosition = mLetterList.indexOf(letter);
                if(currentPosition <= mTxtViewList.size()){
                    if(mLastReactionPosition != currentPosition) {
                        mTxtViewList.get(mLastReactionPosition).setTextColor(getResources().getColor(android.R.color.black));
                    }
                    if(color != 0){
                        try {
                            mTxtViewList.get(currentPosition).setTextColor(color);
                        }catch(Exception e){
                            mTxtViewList.get(currentPosition).setTextColor(getResources().getColor(R.color.letter_color));
                        }
                    }else{
                        mTxtViewList.get(currentPosition).setTextColor(getResources().getColor(R.color.letter_color));
                    }
                }
                if(mLastReactionPosition != currentPosition){
                    mLastReactionPosition = currentPosition;
                }

            }

        }
    }

    public void setInitAlphabet(){
        for(TextView letter : mTxtViewList){
            letter.setTextColor(Color.BLACK);
        }
    }

}
