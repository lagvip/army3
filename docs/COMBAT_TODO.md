
## Mở rộng kiểm tra lệnh theo trạng thái sang toàn bộ chế độ

Trạng thái: **tạm hoãn**. Guard tổng quát tại router boss đã được gỡ vì chặn
nhầm packet tải/khởi tạo hợp lệ của client gốc. Các handler nhạy cảm vẫn tự
kiểm tra trạng thái; chưa mở rộng state machine chung sang PvP, luyện tập, RPG
và các chế độ sẽ bổ sung sau này.

Khi thực hiện:

- Xây dựng state machine chung cho phiên: đăng nhập, RPG, phòng chờ, đang bắt
  đầu trận, đang chiến đấu, đang hiện kết quả và đang thoát.
- Lập allowlist packet riêng cho từng trạng thái và từng chế độ; mặc định từ
  chối packet không được khai báo.
- Các lệnh chuyển scene, tạo/vào phòng khác, bắt đầu thêm trận hoặc gọi sai chế
  độ trong lúc đang chiến đấu phải được phân loại bằng allowlist đã bắt từ
  client gốc; không chặn theo danh sách suy đoán. Lệnh sai phải bị từ chối ở
  handler trước, chỉ ngắt kết nối khi đã phân biệt được packet trễ hợp lệ.
- Nút thoát hợp lệ và packet do client gốc thực sự cần trong animation/tải tài
  nguyên phải được giữ lại để tránh ngắt nhầm.
- Handler bên trong vẫn phải kiểm tra trạng thái lần hai, không chỉ dựa vào
  router packet.
- Server không thể phân biệt một packet giống hệt nhau được gửi từ UI hay từ
  client sửa đổi; quyết định chỉ dựa trên trạng thái server và nội dung packet.
- Cần kiểm thử ma trận cho PvP, luyện tập, toàn bộ boss và RPG: gọi lặp, gọi
  chéo chế độ, packet trễ sau chuyển cảnh, reconnect, thoát hợp lệ và packet
  animation đến muộn.
