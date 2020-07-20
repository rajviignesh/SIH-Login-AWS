package de.cryptiot.indoorfarming2;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAccess {
    private static final String COGNITO_POOL_ID = "ap-south-1:4e2421c4-889b-46a5-b01f-943fd20d2fe3";
    private static final Regions MY_REGION = Regions.AP_SOUTH_1;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private Context context;
    CognitoCachingCredentialsProvider credentialsProvider;


    private static volatile DatabaseAccess instance;
    private DatabaseAccess (Context context) {
        this.context =context;
        credentialsProvider = new CognitoCachingCredentialsProvider (context, COGNITO_POOL_ID, MY_REGION);
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        dbTable = Table.loadTable(dbClient, "Test");
    }
    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }
    public List<Float> getItem (String column, String duration) {
        //Scan Part
        List<String> val= new ArrayList<>();
        val.add(column);
        ScanOperationConfig scanConfig= new ScanOperationConfig();
        scanConfig.withAttributesToGet(val);
        Search searchResult=dbTable.scan(scanConfig);
        double number=searchResult.getAllResults().size();


        //Query Part
        Date date= new Date();
        SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
        String strdate = formatter.format(date);
        Long nowtime =date.getTime();
        Long timeval = null;
        SimpleDateFormat form = new SimpleDateFormat("HH:mm");
        if(duration=="Onehour")
        {
            timeval = nowtime-(1*60*60*1000);

        }
        else if(duration=="TwoHours")
        {
            timeval = nowtime-(2*60*60*1000);

        }
        else if(duration=="24Hours")
        {
            timeval = nowtime-(24*60*60*1000);
        }





        Map<String,String> expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#SNO","SNO");
        expressionAttributesNames.put("#Date","Date");
        expressionAttributesNames.put("#Time","Time");

        double j=1;
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        List<Float> attribute = new ArrayList<>();
        float value;

        while(j<=number) {

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":SNOValue", new AttributeValue().withN(Double.toString(j)));
            expressionAttributeValues.put(":DateValue",new AttributeValue().withS(strdate));
            expressionAttributeValues.put(":TimeValue",new AttributeValue().withN(Long.toString(timeval)));

            QueryResult result = null;
            QueryRequest req = new QueryRequest()
                    .withTableName("Test")
                    .withKeyConditionExpression("#SNO = :SNOValue and #Time > :TimeValue")
                    .withFilterExpression("#Date = :DateValue ")
                    .withExpressionAttributeNames(expressionAttributesNames)
                    .withExpressionAttributeValues(expressionAttributeValues);
            req.setTableName("Test");
            req.setIndexName("SNO-Time-index");

            result = dbClient.query(req);
            List<Map<String, AttributeValue>> rows = result.getItems();
            items.addAll(rows);

            j++;

        }
        //Column traversal
        for (Map<String, AttributeValue> map : items) {
            try {
                AttributeValue nameValue = map.get(column);
                value = Float.parseFloat(nameValue.getN());
                attribute.add(value);
                //  Collections.reverse(attribute);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        return attribute;
    }


    public List<Long> getTime(String duration){

        List<String> val= new ArrayList<>();
        val.add("Time");
        ScanOperationConfig scanConfig= new ScanOperationConfig();
        scanConfig.withAttributesToGet(val);
        Search searchResult=dbTable.scan(scanConfig);
        double number=searchResult.getAllResults().size();


        //Query Part
        Date date= new Date();
        SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
        String strdate = formatter.format(date);
        Long nowtime =date.getTime();
        Long timeval = null;
        SimpleDateFormat form = new SimpleDateFormat("HH:mm");
        if(duration=="Onehour")
        {
            timeval = nowtime-(1*60*60*1000);

        }
        else if(duration=="TwoHours")
        {
            timeval = nowtime-(2*60*60*1000);

        }
        else if(duration=="24Hours")
        {
            timeval = nowtime-(24*60*60*1000);
        }


        Map<String,String> expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#SNO","SNO");
        expressionAttributesNames.put("#Date","Date");
        expressionAttributesNames.put("#Time","Time");

        double j=1;
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        List<Long> attribute = new ArrayList<>();
        long value;

        while(j<=number) {

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":SNOValue", new AttributeValue().withN(Double.toString(j)));
            expressionAttributeValues.put(":DateValue",new AttributeValue().withS(strdate));
            expressionAttributeValues.put(":TimeValue",new AttributeValue().withN(Long.toString(timeval)));

            QueryResult result = null;
            QueryRequest req = new QueryRequest()
                    .withTableName("Test")
                    .withKeyConditionExpression("#SNO = :SNOValue and #Time > :TimeValue")
                    .withFilterExpression("#Date = :DateValue")
                    .withExpressionAttributeNames(expressionAttributesNames)
                    .withExpressionAttributeValues(expressionAttributeValues);
            req.setTableName("Test");
            req.setIndexName("SNO-Time-index");

            result = dbClient.query(req);
            List<Map<String, AttributeValue>> rows = result.getItems();
            items.addAll(rows);

            j++;

        }
        //Column traversal
        for (Map<String, AttributeValue> map : items) {
            try {
                AttributeValue nameValue = map.get("Time");
                value = Long.parseLong(nameValue.getN());
                attribute.add(value);
                //  Collections.reverse(attribute);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        return attribute;

    }



    public List<Document> getAllItems() {

        ScanOperationConfig scanConfig= new ScanOperationConfig();
        List<String> attributeList= new ArrayList<>();
        attributeList.add("Time");
        attributeList.add("HR");
        attributeList.add("RR");
        attributeList.add("TEMP");
        attributeList.add("BP(sys)");
        attributeList.add("BP(dia)");
        scanConfig.withAttributesToGet(attributeList);
        Search searchResult=dbTable.scan(scanConfig);
        return searchResult.getAllResults();

    }

    public void pushItems(){
        Date date= new Date();
        Table table=Table.loadTable(dbClient,"Test");
        Document Med= new Document();
        Med.put("SNO",1);
        Med.put("Time", date.getTime());
        Med.put("Date","20/07/2020");
        Med.put("HR", 72);
        Med.put("RR", 14);
        Med.put("TEMP", 37);
        Med.put("BP(sys)", 111);
        Med.put("BP(dia)",67);
        table.putItem(Med);

    }

    public Map<String,AttributeValue> queryItems(String Med) {


        Map<String,String> expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#Med","Medname");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":MedValue",new AttributeValue().withS(Med));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("PillReminder")
                .withKeyConditionExpression("#Med = :MedValue")
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues);

        QueryResult queryResult = dbClient.query(queryRequest);
        List<Map<String,AttributeValue>> attributeValues = queryResult.getItems();
        if(attributeValues.size()>0) {
            return attributeValues.get(0);
        } else {
            return null;
        }
    }

    public List<Double> sort(List<Double> time){
        int i;
        double temp;
        for(i=0;i<time.size();i++) {
            if (time.get(i) > time.get(i + 1))
            {
                temp = time.get(i);
                time.set(i, time.get(i + 1));
                time.set(i + 1, temp);
            }
        }

        return null;
    }



}