package com.example.moattravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.entity.House;
import com.example.moattravel.form.HouseEditForm;
import com.example.moattravel.form.HouseRegisterForm;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.service.HouseService;

@Controller
@RequestMapping("/admin/houses") //ルートパスの基準値を設定する（他の場所で@GetMapping  などを使った場合に指定したルートパス（今回は　/admin/houses）を省略できるようになる）
public class AdminHouseController {
	private final HouseRepository houseRepository;//DIするための準備
	private final HouseService houseService;

	//@Autowired　はコンストラクタが１つしか存在しない場合、省略できる
	public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {//コンストラクタでDIを行う（コンストラクタインジェクション）
		this.houseRepository = houseRepository;
		this.houseService = houseService;
	}

	@GetMapping
	//@PageableDefault を使用することでデフォルト値を設定することができる page：ページ番号（デフォルトは0）size：サイズ（1ページあたりの表示数、デフォルトは10）sort：並べ替える対象（デフォルトはなし）direction：並べ替える順番（デフォルトはDirection.ASC）
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			//@RequestParam　フォームから送信されたリクエストパラメータを引数に割り当てることができる
			//name属性：取得するリクエストパラメータ名、required属性：そのリクエストパラメータが必須かどうか（デフォルトでtrue）
			@RequestParam(name = "keyword", required = false) String keyword) {
		Page<House> housePage;
		
		if (keyword != null && !keyword.isEmpty()) {//isEmpty()　Stringは文字列が空(0)であればtrueになる、これだけで使うとnullの場合エラーになるので、nullチェックを一緒に使う必要がある(keyword != null)
			housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);//部分一致検索を行っている %は0文字以上の任意の文字
		} else {
			housePage = houseRepository.findAll(pageable);//検索をしていない場合は、全て取得する
		}

		model.addAttribute("housePage", housePage);//Model　にhousePage を格納する
		model.addAttribute("keyword", keyword);

		return "admin/houses/index";//  admin/houses/index.htmlファイルに渡す
	}
	
	@GetMapping("/{id}")//{id}にすることでidの値が入る
	public String show(@PathVariable(name = "id") Integer id, Model model) {//@PathVariable URLの一部を変数としてメソッドに渡す 例：/{id} が6の場合、Integer id に6が格納される
		House house = houseRepository.getReferenceById(id);//getReferenceById() まだデータベースにはアクセスせずに、Houseの擬似オブジェクトを生成し、house.getName()などのフィールドにアクセスした瞬間にデータを取得する（必要になるまでデータを取得しない）
		
		model.addAttribute("house", house);
		
		return "admin/houses/show";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("houseRegisterForm", new HouseRegisterForm());//デフォルトのオブジェクトを用意することで、nullにならず安全にデータを使える。フォームから送信されたデータを格納するためにインスタンス化をしている（インスタンス化しないとフォームとデータの紐付けができない）
		return "admin/houses/register";
	}
	
	@PostMapping("/create")
	//@ModelAttribute フォームから送られたリクエストパラメータを自動的に格納してくれる
	//@Validated フォームから送られてきたデータがHouseRegisterFormに設定されたバリデーションを参照してエラーがないかチェックする
	//BindingResult バリデーションで検証した結果が格納される
	public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {//検証でエラーが出ていた場合に処理される
			return "admin/houses/register";//入力フォームに返してもう一度入力し直すようにする
		}
		
		houseService.create(houseRegisterForm);//houseServiceのcreate()を呼び出して、データを登録する
		redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");//リダイレクト後に一度だけ表示するメッセージを設定
		
		return "redirect:/admin/houses";//ページを再読み込みしても同じデータが二重で登録されないようにする
	}
	
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		House house = houseRepository.getReferenceById(id);
		String imageName = house.getImageName();//更新前の民宿画像はファイル名を渡して表示するため、MultipartFile型のファイルを直接渡さなくて良い
		//どのデータを更新するかという情報が必要なため、フォームクラスに更新前のデータを格納し、インスタンス化する
		HouseEditForm houseEditForm = new HouseEditForm(house.getId(), house.getName(), null, house.getDescription(), house.getPrice(), house.getCapacity(), house.getPostalCode(), house.getAddress(), house.getPhoneNumber());
		
		model.addAttribute("imageName", imageName);
		model.addAttribute("houseEditForm", houseEditForm);
		
		return "admin/houses/edit";
	}
	
	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated HouseEditForm houseEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/houses/edit";
		}
		
		houseService.update(houseEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。");
		
		return "redirect:/admin/houses";
	}
	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		houseRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");
		
		return "redirect:/admin/houses";
	}
}
