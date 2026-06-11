package com.example.saive.utils;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LocationProvider {
    private static List<Province> provinces;
    private static boolean isLoading = false;

    public interface LocationLoadListener {
        void onLoaded();
    }

    public static class Province {
        public String name;
        public List<District> districts;
    }

    public static class District {
        public String name;
        public List<String> wards;
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
                InputStream is = context.getAssets().open("data/vietnam_provinces.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");
                JSONArray provinceArray = new JSONArray(json);
                List<Province> tempList = new ArrayList<>();
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject pObj = provinceArray.getJSONObject(i);
                    Province province = new Province();
                    province.name = pObj.getString("name");
                    province.districts = new ArrayList<>();
                    JSONArray districtArray = pObj.getJSONArray("districts");
                    for (int j = 0; j < districtArray.length(); j++) {
                        JSONObject dObj = districtArray.getJSONObject(j);
                        District district = new District();
                        district.name = dObj.getString("name");
                        district.wards = new ArrayList<>();
                        JSONArray wardArray = dObj.getJSONArray("wards");
                        for (int k = 0; k < wardArray.length(); k++) {
                            district.wards.add(wardArray.getString(k));
                        }
                        province.districts.add(district);
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
        init(context);
        List<String> list = new ArrayList<>();
        if (provinces != null) {
            for (Province p : provinces) list.add(p.name);
        }
        return list;
    }

    public static List<String> getDistricts(Context context, String provinceName) {
        init(context);
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
        init(context);
        List<String> list = new ArrayList<>();
        if (provinces != null) {
            for (Province p : provinces) {
                if (p.name.equals(provinceName)) {
                    for (District d : p.districts) {
                        if (d.name.equals(districtName)) {
                            list.addAll(d.wards);
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