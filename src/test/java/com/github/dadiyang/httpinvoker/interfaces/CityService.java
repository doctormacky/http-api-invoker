package com.github.dadiyang.httpinvoker.interfaces;


import com.github.dadiyang.httpinvoker.annotation.*;
import com.github.dadiyang.httpinvoker.entity.City;
import com.github.dadiyang.httpinvoker.entity.ResultBean;

import java.util.List;
import java.util.Map;

/**
 * a example interface for testing
 *
 * @author huangxuyang
 * date 2018/11/1
 */
@HttpApi(prefix = "${api.url.city.host}/city")
public interface CityService {
    /**
     * 使用URI，会自动添加prefix指定的前缀
     */
    @HttpReq("/allCities")
    List<City> getAllCities();

    /**
     * 使用Param注解指定方法参数对应的请求参数名称
     */
    @HttpReq("/getById")
    City getCity(@Param("id") int id);

    /**
     * 如果是集合类或数组的参数数据会直接当成请求体直接发送
     */
    @HttpReq(value = "/save", method = "POST")
    boolean saveCities(List<City> cities);

    /**
     * 默认使用GET方法，可以通过method指定请求方式
     */
    @HttpReq(value = "/saveCity", method = "POST")
    boolean saveCity(@Param("id") Integer id, @Param("name") String name);

    /**
     * 使用完整的路径，不会添加前缀
     */
    @HttpReq(value = "${api.url.city.host}/city/getCityByName")
    ResultBean<City> getCityByName(@Param("name") String name);

    /**
     * 支持路径参数
     */
    @HttpReq("/getCityRest/{id}")
    City getCityRest(@Param("id") int id);

    /**
     * 带错误请求头的方法
     */
    @HttpReq("/getCityRest/{id}")
    City getCityWithErrHeaders(@Param("id") int id, @Headers String headers);

    /**
     * 带正确请求头的方法
     */
    @HttpReq("/getCityRest/{id}")
    City getCityWithHeaders(@Param("id") int id, @Headers Map<String, String> headers);

    /**
     * 带cookie的方法
     */
    @HttpReq("/getCityRest/{id}")
    City getCityWithCookies(@Param("id") int id, @Cookies Map<String, String> cookies);

}
