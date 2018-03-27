/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erstegroupit.restclient;

import com.erstegroupit.model.AllocationData;
import com.erstegroupit.model.DataModel;
import com.erstegroupit.model.DealData;
import com.erstegroupit.model.SubscriptionData;
import com.erstegroupit.model.TrancheData;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author H50UDBB
 */
@Singleton
public class SSDRestClient {

    private static final String REST_URI = "http://ssd-dlt-poc.eb.lan.at:4000";
    private static final DateTimeFormatter YYYYMMMDD_FMT = DateTimeFormatter.ofPattern("yyyy-MMM-dd", Locale.UK);
    
    private String token;    

    public SSDRestClient() {
    }

    public void authenticate() {
        Client client = ClientBuilder.newClient();

        Form form = new Form();
        form.param("username", "jim");
        form.param("orgName", "org1");

        Response response = client
                .target(REST_URI + "/users")
                .path("")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), Response.class);

        if (response.getStatus() == 200) {
            AuthResponse obj = response.readEntity(AuthResponse.class);
            System.out.println("Answer: " + obj);
            System.out.println("Tokem: " + obj.getToken());
            client.close();
            
            token = obj.getToken();
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }
    }
    
    public CreateDealResponse createDeal(DealData deal) {
        Client client = ClientBuilder.newClient();

        String issueDate = deal.getIssueDate().format(YYYYMMMDD_FMT);
        String expiryDate = deal.getExpiryDate().format(YYYYMMMDD_FMT);
        
        CreateDeal crd = new CreateDeal("createDeal", new String[]{deal.getIssuerName(), deal.getIssuedAmount().toString(), issueDate, expiryDate});
        
        System.out.println("REST CALL: " + crd);
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {            
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }        
    }
    
    public CreateDealResponse readDeal(String dealId) {
        Client client = ClientBuilder.newClient();
        
        CreateDeal crd = new CreateDeal("readDeal", new String[]{dealId});
        
        System.out.println("REST CALL: " + crd);
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .queryParam("peer", "peer1")
                .queryParam("fcn", "readDeal")
                .queryParam("args", new String[]{dealId})
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            System.out.println("Answer is: " + obj);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }        
    }
    
    public CreateDealResponse readDeals() {
        Client client = ClientBuilder.newClient();                      
        CreateDeal crd = new CreateDeal("readAllDeals", new String[]{});        
        System.out.println("REST CALL: " + crd);        
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .queryParam("peer", "peer1")
                .queryParam("fcn", "readAllDeal")
                .queryParam("args", new String[]{})
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);        
        
        /*
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes")
                .path("ssd")
                .queryParam("peer", "peer1")
                .queryParam("fcn", "readAllDeals")
                .queryParam("args", new String[]{"[]"})
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + dataModel.getToken())
                .get()
                ;
        */
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            System.out.println("Answer is: " + obj);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }        
    }
    
    public CreateDealResponse createTranche(TrancheData tranche) {
        Client client = ClientBuilder.newClient();

        CreateDeal crd = new CreateDeal("createTranche", new String[]{tranche.getDealId(), tranche.getTrancheAmount().toString()});
        
        System.out.println("REST CALL: " + crd);
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }         
    }
    
    public CreateDealResponse readTranche(String trancheId) {
        Client client = ClientBuilder.newClient();
        
        CreateDeal crd = new CreateDeal("readTranche", new String[]{trancheId});
        
        System.out.println("REST CALL: " + crd);
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .queryParam("peer", "peer1")
                .queryParam("fcn", "readTranche")
                .queryParam("args", new String[]{trancheId})
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            System.out.println("Answer is: " + obj);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }        
    }
    
    public CreateDealResponse readSubscription(String subscriptionId) {
        Client client = ClientBuilder.newClient();
        
        CreateDeal crd = new CreateDeal("readSubscription", new String[]{subscriptionId});
        
        System.out.println("REST CALL: " + crd);
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .queryParam("peer", "peer1")
                .queryParam("fcn", "readSubscription")
                .queryParam("args", new String[]{subscriptionId})
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            System.out.println("Answer is: " + obj);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }        
    }
    
    public CreateDealResponse createSubscription(SubscriptionData subscription) {
        
        String initDate = subscription.getInitDate().format(YYYYMMMDD_FMT);
        
        Client client = ClientBuilder.newClient();

        CreateDeal crd = new CreateDeal("createSubscription", new String[]{subscription.getInvestorId(), subscription.getTrancheId(), 
            initDate, subscription.getTargetAmount().toString(), subscription.getSpreadLimit().toString()});
        
        System.out.println("REST CALL: " + crd);
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }         
    }  
    
    public CreateDealResponse createAllocation(AllocationData allocation) {
        
        String initDate = allocation.getInitDate().format(YYYYMMMDD_FMT);
        
        Client client = ClientBuilder.newClient();

        CreateDeal crd = new CreateDeal("createAllocation", new String[]{allocation.getInvestorId(), allocation.getTrancheId(), 
            initDate, allocation.getAllocationAmount().toString(), "true"});
        
        System.out.println("REST CALL: " + crd);
        
        Response response = client
                .target(REST_URI + "/channels/mychannel/chaincodes/ssd")
                .path("")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(crd, MediaType.APPLICATION_JSON), Response.class);
        
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CreateDealResponse obj = response.readEntity(CreateDealResponse.class);
            client.close();
            return obj;
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
            client.close();
            throw new RuntimeException("Error calling  \"" + REST_URI + "\": " + response.getStatusInfo().getReasonPhrase());
        }         
    }  
    

}
