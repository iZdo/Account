package com.izdo.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by iZdo on 2017/10/27.
 */

public class User extends BmobUser {

    private BmobFile pic;

    private BmobFile data;

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public BmobFile getData() {
        return data;
    }

    public void setData(BmobFile data) {
        this.data = data;
    }
}
