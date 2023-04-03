package com.driver;


import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String, Order> orderDB = new HashMap<>();
    HashMap<String, DeliveryPartner> partnerDB = new HashMap<>();

    HashMap<String,String> orderPartnerPair = new HashMap<>();
    HashMap<String, HashSet<Order>> pairDB = new HashMap<>();
    public void addOrder(Order order) {
        String key = order.getId();
        if(key != null)
            orderDB.put(key, order);
        //orderDB.put(order.getId(),order);
    }

    public void addPartner(DeliveryPartner deliveryPartner) {
        partnerDB.put(deliveryPartner.getId(), deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        orderPartnerPair.put(orderId,partnerId);
        HashSet <Order> hs = pairDB.get(partnerId);
        if(hs == null){
            hs = new HashSet<>();
        }
        hs.add(orderDB.get(orderId));
        pairDB.put(partnerId, hs);
    }

    public Order getOrderById(String orderId) {
        return orderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        DeliveryPartner deliveryPartner = null;

        if(partnerDB.containsKey(partnerId))
            deliveryPartner = partnerDB.get(partnerId);

        return deliveryPartner;
        //return partnerDB.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return pairDB.get(partnerId).size();
    }

    public List<String> getOrderByPartnerId(String partnerId) {
        HashSet <Order> orderList = pairDB.get(partnerId);
        List <String> ans = new ArrayList<>();
        for(Order order : orderList){
            ans.add(order.getId());
        }
        return ans;
    }

    public List<String> getAllOrders() {
        List <String> ans = new ArrayList<>();
        for(Order order : orderDB.values()){
            ans.add(order.getId());
        }
        return ans;
    }

    public Integer getCountOfUnassignedOrders() {
        Integer countOfOrders = 0;

        List<String> list = new ArrayList<>(orderDB.keySet());

        for(String st : list){
            if(!orderPartnerPair.containsKey(st))
                countOfOrders += 1;
        }

        return countOfOrders;

        //return orderDB.size() - orderPartnerPair.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int givenTime = Integer.parseInt(time.substring(0,2)) * 60;
        givenTime += Integer.parseInt(time.substring(3));
        int count = 0;
        System.out.println(givenTime);
        for(Order order : pairDB.get(partnerId)){
            if(order.getDeliveryTime() > givenTime){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int ans = Integer.MIN_VALUE;
        for(Order order : pairDB.get(partnerId)){
            if(order.getDeliveryTime() > ans){
                ans = order.getDeliveryTime();
            }
        }
        String ansTime = Integer.toString(ans%60);
        if(ans % 60 < 10){
            ansTime = "0"+ansTime;
        }
        ansTime = Integer.toString(ans/60) + ":" + ansTime;
        return ansTime;
    }

    public void deletePartnerById(String partnerId){
        HashSet <Order> orderList = pairDB.get(partnerId);
        partnerDB.remove(partnerId);
        pairDB.remove(partnerId);
        for(Order order : orderList){
            //orderDB.remove(order.getId());
            orderPartnerPair.remove(order.getId());
        }
    }
    public void deleteOrderById(String orderId) {
        Order order = orderDB.get(orderId);
        String partnerId = orderPartnerPair.get(orderId);
        orderDB.remove(orderId);
        HashSet <Order> hs = pairDB.get(partnerId);
        if(hs == null){
            return;
        }
        orderPartnerPair.remove(orderId);
        if(hs.contains(order)){
            hs.remove(order);
        }
        pairDB.put(partnerId,hs);
        DeliveryPartner partner = partnerDB.get(partnerId);
        partner.setNumberOfOrders(partner.getNumberOfOrders()-1);
    }


}
