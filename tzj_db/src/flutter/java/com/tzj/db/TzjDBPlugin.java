package com.tzj.db;

import android.app.Activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

/**
 * TzjDBPlugin
 */
public class TzjDBPlugin implements MethodChannel.MethodCallHandler {
    private MethodChannel channel;//1.channel 可以 new 多个吗？ 2. 这里循环引用了好不
    private Map<String,FlutterDB> map = new HashMap<>();

    /**
     * Plugin registration.
     */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        SQLiteDelegate.init(registrar.activeContext().getApplicationContext());
        final MethodChannel channel = new MethodChannel(registrar.messenger(), TzjDBPlugin.class.getSimpleName());
        channel.setMethodCallHandler(new TzjDBPlugin(registrar.activity(),channel));
    }

    public TzjDBPlugin(Activity mActivity,MethodChannel channel) {
        this.channel = channel;
    }


    @Override
    public void onMethodCall(MethodCall methodCall, final MethodChannel.Result result) {
        try {
            DBInfo dbInfo = new DBInfo(methodCall);

            FlutterDB flutterDB  =  map.get(dbInfo.getKey());
            if (flutterDB!=null){
                flutterDB.setDbInfo(dbInfo);
            }
            switch (methodCall.method) {
                case "init"://返回
                    flutterDB = new FlutterDB(dbInfo,channel);
                    map.put(dbInfo.getKey(),flutterDB);
                    break;
                case "insert":
                    flutterDB.where()
                            .insert(new ICallBack() {
                                @Override
                                public void result(List list, long idRoNum) {
                                    result.success(idRoNum);
                                }

                                @Override
                                public void err(Exception e, long code) {
                                    result.error("-1", e.getMessage() + "", e);
                                }
                            });
                    break;
                case "delete":
                    flutterDB.where()
                            .delete(new ICallBack() {
                                @Override
                                public void result(List list, long idRoNum) {
                                    result.success(idRoNum);
                                }

                                @Override
                                public void err(Exception e, long code) {
                                    result.error("-1", e.getMessage() + "", e);
                                }
                            });
                    break;
                case "update":
                    flutterDB.where()
                            .update(new ICallBack() {
                                @Override
                                public void result(List list, long idRoNum) {
                                    result.success(idRoNum);
                                }

                                @Override
                                public void err(Exception e, long code) {
                                    result.error("-1", e.getMessage() + "", e);
                                }
                            });
                    break;
                case "select":
                    flutterDB.where()
                            .select(new ICallBack<FlutterDB>() {
                                @Override
                                public void result(List<FlutterDB> list, long idRoNum) {
                                    Map<String,Object> temp = new HashMap<>();
                                    if (list.size() > 0){
                                        Map<String, Object> info = list.get(0).getDbInfo().getInfo();
                                        temp.putAll(info);
                                        List<Map<String,Object>> fields = new ArrayList<>();
                                        for (FlutterDB flutterDB:list) {
                                            fields.add(flutterDB.getDbInfo().getFields());
                                        }
                                        temp.put("fields",fields);
                                    }
                                    result.success(temp);
                                }

                                @Override
                                public void err(Exception e, long code) {
                                    result.error("-1", e.getMessage() + "", e);
                                }
                            });
                    break;
                case "count":
                    flutterDB.where()
                            .count(new ICallBack() {
                                @Override
                                public void result(List list, long count) {
                                    result.success(count);
                                }

                                @Override
                                public void err(Exception e, long code) {
                                    result.error("-1", e.getMessage() + "", e);
                                }
                            });
                    break;
                case "close":
                    if (flutterDB != null){
                        map.remove(dbInfo.getKey());
                        flutterDB.close();
                        result.success(null);
                    }
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        } catch (Exception e) {
            result.error("-1", e.getMessage() + "", e);
        }
    }
}
