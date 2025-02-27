package com.example.moattravel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.moattravel.entity.House;
import com.example.moattravel.form.HouseRegisterForm;
import com.example.moattravel.repository.HouseRepository;

@Service//コントローラとリポジトリの間をつなぐ（間に入って処理を分担する、データ処理を専門に担当するクラス）
public class HouseService {
	private final HouseRepository houseRepository;
	
	public HouseService(HouseRepository houseRepository) {
		this.houseRepository = houseRepository;
	}
	
	//データベースの操作をひとまとまりにする。データベース操作が完全に成功するか、全て失敗かをはっきりさせられる
	//例：お金を振り込むときに、Aさんの口座から1000円引く→Bさんの口座に1000円足す→途中でエラーが発生！（Bさんの口座が見つからないなど）
	//　　このときAさんのお金だけが減り、Bさんには届かなくなりデータがおかしくなる
	//@Transactional　を使うことで、全部成功するか全部失敗かになるため、データの生合成を保つことができる
	@Transactional
	public void create(HouseRegisterForm houseRegisterForm) {
		House house = new House();//新しいhouseオブジェクトを生成
		MultipartFile imageFile = houseRegisterForm.getImageFile();//画像ファイルを取得する
		
		if (!imageFile.isEmpty()) {//画像ファイルがある場合のみ処理する
			String imageName = imageFile.getOriginalFilename();//元のファイル名を取得
			String hashedImageName = generateNewFileName(imageName);//元のファイル名をUUIDで重複しないファイル名に変更
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);//保存するパスを作成
			copyImageFile(imageFile, filePath);//画像をコピーする
			house.setImageName(hashedImageName);//house　にファイル名をセット
			
		}
		
		//Formからデータを取り出してhouseにセットする（この時点ではデータベースに保存されていない）
		house.setName(houseRegisterForm.getName());
		house.setDescription(houseRegisterForm.getDescription());
		house.setPrice(houseRegisterForm.getPrice());
		house.setCapacity(houseRegisterForm.getCapacity());
		house.setPostalCode(houseRegisterForm.getPostalCode());
		house.setAddress(houseRegisterForm.getAddress());
		house.setPhoneNumber(houseRegisterForm.getPhoneNumber());
		
		//データベースに保存（@Transactionalでエラーが発生したらロールバックされる）
		houseRepository.save(house);
	}
	
	// UUIDを使って生成したファイル名を返す
	public String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");//拡張子で分割(.jpgなど)
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();//ファイル名の重複を防ぐため、UUIDで重複しないファイル名に変更している（重複した場合上書きされて、使っていたファイルが変更されてしまう）
		}
		String hashedFileName = String.join(".", fileNames);//拡張子を結合する
		return hashedFileName;
	}
	
	//画像ファイルを指定したファイルをコピーする
	public void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath);//画像を指定したパスにコピー
		} catch (IOException e) {
			e.printStackTrace();//エラーが出た場合ログを表示
		}
	}
}
