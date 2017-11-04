package net.huansi.csapp.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 *
 * Created by Tony on 2017/11/2.
 *     "ITERMINAL": "1401",
        "STERMINALNAME": "水洗01",
        "NQTY": "500.00",
         "SOPTION": "张三",
        "TPLANDATE": "2017/11/1 0:00:00",
        "TSTARTTIME": "2017/11/2 9:00:00",
        "TENDTIME": "2017/11/2 11:00:00"
 */

public class ProductionBean extends WsData {
    public String ITERMINAL="";
    public String STERMINALNAME="";
    public String NQTY="";
    public String SOPTION="";
    public String TPLANDATE="";
    public String TSTARTTIME="";

    @Override
    public String toString() {
        return "ProductionBean{" +
                "ITERMINAL='" + ITERMINAL + '\'' +
                ", STERMINALNAME='" + STERMINALNAME + '\'' +
                ", NQTY='" + NQTY + '\'' +
                ", SOPTION='" + SOPTION + '\'' +
                ", TPLANDATE='" + TPLANDATE + '\'' +
                ", TSTARTTIME='" + TSTARTTIME + '\'' +
                ", TENDTIME='" + TENDTIME + '\'' +
                '}';
    }

    public String TENDTIME="";

}
