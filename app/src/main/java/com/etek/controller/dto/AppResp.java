<<<<<<< HEAD
package com.etek.controller.dto;


import java.util.List;

public class AppResp {
private int success;

private List<Cwxx> cwxx ;

public void setSuccess(int success){
this.success = success;
}
public int getSuccess(){
return this.success;
}
public void setCwxx(List<Cwxx> cwxx){
this.cwxx = cwxx;
}
public List<Cwxx> getCwxx(){
return this.cwxx;
}

    @Override
    public String toString() {
        return "AppResp{" +
                "success=" + success +
                ", cwxx=" +cwxx.toString()+
                '}';
    }
=======
package com.etek.controller.dto;


import java.util.List;

public class AppResp {
private int success;

private List<Cwxx> cwxx ;

public void setSuccess(int success){
this.success = success;
}
public int getSuccess(){
return this.success;
}
public void setCwxx(List<Cwxx> cwxx){
this.cwxx = cwxx;
}
public List<Cwxx> getCwxx(){
return this.cwxx;
}

    @Override
    public String toString() {
        return "AppResp{" +
                "success=" + success +
                ", cwxx=" +cwxx.toString()+
                '}';
    }
>>>>>>> 806c842... 雷管组网
}