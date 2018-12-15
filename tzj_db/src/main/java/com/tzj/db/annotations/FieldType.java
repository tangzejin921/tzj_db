package com.tzj.db.annotations;

/**
 * 标记类型
 */
public enum FieldType {
	INT,
	BOOLEAN,
//	chars,//定长 很少用吧
	VARCHAR,//变长 网上说最大255
	TEXT,
	DATETIME,
	ignore
}
