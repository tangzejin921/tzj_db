import 'package:json_annotation/json_annotation.dart';
import 'package:tzj_db/base_db.dart';

part 'User.g.dart';

@JsonSerializable()
class User extends BaseDB{

  int age = 0;
  String name= "";
  bool isBoy = true;


  User();

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);

  Map<String, dynamic> toJson() => _$UserToJson(this);

  bool operator == (other){
    if(age == other.age &&
    name == other.name){
      return true;
    }
    return false;
  }

  @override
  String dbName() {
    return "User";
  }

  @override
  String tabName() {
    return "User";
  }

}

@JsonSerializable()
class Dog{
  String name;
  int age;

  Dog(this.name, this.age);

  factory Dog.fromJson(Map<String, dynamic> json) => _$DogFromJson(json);

  Map<String, dynamic> toJson() => _$DogToJson(this);

}

