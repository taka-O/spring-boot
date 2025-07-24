package com.example.demo.repository;

import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;

/**
 * DBSetupを使用する際にDBと接続するために必要なクラス
 * 接続に必要なDestinationオブジェクトを作成
 *
 */
public class AccessConfig {
	
	/** 接続に必要なDestinationオブジェクトを作成 **/
	public static final Destination dest = new DriverManagerDestination(
			"jdbc:mysql://localhost:3306/test", 
			"root", 
			"secret"
		);

}