package com.gaicomo.app.utils;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.HomeModule.fragment.TagDetailFragment;
import com.gaicomo.app.R;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter implements View.OnClickListener{
    FlowLayout flowLayout;
    List<String> list;
    ArrayList<TextView> viewsArray=new ArrayList<>();
    Context context;

    public TagAdapter(Context context, List<String> list, FlowLayout flowLayout) {
        this.list = list;
        this.flowLayout=flowLayout;
        this.context=context;
        setTags();
    }

    private   void setTags(){
        flowLayout.removeAllViews();
        viewsArray.clear();
        for (int i=0;i<list.size();i++) {
            TextView textView = buildLabel(list.get(i));
            flowLayout.addView(textView);
            viewsArray.add(textView);
            textView.setOnClickListener(this);
        }

    }

    private TextView buildLabel(String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(context.getResources().getColor(R.color.text_dark));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textView.setPadding((int)dpToPx(3), 0, (int)dpToPx(3), 0);
        return textView;
    }

    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View view) {
        for(TextView textView1:viewsArray){
            if(textView1==view){
                ((HomeActivity)context).replaceFragment(TagDetailFragment.newInstance(textView1.getText().toString()));
            }
        }
    }
}
