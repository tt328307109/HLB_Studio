package com.lk.qf.pay.tool;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GSONUtils {
	private static final String TAG = GSONUtils.class.getSimpleName();  
	  
    public static Gson gson = new Gson();  
 
    public static <T> T parseJson(Class<T> cls, String json) {  
         try {  
              return gson.fromJson(json, cls);  
         } catch(JsonSyntaxException e) {  
              Logger.e(TAG, e.getMessage());  
         }  
 
         return null;  
    }  
 
    public static String toJson(Object src) {  
         try {  
              return gson.toJson(src);  
         } catch(JsonSyntaxException e) {  
        	 Logger.e(TAG, e.getMessage());  
         }  
 
         return null;  
    }  
}
