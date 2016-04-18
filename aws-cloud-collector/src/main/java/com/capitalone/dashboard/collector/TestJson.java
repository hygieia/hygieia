package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.NameValue;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yaf107 on 4/14/16.
 */
public class TestJson {

    public static void main(String[] args) {
        Gson gson = new Gson();
        CloudInstance instance = new CloudInstance();
        instance.setLastUpdatedDate(new Date());
        instance.setVirtualNetworkId("vpc-1234");
        instance.setAge(10);
        instance.setInstanceId("id-1234");
        instance.setImageId("img-1234");
        instance.setInstanceOwner("owner-1234");
        instance.setInstanceType("m3-large");
        instance.setLastAction("stop");
        instance.setPrivateDns("whatever");
        instance.setPrivateIp("1.1.1.1");
        instance.setPublicDns("whatever");
        instance.setPublicIp("1.1.1.1");
        instance.setRootDeviceName("Any/Device");
        instance.setStatus("running");
        instance.setSubnetId("sn-1234");
        instance.getSecurityGroups().add("sg-01");
        List<NameValue> tags = new ArrayList<>();
        NameValue nv = new NameValue("tag1", "value1");
        instance.getTags().add(nv);
        nv = new NameValue("tag2", "value2");
        instance.getTags().add(nv);
        System.out.println(gson.toJson(instance));
    }
}
