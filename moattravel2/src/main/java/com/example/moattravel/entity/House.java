package com.example.moattravel.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity //このクラスはデータベースと対応するエンティティクラスとして扱われるようになる
@Table(name = "houses") //@Tableでテーブル名を指定することができる
@Data//lombokにより、getter,setterが自動的に追加される
public class House {
	@Id//@Id で主キーを指定することができる
	@GeneratedValue(strategy = GenerationType.IDENTITY)//主キーの値がデータベース側で自動的に生成される（AUTO_INCREMENT）
	@Column(name = "id")//データベースのカラムに対応させる,カラム名やその性質を変更することができる
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "image_name")//カラムの命名規則はスネークケース(image_name)で定義するのが一般的
	private String imageName;//フィールド名はローワーキャメルケース(imageName)で定義するのが一般的
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "capacity")
	private Integer capacity;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "created_at", insertable = false, updatable = false)//insertable はインサート時に設定するかを指定する。falseはカラムの値を設定しないことを意味する
	private Timestamp createdAt;										//trueにしている場合、値をアプリ側で管理するため、自分で指定しなければなくなる(falseはデータベース側に任せられる)
	
	@Column(name = "updated_at", insertable = false, updatable = false)//updatable はアップデート時に更新するか指定する。falseはカラムの値を更新しないことを意味する
	private Timestamp updatedAt;										//trueにしている場合、値をアプリ側で管理するため、自分で指定しなければなくなる(falseはデータベース側に任せられる)
}
