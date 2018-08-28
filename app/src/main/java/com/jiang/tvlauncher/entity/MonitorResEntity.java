package com.jiang.tvlauncher.entity;

/**
 * Created by wwwfa on 2017/8/9.
 */

public class MonitorResEntity extends BaseEntity {

    /**
     * errorcode : 1000
     * result : {"bussFlag":0}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
       int bussFlag = 0;

        public int getBussFlag() {
            return bussFlag;
        }

        public void setBussFlag(int bussFlag){
            this.bussFlag = bussFlag;
        }
    }
}
