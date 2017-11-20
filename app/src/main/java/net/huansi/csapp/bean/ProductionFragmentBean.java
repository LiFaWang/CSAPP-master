package net.huansi.csapp.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/11/7.
 {
 "SORDERNO": "R001",
 "SCUSTNAME": "客户A",
 "SPRODUCTNAME": "产品B",
 "NQTY": "600.00",
 "STERMINALNAME": "蒸化01",
 "TSTARTTIME": "2017/11/7 9:50:00",
 "TENDTIME": "2017/11/7 11:50:00"
 }

 */

public class ProductionFragmentBean extends WsData {
    public String SORDERNO="";//订单号
    public String SCUSTNAME="";//客户
    public String SPRODUCTNAME="";//产品
    public String NQTY="";//生产数量
    public String STERMINALNAME="";//当前机台
    public String TSTARTTIME="";//开始时间
    public String TENDTIME="";//结束时间
}
