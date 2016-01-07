package com.schedule.risebes.schedule.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Groups {
    private Map<Integer,String> groups;
    public Map<Integer,String> getGroups()
    {
        return groups;
    }
}
