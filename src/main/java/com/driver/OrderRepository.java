package com.driver;


import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderMap;
    HashMap<String,DeliveryPartner> partnerMap;
    HashMap<String,String> orderToPartnerMap;                     // key : orderid , value : partnerid
    HashMap<String, HashSet<String>> partnerToOrderMap;           // key : partnerid , value : HashSet<orderid>

    public OrderRepository() {
        this.orderMap = new HashMap<String,Order>();
        this.partnerMap = new HashMap<String,DeliveryPartner>();
        this.orderToPartnerMap = new HashMap<String,String>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
    }

    public void addorder(Order order){                                                            // 1st API

            String key = order.getId();
            if(key != null)
              orderMap.put(key, order);
    }

    public void addPartner(String partnerId){                                                     // 2nd API
        DeliveryPartner deliveryPartner = new DeliveryPartner((partnerId));
        String key = deliveryPartner.getId();
        partnerMap.put(key,deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId,String partnerId){                             // 3rd API

        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){

            HashSet<String> currentOrders = new HashSet<>();

            if(partnerToOrderMap.containsKey(partnerId)){
                currentOrders = partnerToOrderMap.get(partnerId);
            }
            currentOrders.add(orderId);
            partnerToOrderMap.put(partnerId,currentOrders);

            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(currentOrders.size());

            orderToPartnerMap.put(orderId, partnerId);
        }
    }

    public Order getOrderById(String orderId){                                                        // 4th API
        Order order = orderMap.get(orderId);
        return order;
    }

    public DeliveryPartner getPartnerById(String partnerId){                                            // 5th API

        DeliveryPartner deliveryPartner = null;

        if(partnerMap.containsKey(partnerId))
           deliveryPartner = partnerMap.get(partnerId);

        return deliveryPartner;
    }

    public Integer getOrderCountByPartnerId(String partnerId){                                          // 6th API

        int orderCount = 0;

        if(partnerToOrderMap.containsKey(partnerId))
            orderCount = partnerToOrderMap.get(partnerId).size();

        return orderCount;
    }

    public List<String> getOrdersByPartnerId(String partnerId){                                         // 7th API

        HashSet<String> orderList = null;

        if(partnerToOrderMap.containsKey(partnerId))
            orderList = partnerToOrderMap.get(partnerId);

        return new ArrayList<>(orderList);
    }

    public List<String> getAllOrders(){                                                                  // 8th API
        List<String> orders = new ArrayList<>(orderMap.keySet());
        return orders;
    }

    public Integer getCountOfUnassignedOrders(){                                                         // 9th API
        Integer countOfOrders = 0;

        List<String> list = new ArrayList<>(orderMap.keySet());

        for(String st : list){
            if(!orderToPartnerMap.containsKey(st))
                countOfOrders += 1;
        }

        return countOfOrders;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){                // 10th API

        int countOfOrders = 0;
        int hours = Integer.valueOf(time.substring(0,2));
        int minutes = Integer.valueOf(time.substring(3));
        int total = hours*60 + minutes;

        if(partnerToOrderMap.containsKey(partnerId))
        {
            HashSet<String> set = partnerToOrderMap.get(partnerId);

            for(String st : set)
            {
                if(orderMap.containsKey(st))
                {
                    Order order = orderMap.get(st);

                    if(total < order.getDeliveryTime())
                        countOfOrders++;
                }
            }
        }

        return countOfOrders;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {                                    // 11th API
        String time = null;
        int delivery_time = 0;

        if(partnerMap.containsKey(partnerId))
        {
            HashSet<String> list = partnerToOrderMap.get(partnerId);

            for(String st : list)
            {
                if(orderMap.containsKey(st))
                {
                    Order order = orderMap.get(st);

                    if(delivery_time < order.getDeliveryTime())
                        delivery_time = order.getDeliveryTime();
                }
            }
        }
        StringBuilder str = new StringBuilder();

        int hr = delivery_time / 60;                 // calculate hour
        if(hr < 10)
            str.append(0).append(hr);
        else
            str.append(hr);

        str.append(":");

        int min = delivery_time - (hr*60);          // calculate minutes
        if(min < 10)
            str.append(0).append(min);
        else
            str.append(min);

//        str.append(min);

        return str.toString();
    }

    public void deletePartnerById(String partnerId) {                                                    // 12th API

        HashSet<String> list = new HashSet<>();

        if(partnerToOrderMap.containsKey(partnerId))
        {
            list = partnerToOrderMap.get(partnerId);

            for (String st : list) {
//                orderMap.remove(st);

                if (orderToPartnerMap.containsKey(st))
                    orderToPartnerMap.remove(st);
            }

            partnerToOrderMap.remove(partnerId);
        }

        if(partnerMap.containsKey(partnerId)) {
            partnerMap.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId){                                                        // 13th API                                                                                            // 13th API

        if(orderToPartnerMap.containsKey(orderId))
        {
            String partnerId = orderToPartnerMap.get(orderId);

            HashSet<String> list = partnerToOrderMap.get(partnerId);
            list.remove(orderId);
            partnerToOrderMap.put(partnerId,list);

            DeliveryPartner deliveryPartner = partnerMap.get(partnerId);
            deliveryPartner.setNumberOfOrders(list.size());
        }

        if(orderMap.containsKey(orderId)) {
            orderMap.remove(orderId);
        }
    }
}
