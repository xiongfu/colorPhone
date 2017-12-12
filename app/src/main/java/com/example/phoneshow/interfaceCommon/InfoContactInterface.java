package com.example.phoneshow.interfaceCommon;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public interface InfoContactInterface {
    /**
     * 查询联系人数据库将数据给拦截界面
     * @param name
     * @param bytes 头像
     */
    void setInfoContact(String name, byte[] bytes);
}
