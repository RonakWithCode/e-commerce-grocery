package com.crazyostudio.ecommercegrocery.HelperClass;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class DynamoDBHelper {
    private static final String ACCESS_KEY = "AKIA4MTWHEZRXLOX5QJI";
    private static final String SECRET_KEY = "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";
    private static final Regions REGION = Regions.AP_SOUTH_1;

    private static DynamoDBMapper dynamoDBMapper;

    public static DynamoDBMapper getDynamoDBMapper() {
        if (dynamoDBMapper == null) {
            AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentials);
            dynamoDBClient.setRegion(com.amazonaws.regions.Region.getRegion(REGION));
            dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
        }
        return dynamoDBMapper;
    }
}
