package com.tonight.manage.organization.managingmoneyapp.Object;

/**
 * Created by Taek on 2016. 11. 9..
 */

public class EventListItem {
    String membernum;
    String eventnum;
    String eventname;
    String targetm;
    String summ;
    String eventdate;
    String personalm;
    String bubblecount;
    public EventListItem setMembernum(String membernum) {
        this.membernum = membernum;
        return this;
    }
    public EventListItem setEventnum(String eventnum) {
        this.eventnum = eventnum;
        return this;
    }
    public EventListItem setEventname(String eventname) {
        this.eventname = eventname;
        return this;
    }
    public EventListItem setTargetm(String targetm) {
        this.targetm = targetm;
        return this;
    }
    public EventListItem setSumm(String summ) {
        this.summ = summ;
        return this;
    }
    public EventListItem setEventdate(String eventdate) {
        this.eventdate = eventdate;
        return this;
    }
    public EventListItem setPersonalm(String personalm) {
        this.personalm = personalm;
        return this;
    }
    public EventListItem setBubblecount(String bubblecount) {
        this.bubblecount = bubblecount;
        return this;
    }

    public String getEventnum() {
        return eventnum;
    }
    public String getMembernum() {
        return membernum;
    }
    public String getEventname() {
        return eventname;
    }
    public String getTargetm() {
        return targetm;
    }
    public String getSumm() {
        return summ;
    }
    public String getEventdate() {
        return eventdate;
    }
    public String getPersonalm() {
        return personalm;
    }
    public String getBubblecount() {
        return bubblecount;
    }





}
