import 'dart:async';

import 'package:flutter/services.dart';

import 'base_db.dart';


class TzjDBPlugin {
  static MethodChannel _channel;
  final Map<BaseDB, dynamic> _map = <BaseDB, dynamic>{};

  Future<dynamic> _handleTzjDbInvocation(MethodCall methodCall) {
    switch (methodCall.method) {
      case "onUpgrade":
        var map = methodCall.arguments as Map<String, dynamic>;
        var dbPath = map["dbPath"];
        var dbName = map["dbName"];
        _map.forEach((BaseDB baseDB, dynamic value) {
          if (dbPath == baseDB.dbPath() && dbName == baseDB.dbName()) {
            return baseDB.onUpgrade(map["oldVersion"], map["newVersion"]);
          }
        });
        break;
      default:
        throw MissingPluginException();
        break;
    }
  }

  final BaseDB _baseDB;


  TzjDBPlugin(this._baseDB) {
    if (_channel == null) {
      _channel = const MethodChannel('TzjDBPlugin');
      _channel.setMethodCallHandler(_handleTzjDbInvocation);
    }
    _map.putIfAbsent(_baseDB, () => _baseDB);
    _init();
  }

  void removeHandlerCallBack() {
    _map.remove(_baseDB);
  }

  void clearAll() {
    _map.clear();
  }

  /// 调用平台，创建/打开数据库，如果需要更新则会同过
  Future<Null> _init() async {
    return await _channel.invokeMethod('init', _baseDB.dbInfo());
  }

  Future<int> insert() async {
    return await _channel.invokeMethod('insert', _baseDB.dbInfo());
  }

  Future<int> delete() async {
    return await _channel.invokeMethod('delete', _baseDB.dbInfo());
  }

  Future<int> update() async {
    return await _channel.invokeMethod('update', _baseDB.dbInfo());
  }

  Future<List<dynamic>> select() async {
    return await _channel.invokeMethod('select', _baseDB.dbInfo())
        .then((map) {
      return map['fields'] as List;
    });
  }

  Future<int> count() async {
    return await _channel.invokeMethod('count', _baseDB.dbInfo());
  }

  Future<Null> close() async {
    return await _channel.invokeMethod('close', _baseDB.dbInfo());
  }
}
