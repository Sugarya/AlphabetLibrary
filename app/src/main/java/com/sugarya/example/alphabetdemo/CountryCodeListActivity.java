package com.sugarya.example.alphabetdemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sugarya.example.alphabetdemo.custom.AlphabetLinearLayout;
import com.sugarya.example.alphabetdemo.event.AlphabetValueEvent;
import com.sugarya.example.alphabetdemo.event.ItemIndexEvent;
import com.sugarya.example.alphabetdemo.utils.ParserUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class CountryCodeListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = "CountryCodeListActivity";
    private static final String[] mLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //widgets
    private AlphabetLinearLayout mAlphabetLinearLayout;
    private RelativeLayout mHeaderContainer1;
    private RelativeLayout mHeaderContainer2;
    private EditText mEdSearch;
    private ImageView mImgSearch,mHeaderBack,mSearchClose;

    //arguments
    private List<String> mCountryNameListOrigin = new ArrayList<>();
    private List<String> mCountryCodeListOrigin;

    private List<String> mCountryNameList = new ArrayList<>();
    private List<String> mCountryCodeList=new ArrayList<>();

    //查询结果集
    private List<String> mCountryResultList = new ArrayList<>();
    private List<String> mCountryCodeResultList = new ArrayList<>();

    private List<String> mLetterList;
    private ListView mListView;
    private CountryCodeAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.country_code_list);
        EventBus.getDefault().register(this);

        mLetterList = Arrays.asList(mLetters);
        parsePList();

        ///////////////////////////////////////////////////////////////////////////initView;

        mAlphabetLinearLayout = (AlphabetLinearLayout)findViewById(R.id.register_country_code_alphabet);
        mHeaderContainer1 = (RelativeLayout) findViewById(R.id.register_country_code_header1_container);
        mHeaderContainer2 = (RelativeLayout) findViewById(R.id.register_country_code_header2_container);
        mEdSearch = (EditText) findViewById(R.id.register_country_code_header2_search_ed);
        mImgSearch = (ImageView) findViewById(R.id.register_country_code_header1_search_img);
        mHeaderBack = (ImageView) findViewById(R.id.register_country_code_header_back);
        mSearchClose = (ImageView) findViewById(R.id.register_country_code_header2_close);
        mListView = (ListView) findViewById(R.id.register_country_code_list);

        mAdapter = new CountryCodeAdapter(this, mCountryNameList, mCountryCodeList);
        mListView.setAdapter(mAdapter);

        ///////////////////////////////////////////////////////////////////////////////initEvent

        mHeaderBack.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mImgSearch.setOnClickListener(this);
        mSearchClose.setOnClickListener(this);
        mEdSearch.setOnEditorActionListener(this);

        ////////////////////////////////////////////////////////////////////////////////initData



    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private void parsePList() {

        AssetManager assetManager = getResources().getAssets();
        String filePath = "country_cn.plist";
        try {
            InputStream inputStream = assetManager.open(filePath);
            mCountryCodeListOrigin = ParserUtils.parseToArrayList(inputStream);

            for (String str : mCountryCodeListOrigin) {
                String[] strings = str.split("\\+");
                mCountryNameListOrigin.add(strings[0]);
                mCountryNameList.add(strings[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String s:mCountryCodeListOrigin){
            mCountryCodeList.add(s);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.register_country_code_header1_search_img:
                    mHeaderContainer2.setVisibility(View.VISIBLE);
                    mHeaderContainer1.setVisibility(View.GONE);
                    break;
                case R.id.register_country_code_header2_close:
                    String content = mEdSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        mHeaderContainer2.setVisibility(View.GONE);
                        mHeaderContainer1.setVisibility(View.VISIBLE);
                    } else {
                        mEdSearch.setText("");
                        mAdapter.updateData(mCountryNameListOrigin, mCountryCodeListOrigin);
                    }
                    mAlphabetLinearLayout.setLetterColor("A");

                    break;
                case R.id.register_country_code_header_back:

                    //淡出动画
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(300);
                    alphaAnimation.setFillAfter(true);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            onBackPressed();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    v.setAnimation(alphaAnimation);
                    v.startAnimation(alphaAnimation);

                    break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        String content = v.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {

            mAlphabetLinearLayout.setInitAlphabet();

            mCountryResultList.clear();
            mCountryCodeResultList.clear();
            int len = mCountryNameList.size();
            for (int i = 0; i < len; i++) {
                String countryName = mCountryNameListOrigin.get(i);
                String countryCode = mCountryCodeListOrigin.get(i);
                if (countryName.contains(content)) {
                    mCountryResultList.add(countryName);
                    mCountryCodeResultList.add(countryCode);
                }
            }
            mAdapter.updateData(mCountryResultList, mCountryCodeResultList);

        }

        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String countryCode = (String) parent.getItemAtPosition(position);

        if (!mLetterList.contains(countryCode)) {
            EventBus.getDefault().post(new AlphabetValueEvent(countryCode));
            finish();
        }

    }

    public void onEventMainThread(ItemIndexEvent event) {
        if (event != null) {
            String letter = mLetterList.get(event.currentIndex);
            int indexOf = mCountryNameList.indexOf(letter);
            mListView.setSelection(indexOf);

            mAlphabetLinearLayout.setLetterColor(letter);

        }
    }




    public class CountryCodeAdapter extends BaseAdapter {

        private Context mContext;

        private List<String> countryNameList;
        private List<String> countryCodeList;

        public CountryCodeAdapter(Context mContext, List<String> countryNameList, List<String> countryCodeList) {
            this.mContext = mContext;
            this.countryNameList = countryNameList;
            this.countryCodeList = countryCodeList;
        }

        @Override
        public int getCount() {
            return countryNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return countryCodeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String str = countryNameList.get(position).trim();
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_country_code_layout,null);
            }
            TextView txtCountry = (TextView)convertView.findViewById(R.id.item_country_code_txt);
            txtCountry.setText(str);

            if(mLetterList.contains(str)){
                txtCountry.setTextColor(mContext.getResources().getColor(R.color.letter_color));
                if(position != 0) {
                    mAlphabetLinearLayout.setLetterColor(str);
                }
            }else{
                txtCountry.setTextColor(mContext.getResources().getColor(R.color.country_color));
            }


            return convertView;
        }

        public void updateData(List<String> cList,List<String> cCodeList){
            if(cList != null && cCodeList != null){
                if(this.countryNameList != null && this.countryCodeList != null) {

                    this.countryNameList.clear();
                    this.countryCodeList.clear();
                    this.countryNameList.addAll(cList);
                    this.countryCodeList.addAll(cCodeList);

                    notifyDataSetChanged();

                }
            }

        }
    }

}
