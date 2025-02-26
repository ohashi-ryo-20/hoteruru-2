package com.example.moattravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moattravel.entity.House;

//JpaRepositoryの機能（一部を記載）
//save()　エンティティを保存または更新するときに使用。新規はINSERT操作、既存ならばUPDATE操作になる
//findById()　指定されたIDのエンティティを取得する
//getReferenceById(id)　指定したidのエンティティを取得する
//delete()　指定されたエンティティを削除する
//findAll()　テーブル内の全てのレコードをリストとして返す
//クエリメソッド　メソッドによって自動的にSQLクエリを生成する（SQLを書かずにデータベースを操作することができる）
//findByName()　sprinJPAが自動的に　SELECT * FROM users WHERE name = ?　を生成する
//findAll(Pageable pageable)　ページネーションを行い、指定されたページのデータを返す
public interface HouseRepository extends JpaRepository<House, Integer> {//ジェネリクス(<>)の中身には<エンティティのクラス型,主キーのデータ型>を指定する

}
