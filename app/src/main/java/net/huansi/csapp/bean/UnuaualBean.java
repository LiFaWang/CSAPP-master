package net.huansi.csapp.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/8/9.
 * 10:09
 */

public class UnuaualBean extends WsData{
    /**
     *
     *  sChannelName:异常点
        tStartTime:开始时间
         tEndTime:结束时间
        sCurValue：异常值
        sType：异常值类型
     {
     "STATUS": "0",
     "DATA": [
     {
     "IYUNTERMINALID": "1510",
     "IYUNUSERMODULEID": "2151",
     "ICHANNEL": "8",
     "SCHANNELNAME": "8色伺服报警",
     "EXPMAXVALUE": "1",
     "EXPCOUNT": "4"
     }
     ]
     }
     */

    public String SCHANNELNAME="";
    public String TSTARTTIME="";
    public String TENDTIME="";
    public String SCURVALUE="";
    public String STYPE="";
}
