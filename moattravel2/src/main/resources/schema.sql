CREATE TABLE IF NOT EXISTS houses ( --アプリを起動するたびに実行されるため、IF NOT EXISTS　で毎回生成されることを回避する
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	image_name VARCHAR(255),
	description VARCHAR(255) NOT NULL,
	price INT NOT NULL,
	capacity INT NOT NULL,
	postal_code VARCHAR(50) NOT NULL,
	address VARCHAR(50) NOT NULL,
	phone_number VARCHAR(50) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,--DEFAULT CURRENT_TIMESTAMP は、行が挿入されるときに、そのカラムに現在のタイムスタンプ（日時）が自動的にセットされるという設定です。
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP--ON UPDATE CURRENT_TIMESTAMP は、行が更新されるたびに、そのカラムの値を自動的に現在のタイムスタンプに更新する設定
);