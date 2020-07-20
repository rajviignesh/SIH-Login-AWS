/*
 * Developed by Keivan Kiyanfar on 19/7/20 12:37 PM
 * Last modified 19/7/20 12:32 PM
 * Copyright (c) 2020. All rights reserved.
 */

package de.cryptiot.indoorfarming2;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import de.cryptiot.indoorfarming.R;


public class Monitoring extends Fragment {
    public Monitoring() {
        // Required empty public constructor
    }
    private String TAG = "DynamoDb_Demo";
    public TextView textViewval1,textViewval2,textViewval3,textViewval4,textViewval5;
    public ArrayAdapter<CharSequence> adapter;
    private boolean isChecking = true;
    private int radioButton = R.id.allrate;
    View rootView;
    public LineChart lc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_monitoring, container, false);
        textViewval1 = rootView.findViewById(R.id.avg1);
        textViewval2 = rootView.findViewById(R.id.avg2);
        textViewval3 = rootView.findViewById(R.id.avg3);
        textViewval4 = rootView.findViewById(R.id.avg4);
        textViewval5 = rootView.findViewById(R.id.avg5);

        lc= rootView.findViewById(R.id.graph);


        final RadioGroup duration = rootView.findViewById(R.id.radiogroup);

        final RadioGroup duration2=rootView.findViewById(R.id.radio);

        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);

        int id=getResources().getIdentifier("Duration","array",getActivity().getPackageName());
        int spinid=getResources().getIdentifier("spinner_item","layout",getActivity().getPackageName());

        adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), id, spinid);
        adapter.setDropDownViewResource(spinid);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button pushitem = rootView.findViewById(R.id.push);
        pushitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAllItemsAsyncTask addAllItemsAsyncTask= new AddAllItemsAsyncTask();
                addAllItemsAsyncTask.execute();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String vital1="";
                String vital2="";
                String frequency = "";

                int item =duration.getCheckedRadioButtonId();
                int item1=duration2.getCheckedRadioButtonId();
                if(item==R.id.allrate)
                {
                    vital1=null;
                    vital2=null;

                }
                else if(item==R.id.heartrate)
                {
                    vital1="HR";
                    vital2=null;
                }
                else if(item==R.id.resprate)
                {
                    vital1="RR";
                    vital2=null;
                }
                else if(item1==R.id.temp)
                {
                    vital1="TEMP";
                    vital2=null;
                }
                else if(item1==R.id.bp)
                {
                    vital1="BP(sys)";
                    vital2="BP(dia)";
                }


               switch(position)
                {
                    case 0:
                    {
                        frequency="24Hours";
                        new PlotAllItemAsyncTask().execute(vital1,vital2,frequency);
                    }
                    break;
                    case 1:
                    {
                        frequency="TwoHours";
                        new PlotAllItemAsyncTask().execute(vital1,vital2, frequency);

                    }
                    break;
                    case 2:
                    {
                        frequency="Onehour";
                        new PlotAllItemAsyncTask().execute(vital1,vital2, frequency);
                    }
                    break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        duration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    duration2.clearCheck();
                    radioButton = checkedId;
                }
                isChecking = true;

                String vital1 = null;
                String vital2 = null;
                String frequency = "";
                int spin =spinner.getSelectedItemPosition();
                if(spin==0)
                {
                    frequency="24Hours";
                }
                else if(spin==1)
                {
                    frequency="TwoHours";
                }
                else if(spin==2)
                {
                    frequency="Onehour";
                }
                if(checkedId==R.id.allrate)
                {
                    new PlotAllItemAsyncTask().execute(null,null, frequency);
                    textViewval2.setVisibility(View.VISIBLE);
                    textViewval3.setVisibility(View.VISIBLE);
                    textViewval4.setVisibility(View.VISIBLE);
                    textViewval5.setVisibility(View.VISIBLE);

                }
                else if(checkedId==R.id.heartrate)
                {
                    vital1="HR";
                    new PlotAllItemAsyncTask().execute(vital1,null, frequency);
                    textViewval2.setVisibility(View.GONE);
                    textViewval3.setVisibility(View.GONE);
                    textViewval4.setVisibility(View.GONE);
                    textViewval5.setVisibility(View.GONE);
                }
                else if(checkedId==R.id.resprate)
                {
                    vital1="RR";
                    new PlotAllItemAsyncTask().execute(vital1,null, frequency);
                    textViewval2.setVisibility(View.GONE);
                    textViewval3.setVisibility(View.GONE);
                    textViewval4.setVisibility(View.GONE);
                    textViewval5.setVisibility(View.GONE);
                }

            }


        });


        duration2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    duration.clearCheck();
                    radioButton = checkedId;
                }
                isChecking = true;
                String vital1 = null;
                String vital2 = null;
                String frequency = "";
                int spin =spinner.getSelectedItemPosition();
                if(spin==0)
                {
                    frequency="24Hours";
                }
                else if(spin==1)
                {
                    frequency="TwoHours";
                }
                else if(spin==2)
                {
                    frequency="Onehour";
                }

                if(checkedId==R.id.temp)
                {
                    vital1="TEMP";
                    new PlotAllItemAsyncTask().execute(vital1,null, frequency);
                    textViewval2.setVisibility(View.GONE);
                    textViewval3.setVisibility(View.GONE);
                    textViewval4.setVisibility(View.GONE);
                    textViewval5.setVisibility(View.GONE);
                }
                else if(checkedId==R.id.bp)
                {
                    vital1="BP(sys)";
                    vital2="BP(dia)";
                    new PlotAllItemAsyncTask().execute(vital1,vital2, frequency);
                    textViewval2.setVisibility(View.VISIBLE);
                    textViewval3.setVisibility(View.GONE);
                    textViewval4.setVisibility(View.GONE);
                    textViewval5.setVisibility(View.GONE);
                }
            }
        });
        return rootView;
    }
    private class GetAllItemsAsyncTask extends AsyncTask<Void, Void, List<Document>> {
        @Override
        protected List<Document> doInBackground(Void... params)
        {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            //Log.d(TAG, "databases content"+databaseAccess.getAllItems());
            return databaseAccess.getAllItems();
        }

        @Override
        protected void onPostExecute(List<Document> documents) {

        }

    }

    private class AddAllItemsAsyncTask extends AsyncTask<Void,Void,List<Document>>{

        @Override
        protected List<Document> doInBackground(Void... voids) {

            DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getActivity().getApplicationContext());
            databaseAccess.pushItems();
            return null;
        }
    }

    private class QueryItemAsyncTask extends AsyncTask<Void,Void,List<Document>>{

        @Override
        protected List<Document> doInBackground(Void... voids) {

            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            //Log.d(TAG, "databases content"+databaseAccess.queryItems("10:00"));
            return null;
        }


    }

    public class PlotAllItemAsyncTask extends AsyncTask<String,Void,List<Document>>{

        ArrayList<String> myTime = new ArrayList<>();
        @Override
        protected List<Document> doInBackground(String... params) {

            final String vital1=params[0];
            final String vital2=params[1];
            String freq =params[2];

            //System.out.println(vital1);
            //System.out.println(vital2);
            //System.out.println(freq);



            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());

            List<Float> vitalItems= null,bpdItems=null;
            LineDataSet lineDataSet1 = null,lineDataSet2=null,lineDataSet3,lineDataSet4,lineDataSet5;
            List<Long> timeItems =databaseAccess.getTime(freq);
            //System.out.println(timeItems);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

            //For all vitals
            ArrayList<Entry> hrEntrys = new ArrayList<>();
            ArrayList<Entry> rrEntrys = new ArrayList<>();
            ArrayList<Entry> tempEntrys = new ArrayList<>();
            ArrayList<Entry> bpsEntrys = new ArrayList<>();
            ArrayList<Entry> bpdEntrys = new ArrayList<>();


            //For specific parameters
            ArrayList<Entry> yEntrys1 = new ArrayList<>();
            ArrayList<Entry> yEntrys2 = new ArrayList<>();
            int i;
            for(i=0;i<timeItems.size();i++)
            {
                String dateString = formatter.format(new Date(timeItems.get(i)));
                myTime.add(i,dateString);
            }
            //System.out.println(myTime);
            /*Date date= new Date();
            Long nowtime =date.getTime();
            Long onehrback = nowtime-(1*60*60*1000);
            String dateString = formatter.format(new Date(onehrback));
            System.out.println(onehrback);*/

            XAxis xAxis=lc.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if(myTime.size()==0)
                    {
                        return null;
                    }

                    if(myTime.size()==1)
                    {
                        return myTime.get(0);
                    }
                    if(myTime.size() > (int) value) {
                        //System.out.println(myTime.size());
                        //System.out.println(value);
                        return myTime.get((int) value);
                    }
                    else
                        return null;
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });
            xAxis.setGranularityEnabled(true);


            YAxis rightYAxis = lc.getAxisRight();
            rightYAxis.setEnabled(false);

            YAxis leftYAxis =lc.getAxisLeft();
            leftYAxis.setTextColor(Color.WHITE);

            LimitLine ll1 = null,ll2=null,ll3=null,ll4=null,ll5=null,ll6=null;
            final DecimalFormat df = new DecimalFormat("0.00");

            String parameter="";
            lc.getDescription().setEnabled(false);
            ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
            if(vital1!=null)
            {
                if (vital2 == null) {
                    vitalItems = databaseAccess.getItem(vital1, freq);
                    final float vitalavg=averagevital(vitalItems);

                    for (int j = 0; j < vitalItems.size(); j++) {
                        yEntrys1.add(new Entry(j, vitalItems.get(j)));
                    }
                    float yChartMax,yChartMin;
                    lineDataSet1 = new LineDataSet(yEntrys1, vital1);
                    if(vital1=="HR")
                    {
                        parameter="Heart Rate";
                        ll1 =new LimitLine(77f,"Upper Limit");
                        ll2 =new LimitLine(65f,"Lower Limit");
                        lineDataSet1.setColor(Color.RED);

                    }
                    else if(vital1=="RR")
                    {
                        parameter="Respiration Rate";
                        ll1 =new LimitLine(13f,"UpperLimit");
                        ll2 =new LimitLine(9f,"Lower Limit");
                        lineDataSet1.setColor(Color.BLUE);

                    }
                    else if(vital1=="TEMP")
                    {
                        parameter="Temperature";
                        ll1 =new LimitLine(36f,"UpperLimit");
                        ll2 =new LimitLine(30f,"Lower Limit");
                        lineDataSet1.setColor(Color.GREEN);

                    }
                    final String temp = parameter;
                    textViewval1.post(new Runnable() {
                        @Override
                        public void run() {
                            textViewval1.setText(getString(R.string.displaying_avg,temp,String.valueOf(df.format(vitalavg))));
                        }
                    });

                    ll1.setLineWidth(2f);
                    ll1.setLineColor(Color.RED);
                    ll1.enableDashedLine(4f, 4f, 0f);
                    rightYAxis.addLimitLine(ll1);

                    ll2.setLineWidth(2f);
                    ll2.setLineColor(Color.RED);
                    ll2.enableDashedLine(4f, 4f, 0f);
                    rightYAxis.addLimitLine(ll2);

                    lineDataSet1.setValueTextColor(Color.WHITE);


                }


                else if(vital2!=null) {

                    vitalItems = databaseAccess.getItem(vital1, freq);
                    final float vitalavg=averagevital(vitalItems);
                    textViewval1.post(new Runnable() {
                        @Override
                        public void run() {
                            textViewval1.setText(getString(R.string.displaying_avg,"Blood Pressure(sys)",String.valueOf(df.format(vitalavg))));
                        }
                    });

                    for (int j = 0; j < vitalItems.size(); j++) {
                        yEntrys1.add(new Entry(j, vitalItems.get(j)));
                    }
                    bpdItems = databaseAccess.getItem(vital2, freq);

                    final float bpdavg=averagevital(bpdItems);
                    textViewval2.post(new Runnable() {
                        @Override
                        public void run() {
                            textViewval2.setText(getString(R.string.displaying_avg,"Blood Pressure(dia)",String.valueOf(df.format(bpdavg))));
                        }
                    });
                    for (int k = 0; k < bpdItems.size(); k++) {
                        yEntrys2.add(new Entry(k, bpdItems.get(k)));
                    }
                    ll1 = new LimitLine(130f, "UpperLimit");
                    ll2 = new LimitLine(110f, "Lower Limit");

                    ll3 =new LimitLine(90f,"Upper Limit");
                    ll4 =new LimitLine(75f,"Lower Limit");

                    ll1.setLineWidth(2f);
                    ll1.setLineColor(Color.RED);
                    ll1.enableDashedLine(4f,4f,0f);
                    rightYAxis.removeAllLimitLines();
                    rightYAxis.addLimitLine(ll1);

                    ll2.setLineWidth(2f);
                    ll2.setLineColor(Color.RED);
                    ll2.enableDashedLine(4f,4f,0f);
                    rightYAxis.addLimitLine(ll2);

                    ll3.setLineWidth(2f);
                    ll3.setLineColor(Color.RED);
                    ll3.enableDashedLine(4f,4f,0f);
                    rightYAxis.addLimitLine(ll3);

                    ll4.setLineWidth(2f);
                    ll4.setLineColor(Color.RED);
                    ll4.enableDashedLine(4f,4f,0f);
                    rightYAxis.addLimitLine(ll4);

                    lineDataSet1 = new LineDataSet(yEntrys1, vital1);
                    lineDataSet1.setColor(Color.MAGENTA);
                    lineDataSet2 = new LineDataSet(yEntrys2, vital2);
                    lineDataSet1.setColor(Color.CYAN);
                    lineDataSet1.setValueTextColor(Color.WHITE);
                    lineDataSet2.setValueTextColor(Color.WHITE);
                }

                //System.out.println(yEntrys1);
                //System.out.println(lineDataSet1);
                lineDataSet1.setCircleColor(Color.BLACK);


                if (lineDataSet2 != null) {
                    iLineDataSets.add(lineDataSet2);
                    lineDataSet2.setColor(Color.BLUE);
                }
                iLineDataSets.add(lineDataSet1);
                //System.out.println(iLineDataSets);

            }
            else
            {
                rightYAxis.removeAllLimitLines();
                List<Float> hrItems =databaseAccess.getItem("HR",freq);
                List<Float> rrItems =databaseAccess.getItem("RR",freq);
                List<Float> bpsItems =databaseAccess.getItem("BP(sys)",freq);
                List<Float> bpItems =databaseAccess.getItem("BP(dia)",freq);
                List<Float> tempItems =databaseAccess.getItem("TEMP",freq);
                int m;
                for(m=0;m<myTime.size();m++)
                {
                    hrEntrys.add(new Entry(m, hrItems.get(m)));
                    rrEntrys.add(new Entry(m, rrItems.get(m)));
                    tempEntrys.add(new Entry(m, tempItems.get(m)));
                    bpsEntrys.add(new Entry(m, bpsItems.get(m)));
                    bpdEntrys.add(new Entry(m, bpItems.get(m)));
                }
                final float hravg=averagevital(hrItems);
                textViewval1.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewval1.setText(getString(R.string.displaying_avg,"HeartRate",String.valueOf(df.format(hravg))));
                    }
                });
                final float rravg=averagevital(rrItems);
                textViewval2.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewval2.setText(getString(R.string.displaying_avg,"Respiration Rate",String.valueOf(df.format(rravg))));
                    }
                });
                final float tempavg=averagevital(tempItems);
                textViewval3.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewval3.setText(getString(R.string.displaying_avg,"Temperature",String.valueOf(df.format(tempavg))));

                    }
                });
                final float bpsavg=averagevital(bpsItems);
                textViewval4.post(new Runnable() {
                    @Override
                    public void run() {

                        textViewval4.setText(getString(R.string.displaying_avg,"Blood Pressure(sys)",String.valueOf(df.format(bpsavg))));
                    }
                });
                final float bpdavg=averagevital(bpItems);
                textViewval5.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewval5.setText(getString(R.string.displaying_avg,"Blood Pressure(dia)",String.valueOf(df.format(bpdavg))));
                    }
                });

                lineDataSet1 = new LineDataSet(hrEntrys, "HR");
                lineDataSet2 = new LineDataSet(rrEntrys, "RR");
                lineDataSet3 = new LineDataSet(tempEntrys, "TEMPERATURE");
                lineDataSet4 = new LineDataSet(bpsEntrys, "BP(sys)");
                lineDataSet5 = new LineDataSet(bpdEntrys, "BP(dia)");

                lineDataSet1.setColor(Color.RED);
                lineDataSet1.setCircleColor(Color.BLACK);
                lineDataSet1.setValueTextColor(Color.WHITE);

                lineDataSet2.setColor(Color.BLUE);
                lineDataSet2.setCircleColor(Color.BLACK);
                lineDataSet2.setValueTextColor(Color.WHITE);

                lineDataSet3.setColor(Color.GREEN);
                lineDataSet3.setCircleColor(Color.BLACK);
                lineDataSet3.setValueTextColor(Color.WHITE);

                lineDataSet4.setColor(Color.MAGENTA);
                lineDataSet4.setCircleColor(Color.BLACK);
                lineDataSet4.setValueTextColor(Color.WHITE);

                lineDataSet5.setColor(Color.CYAN);
                lineDataSet5.setCircleColor(Color.BLACK);
                lineDataSet5.setValueTextColor(Color.WHITE);


                iLineDataSets.add(lineDataSet1);
                iLineDataSets.add(lineDataSet2);
                iLineDataSets.add(lineDataSet3);
                iLineDataSets.add(lineDataSet4);
                iLineDataSets.add(lineDataSet5);


            }


            Legend legend = lc.getLegend();
            legend.setTextColor(Color.WHITE);

            lc.setDrawBorders(true);

            LineData lineData= new LineData(iLineDataSets);
            //System.out.println(lineData);
            if(lineData!=null) {
                lc.setData(lineData);
                lc.invalidate();
                lc.notifyDataSetChanged();
            }


            return null;
        }
    }

    private ArrayList<Entry> lineChartDataSet(List<Long> vitalItems,List<Long> timeItems){
        ArrayList<Entry> dataset = new ArrayList<Entry>();
        int i;
        for(i=0;i<vitalItems.size();i++)
        {
            //System.out.println(vitalItems.get(i));
            //System.out.println(timeItems.get(i));
            dataset.add(new Entry(timeItems.get(i),vitalItems.get(i)));
        }

        return dataset;

    }


    private float averagevital(List<Float> vital)
    {
        int i; float sum=0,avg;
        for(i=0;i<vital.size();i++)
        {
            sum= sum+vital.get(i);
        }
        avg=sum/vital.size();
        return avg;
    }


}


