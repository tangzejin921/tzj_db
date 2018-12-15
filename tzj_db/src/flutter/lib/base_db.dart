import 'tzj_db_plugin.dart';
///请字段都给个初始值
///dateTime 要取当前时间，传过去会比较当前时间 <= 一定时间表示为 datetime 类型
abstract class BaseDB extends BaseReflex {
  TzjDBPlugin _tzjDBPlugin;

  BaseDB() {
    _tzjDBPlugin = TzjDBPlugin(this);
  }

  Future<int> insert() async {
    return _tzjDBPlugin.insert();
  }

  Future<int> delete() async {
    return _tzjDBPlugin.delete();
  }

  Future<int> update() async {
    return _tzjDBPlugin.update();
  }

  Future<List<dynamic>> select() async {
    return _tzjDBPlugin.select();
  }

  Future<int> count() async {
    return _tzjDBPlugin.count();
  }

  String onUpgrade(int oldVersion, int newVersion) {

  }

  void close() {
    _tzjDBPlugin.close();
  }
}


abstract class BaseReflex {
  String _where;
  List<dynamic> _values;

  String _orderBy;
  bool _desc;
  int _limit;

  void where(String where, {List<dynamic> values}) {
    _where = where;
    _values = values;
  }

  void orderBy(String orderBy) {
    _orderBy = orderBy;
  }

  void desc(bool desc) {
    _desc = desc;
  }

  void limit(int limit) {
    _limit = limit;
  }


  int version() {
    return 1;
  }

  String dbPath() {
    return "";
  }

  String dbName();

  String tabName();

  Map<String, dynamic> dbInfo() {
    dynamic thiz = this;
    var temp = {
      "dbPath": dbPath(),
      "dbName": dbName(),
      "tabName": tabName(),
      "version": version(),
      "fields": thiz.toJson(),//请实现toJson方法
      "where": _where,
      "values": _values,
      "orderBy": _orderBy,
      "desc": _desc,
      "limit": _limit,
    };
    _where = null;
    _values = null;
    _orderBy = null;
    _desc = null;
    _limit = null;
    return temp;
  }
}
