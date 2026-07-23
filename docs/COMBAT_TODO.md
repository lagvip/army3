# Việc chiến đấu tạm hoãn

## Damage theo khoảng cách nổ của từng súng

Trạng thái: **tạm hoãn cho tới khi hoàn thiện danh sách súng**.

Khi làm lại, server phải là nơi duy nhất tính kết quả:

- Mỗi ID súng có bán kính nổ, đường cong giảm damage và damage tối thiểu riêng.
- Trúng trực tiếp hoặc nổ ngay dưới chân nhận 100% damage gốc.
- Nổ càng xa hitbox mục tiêu thì damage giảm dần; ra ngoài bán kính thì bằng 0.
- Điểm dùng để đo là điểm va chạm/nổ do server tính, không nhận điểm trúng hay damage từ client.
- Cần quy định riêng việc tường che, độ dày địa hình và các súng xuyên người/xuyên map.
- Dùng chung một công thức cho PvP, luyện tập và boss; client chỉ hiển thị.
- Phải kiểm thử: trúng trực tiếp, sát cạnh, rìa bán kính, ngoài bán kính, sau tường, nhiều viên và nhiều mục tiêu.

Chỉ bắt đầu cân bằng các hệ số sau khi mapping `ID súng -> loại đạn -> cơ chế va chạm` đã hoàn chỉnh.
