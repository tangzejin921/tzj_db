// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'User.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

User _$UserFromJson(Map<String, dynamic> json) {
  return User()
    ..age = json['age'] as int
    ..name = json['name'] as String
    ..isBoy = json['isBoy'] as bool;
}

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
      'age': instance.age,
      'name': instance.name,
      'isBoy': instance.isBoy
    };

Dog _$DogFromJson(Map<String, dynamic> json) {
  return Dog(json['name'] as String, json['age'] as int);
}

Map<String, dynamic> _$DogToJson(Dog instance) =>
    <String, dynamic>{'name': instance.name, 'age': instance.age};
