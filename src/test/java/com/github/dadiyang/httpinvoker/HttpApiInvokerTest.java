package com.github.dadiyang.httpinvoker;

import com.alibaba.fastjson.JSON;
import com.github.dadiyang.httpinvoker.entity.City;
import com.github.dadiyang.httpinvoker.entity.ResultBean;
import com.github.dadiyang.httpinvoker.interfaces.CityService;
import com.github.dadiyang.httpinvoker.requestor.HttpRequest;
import com.github.dadiyang.httpinvoker.requestor.HttpResponse;
import com.github.dadiyang.httpinvoker.requestor.Requestor;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.github.dadiyang.httpinvoker.util.CityUtil.createCities;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * HttpApiInvoker 完整单元测试
 *
 * @author huangxuyang
 * date 2018/11/27
 */
public class HttpApiInvokerTest {
    private Requestor requestor;
    private CityService service;
    private String host;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(HttpApiInvokerTest.class.getClassLoader().getResourceAsStream("conf.properties"));
        host = properties.get("api.url.city.host").toString();
        System.out.println("Mock requestor");
        requestor = mock(Requestor.class);
        service = new HttpApiProxyFactory(requestor, properties).getProxy(CityService.class);
    }

    @Test
    public void saveCitiesTest() throws Exception {
        System.out.println("————————————开始测试批量保存城市（集合类或数组的参数数）————————————");
        String url = host + "/city/save";
        List<City> citiesToSave = Arrays.asList(new City(22, "南京"), new City(23, "武汉"));
        HttpRequest request = new HttpRequest(url);
        request.setMethod("POST");
        request.setBody(citiesToSave);
        when(requestor.sendRequest(request)).thenReturn(createResponse("true"));
        assertTrue(service.saveCities(citiesToSave));
        System.out.println("————————————测试批量保存城市通过（集合类或数组的参数数）————————————");
    }

    @Test
    public void getAllCitiesTest() throws Exception {
        System.out.println("————————————开始测试获取全部城市（使用URI）————————————");
        List<City> cityList = createCities();
        String url = host + "/city/allCities";
        HttpRequest request = new HttpRequest(url);
        when(requestor.sendRequest(request)).thenReturn(createResponse(JSON.toJSONString(cityList)));
        List<City> cities = service.getAllCities();
        assertTrue(cityList.containsAll(cities));
        assertTrue(cities.containsAll(cityList));
        assertEquals(cities.size(), cityList.size());
        for (City city : cities) {
            System.out.println(city);
        }
        System.out.println("————————————测试获取全部城市通过（使用URI）————————————");
    }


    @Test
    public void saveCityTest() throws Exception {
        System.out.println("————————————开始测试保存单个城市（通过method指定POST）————————————");
        String url = host + "/city/saveCity";
        Map<String, Object> param = new HashMap<>();
        param.put("id", 31);
        param.put("name", "东莞");
        HttpRequest request = new HttpRequest(url);
        request.setMethod("POST");
        request.setData(param);
        when(requestor.sendRequest(request)).thenReturn(createResponse("true"));
        assertTrue(service.saveCity(31, "东莞"));
        System.out.println("————————————测试保存单个城市通过（通过method指定POST）————————————");
    }

    @Test
    public void getCityTest() throws Exception {
        System.out.println("————————————开始测试获取单个城市（使用Param注解指定方法参数）————————————");
        String url = host + "/city/getById";
        City city = new City(31, "东莞");
        Map<String, Object> param = new HashMap<>();
        param.put("id", 31);
        HttpRequest request = new HttpRequest(url);
        request.setData(param);
        when(requestor.sendRequest(request)).thenReturn(createResponse(JSON.toJSONString(city)));
        assertEquals(service.getCity(31), city);
        System.out.println("————————————测试保存单个城市通过（使用Param注解指定方法参数）————————————");
    }

    @Test
    public void getCityByNameTest() throws Exception {
        System.out.println("————————————开始测试根据城市名查询城市（使用完整的路径）————————————");
        String url = host + "/city/getCityByName";
        String name = "北京";
        ResultBean<City> expectedResult = new ResultBean<>(0, new City(1, name));
        Map<String, Object> param = new HashMap<>();
        param.put("name", "北京");
        HttpRequest request = new HttpRequest(url);
        request.setData(param);
        when(requestor.sendRequest(request)).thenReturn(createResponse(JSON.toJSONString(expectedResult)));
        ResultBean<City> invokedResult = service.getCityByName(name);
        assertEquals(expectedResult, invokedResult);
        City city = expectedResult.getData();
        assertEquals(city, invokedResult.getData());
        System.out.println("————————————测试根据城市名查询城市通过（使用完整的路径）————————————");
    }

    @Test
    public void getCityRest() throws Exception {
        System.out.println("————————————开始测试带有路径参数的方法————————————");
        int id = 1;
        String url = host + "/city/getCityRest/" + id;
        City city = new City(id, "北京");
        Map<String, Object> param = new HashMap<>();
        HttpRequest request = new HttpRequest(url);
        request.setData(param);
        when(requestor.sendRequest(request)).thenReturn(createResponse(JSON.toJSONString(city)));
        assertEquals(city, service.getCityRest(id));
        System.out.println("————————————测试带有路径参数的方法通过————————————");
    }

    @Test
    public void getCityWithHeadersTest() {
        Map<String, String> map = new HashMap<>();
        map.put("auth", "OK");
        service.getCityWithHeaders(1, map);
    }

    @Test
    public void getCityWithErrHeadersTest() {
        try {
            service.getCityWithErrHeaders(1, "");
            fail("this method must throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void setRequestor(Requestor requestor) {
        this.requestor = requestor;
    }

    public void setService(CityService service) {
        this.service = service;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private HttpResponse createResponse(String body) {
        HttpResponse response = new HttpResponse(200, "OK", "application/json");
        response.setCharset("UTF-8");
        response.setBody(body);
        response.setBodyAsBytes(body.getBytes());
        return response;
    }

}