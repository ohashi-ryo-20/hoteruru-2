package com.example.moattravel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.moattravel.entity.House;
import com.example.moattravel.repository.HouseRepository;

@Controller
@RequestMapping("/admin/houses")//ルートパスの基準値を設定する（他の場所で@GetMapping  などを使った場合に指定したルートパス（今回は　/admin/houses）を省略できるようになる）
public class AdminHouseController {
	private final HouseRepository houseRepository;//DIするための準備
	
	//@Autowired　はコンストラクタが１つしか存在しない場合、省略できる
	public AdminHouseController(HouseRepository houseRepository) {//コンストラクタでDIを行う（コンストラクタインジェクション）
		this.houseRepository = houseRepository;
	}
	
	@GetMapping
	//@PageableDefault を使用することでデフォルト値を設定することができる page：ページ番号（デフォルトは0）size：サイズ（1ページあたりの表示数、デフォルトは10）sort：並べ替える対象（デフォルトはなし）direction：並べ替える順番（デフォルトはDirection.ASC）
	public String index(Model model,@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
		List<House> houses = houseRepository.findAll();//findAll()　でhouses に民宿データを全て取得
		Page<House> housePage = houseRepository.findAll(pageable);//ページネーションを作成していく デフォルトでページ番号0,サイズ20,並び替えの条件は指定なしが設定されている
		
		model.addAttribute("houses", houses);//Model　にhouses を格納する
		model.addAttribute("housePage", housePage);//Model　にhousePage を格納する
		
		return "admin/houses/index";//  admin/houses/index.htmlファイルに渡す
	}
}
