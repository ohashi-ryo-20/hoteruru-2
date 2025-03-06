package com.example.moattravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	//OrderBy 並び替えができるDesc（降順）Asc（昇順）
	public Page<House> findByNameLike(String keyword, Pageable pageable);//公式リファレンスに記載されているキーワードを使った独自のメソッドを追加することができる
	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
	public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);//LessThanは　< になる
	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);
	public List<House> findTop10ByOrderByCreatedAtDesc(); //Topを使用することでSQLのLIMITと同様に取得するデータを制限できる
}
