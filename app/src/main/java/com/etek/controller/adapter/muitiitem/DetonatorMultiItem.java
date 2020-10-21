package com.etek.controller.adapter.muitiitem;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.etek.controller.adapter.DetControllerAdapter;
import com.etek.controller.entity.Detonator;

import java.util.Date;


public class DetonatorMultiItem  implements MultiItemEntity {
    private   int num; // 指数
    private byte[] ids;          //芯片内部ID
    private String chipID;      //芯片内部ID
    private byte[] source;      //雷管原始上传数据
    private String uid;         //雷管码 uid
    private String detCode;     //雷管发编号 fbh
    private int relay;          //雷管起爆延时时间 relay
    private Date time;           //雷管有效期 yxq
    private boolean isValid;    //是否有效
    private byte[] acCode;      //雷管工作码 gzm
    private int status;         //状态码
    private byte[] extId;       //额外ID
    private String  zbDetCode;  // 中爆管码
    private int type;           // 类型 0 ranyi new 1

    private String statusName;           // 类型 0 ranyi new 1

    public DetonatorMultiItem(Detonator detonator) {
        this.detCode = detonator.getDetCode();
        this.uid = detonator.getUid();
        this.chipID = detonator.getChipID();
        this.num = detonator.getNum();
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public byte[] getIds() {
        return ids;
    }

    public void setIds(byte[] ids) {
        this.ids = ids;
    }

    public String getChipID() {
        return chipID;
    }

    public void setChipID(String chipID) {
        this.chipID = chipID;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDetCode() {
        return detCode;
    }

    public void setDetCode(String detCode) {
        this.detCode = detCode;
    }

    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public byte[] getAcCode() {
        return acCode;
    }

    public void setAcCode(byte[] acCode) {
        this.acCode = acCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getExtId() {
        return extId;
    }

    public void setExtId(byte[] extId) {
        this.extId = extId;
    }

    public String getZbDetCode() {
        return zbDetCode;
    }

    public void setZbDetCode(String zbDetCode) {
        this.zbDetCode = zbDetCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public int getItemType() {
        return DetControllerAdapter.TYPE_DETONATOR;
    }
}
