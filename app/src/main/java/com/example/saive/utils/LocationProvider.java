package com.example.saive.utils;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LocationProvider {
    private static List<Province> provinces;

    public static class Province {
        public String name;
        public List<District> districts;
    }

    public static class District {
        public String name;
        public List<String> wards;
    }

    public static void init(Context context) {
        if (provinces != null) return;
        try {
            InputStream is = context.getAssets().open("data/vietnam_provinces.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONArray provinceArray = new JSONArray(json);
            provinces = new ArrayList<>();
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
                provinces.add(province);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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