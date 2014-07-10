package xxmmk.mobile.toir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectAdapter extends SimpleAdapter {
    private Context mContext;
    private MobileTOiRApp mMobileTOiRApp;
    private List<HashMap<String,String>> results;


    public ObjectAdapter(Context context, List<HashMap<String,String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        mContext = context;
        mMobileTOiRApp = MobileTOiRApp.getInstance();
        //Log.d(mMobileTOiRApp.getLOG_TAG(), "ObjectAdapter " + mMobileTOiRApp.getmHASH());
        this.results = data;

    }

    public View getView(final int position, View convertView,ViewGroup parent)
    {

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = convertView;

        ViewHolder viewHolder;

        if(convertView == null) {

            vi = inflater.inflate(R.layout.item_objects, null);
            viewHolder = new ViewHolder();

            viewHolder.text1=(TextView)vi.findViewById(R.id.objects_text1);
            viewHolder.text2=(TextView)vi.findViewById(R.id.objects_text2);
            viewHolder.buttonScan=(ImageButton)vi.findViewById(R.id.buttonScan);

            viewHolder.rel = (RelativeLayout)vi.findViewById(R.id.item_objects);

            viewHolder.lin = (LinearLayout)vi.findViewById(R.id.orgs);

            vi.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) vi.getTag();
        }
        //vi.refreshDrawableState();

        viewHolder.text1.setText(results.get(position).get("DESCRIPTION"));
        viewHolder.text2.setText(results.get(position).get("CODE"));

        //TextView text1 = (TextView) convertView.findViewById(R.id.objects_text1);
        //TextView text2 = (TextView) convertView.findViewById(R.id.objects_text2);

        //text1.setText(results.get(position).get("DESCRIPTION"));
        //text2.setText(results.get(position).get("CODE"));

        String newCode = mMobileTOiRApp.getmDbHelper().getCountNewCode(results.get(position).get("OBJECT_ID"));
        //Log.v(mMobileTOiRApp.getLOG_TAG(), " position = " + position + " newCode=" + newCode);
        //viewHolder.text2.setText(newCode);
        if (newCode!=null && !newCode.equals("0")) {
            //Log.v(mMobileTOiRApp.getLOG_TAG(), "newCode!=null: position = " + position + " newCode=" + newCode);
            int color = Color.argb( 200, 0, 64, 64 );
            viewHolder.text2.setBackgroundColor(color);
            viewHolder.text2.setText(newCode);
        } else {
            viewHolder.text2.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }

        //ImageButton Button1= (ImageButton)  convertView.findViewById(R.id.buttonScan);

        viewHolder.buttonScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, ScanActivity.class);
                Bundle b = new Bundle();
                b.putString("ORG_ID", results.get(position).get("ORG_ID"));
                b.putString("OBJECT_ID", results.get(position).get("OBJECT_ID"));
                b.putString("ORG_CODE", results.get(position).get("ORG_CODE"));
                b.putString("DESCRIPTION", results.get(position).get("DESCRIPTION"));
                String newCode = mMobileTOiRApp.getmDbHelper().getCountNewCode(results.get(position).get("OBJECT_ID"));
                if (newCode!=null && !newCode.equals("0")) {
                    b.putString("CODE", newCode);
                } else {
                    b.putString("CODE", results.get(position).get("CODE"));
                }
                //b.putSerializable("HashMap",obj);
                //Log.v(mMobileTOiRApp.getLOG_TAG(), "itemClick: position = " + position + " DESCRIPTION=" + results.get(position).get("DESCRIPTION"));
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

        });


        return vi ;
    }

    static class ViewHolder {
        TextView text1;
        TextView text2;
        ImageButton buttonScan;
        RelativeLayout rel;
        LinearLayout lin;
    }
}
