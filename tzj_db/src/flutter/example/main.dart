import 'package:flutter/material.dart';
import 'package:flutter_plugin_example/User.dart';

void main() {
  runApp(new MainPage());
}

class MainPage extends StatefulWidget {
  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  User user;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: new Scaffold(
        appBar: AppBar(
          title: const Text("DB_Test"),
        ),
        body: ListView(
            padding: const EdgeInsets.all(20.0),
            shrinkWrap: true,
            scrollDirection: Axis.horizontal,
            children: <Widget>[
              RaisedButton(onPressed: _newDB, child: const Text("new Db")),
              RaisedButton(onPressed: _insert, child: const Text("insert")),
              RaisedButton(onPressed: _delete, child: const Text("delete")),
              RaisedButton(onPressed: _select, child: const Text("select")),
              RaisedButton(onPressed: _update, child: const Text("update")),
              RaisedButton(onPressed: _count, child: const Text("count")),
              RaisedButton(onPressed: _close, child: const Text("close")),
            ]),
      ),
    );
  }

  void _newDB() {
    user = new User();
  }

  void _insert() {
    user.age++;
    user.insert();
  }

  void _update() {
    user.age = 0;
    user..update()
        .then((id)=>print(id));
  }

  void _delete() {
    user..where("age=0")..delete()
        .then((id)=>print(id));
  }

  void _select() {
    user.select()
        .then((list)=>print(list));
  }
  void _count() {
    user.count()
        .then((count)=>print(count));
  }
  void _close() {
    user.close();
  }


}
