package com.example.saive.utils;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LocationProvider {
    private static final String API_BASE_URL = "https://provinces.open-api.vn/api/";
    private static List<Province> provinces;
    private static boolean isLoading = false;

    public interface LocationLoadListener {
        void onLoaded();
    }

    public static class Province {
        public int code;
        public String name;
        public List<District> districts;
    }

    public static class District {
        public int code;
        public String name;
        public List<Ward> wards;
    }

    public static class Ward {
        public int code;
        public String name;
    }

    public static void init(Context context) {
        init(context, null);
    }

    public static synchronized void init(Context context, LocationLoadListener listener) {
        if (provinces != null) {
            if (listener != null) listener.onLoaded();
            return;
        }
        if (isLoading) return;
        
        isLoading = true;
        new Thread(() -> {
            try {
                // Tải danh sách tỉnh/thành phố kèm theo quận/huyện và phường/xã (depth=3)
                java.net.URL url = new java.net.URL(API_BASE_URL + "?depth=3");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                InputStream is = conn.getInputStream();
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String json = s.hasNext() ? s.next() : "";
                is.close();

                JSONArray provinceArray = new JSONArray(json);
                List<Province> tempList = new ArrayList<>();
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject pObj = provinceArray.getJSONObject(i);
                    Province province = new Province();
                    province.name = pObj.getString("name");
                    province.code = pObj.getInt("code");
                    province.districts = new ArrayList<>();
                    
                    if (pObj.has("districts")) {
                        JSONArray districtArray = pObj.getJSONArray("districts");
                        for (int j = 0; j < districtArray.length(); j++) {
                            JSONObject dObj = districtArray.getJSONObject(j);
                            District district = new District();
                            district.name = dObj.getString("name");
                            district.code = dObj.getInt("code");
                            district.wards = new ArrayList<>();
                            
                            if (dObj.has("wards")) {
                                JSONArray wardArray = dObj.getJSONArray("wards");
                                for (int k = 0; k < wardArray.length(); k++) {
                                    JSONObject wObj = wardArray.getJSONObject(k);
                                    Ward ward = new Ward();
                                    ward.name = wObj.getString("name");
                                    ward.code = wObj.getInt("code");
                                    district.wards.add(ward);
                                }
                            }
                            province.districts.add(district);
                        }
                    }
                    tempList.add(province);
                }
                provinces = tempList;
                if (listener != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(listener::onLoaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isLoading = false;
            }
        }).start();
    }

    public static boolean isLoaded() {
        return provinces != null;
    }

    public static List<String> getProvinces(Context context) {
        if (provinces == null) {
            init(context);
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (Province p : provinces) list.add(p.name);
        return list;
    }

    public static List<String> getDistricts(Context context, String provinceName) {
        List<String> list = new ArrayList<>();
        if (provinces != null) {
            for (Province p : provinces) {
                if (p.name.equals(provinceName)) {
                    for (District d : p.districts) list.add(d.name);
                    break;
                }
            }
        }
        return list;
    }

    public static List<String> getWards(Context context, String provinceName, String districtName) {
        List<String> list = new ArrayList<>();
        if (provinces != null) {
            for (Province p : provinces) {
                if (p.name.equals(provinceName)) {
                    for (District d : p.districts) {
                        if (d.name.equals(districtName)) {
                            for (Ward w : d.wards) list.add(w.name);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return list;
    }
}