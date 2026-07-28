# Theo dõi chuyển fix client về server

## Lấy súng: server giữ thứ tự CMD 22/84

Client cũ gửi yêu cầu bắn ngay khi vừa bắt đầu bốn frame lấy súng. Server local
trả kết quả trong cùng nhịp có thể làm `CPlayer.shoot()` đổi state trong khi
`isGetGun` vẫn bật; `Bullet.update()` sau đó không tiến và CMD 79 không bao giờ
hoàn tất.

Luồng hiện tại:

1. Server xác thực súng, lượt và packet rồi mới mở ngữ cảnh phát bắn người chơi.
2. Damage, va chạm và state trận vẫn được server tính và khóa ngay.
3. CMD 22/84 và các packet HP/đổi lượt đứng sau nó được giữ trong hàng đợi của
   từng phiên trong tối thiểu 420 ms.
4. Hết mốc, server xả hàng đợi theo đúng thứ tự. Phát bắn của boss/bot không bị
   chèn độ trễ lấy súng.
5. Client không gửi và server không tin bất kỳ cờ `isGetGun` nào.

Plugin PC không còn tự gán `isGetGun=false` hay `isRemoveGun=false`. Test hồi quy
bắt buộc xác nhận CMD 22 đứng đầu hàng, CMD 51 không vượt lên trước và thời gian
giữ không nhỏ hơn mốc cấu hình.

Mục tiêu: client chỉ nhận dữ liệu để hiển thị. Tọa độ đích, đường đi hợp lệ,
thứ tự hành động, damage và kết thúc lượt đều do server quyết định.

## Boss Rùa: vòng đời di chuyển do server điều phối

Client cũ dùng `CMD21` để chạy hoạt ảnh BigBoss Rùa type 1. Giao thức server
gốc gửi thêm một `CMD21` cùng tọa độ sau khi hoạt ảnh đến đích, rồi mới gửi
packet bắn hoặc skill. Gói thứ hai chốt vòng chờ nội bộ của client cũ.

Luồng hiện tại:

1. Server tính toàn bộ đường đi, va chạm và tọa độ đích authoritative.
   Trước khi phát packet, tọa độ chân được chuẩn hóa về pixel nền native
   (`480 -> 481` trên nền thấp map 54) để BigBoss có thể kết thúc `isMove`.
2. Server gửi `CMD21` thứ nhất để bắt đầu hoạt ảnh native.
3. Server chờ thời gian do `DiChuyenBossRua.tinhThoiGianHoatAnhMs()` tính.
4. Callback kiểm tra lại trận, mã phiên lượt, slot, boss còn sống và tọa độ đích.
5. Server gửi `CMD21` thứ hai cùng tọa độ để chốt hoạt ảnh.
6. Server mới gửi packet bắn hoặc skill và tiếp tục quản lý lượt.

Không nhận xác nhận tọa độ hay trạng thái hoàn tất từ client. Gói chốt chỉ phục
vụ hiển thị và không thay đổi quyền quyết định gameplay của server.

Client PC và JAR không còn hook vòng đời di chuyển riêng cho `BigBoss.update`.
Plugin PC tiếp tục chỉ phục vụ auto-aim/hiển thị.

## Cảm tử: CMD -64 hoàn tất bằng cơ chế native

Cảm tử ở 2 Tòa Tháp và Bao Vây dùng `CMD -64`. Client native tự nội suy và gọi
`mNotify()` khi chạm đúng tọa độ đích. Server không nhận tọa độ hay xác nhận hoàn tất
từ client; server chỉ điều phối thời điểm phát hành động kế tiếp.

Luồng hiện tại:

1. Server tính waypoint, va chạm, giới hạn 36 px và mép bản đồ.
2. Waypoint được chuẩn hóa theo nền và hitbox chân của client native.
3. Ngay trước khi phát `CMD -64`, server kiểm tra lại waypoint bằng
   `laDiemDenClientCoTheKetThuc`. Waypoint không hợp lệ bị bỏ và lượt được chuyển an toàn.
4. Server chờ 650 ms cho hoạt ảnh native hoàn tất rồi mới nổ hoặc chuyển lượt.

JAR không còn gọi `BigBossMoveWaitFix.tick()` trong `CPlayer.update()` và không còn
class `BigBossMoveWaitFix`. Không có timeout phía client tự sửa tọa độ, state hoặc mở
khóa luồng packet; server chịu trách nhiệm không phát đích không thể hoàn tất.

## Regression test bắt buộc

- Ngay sau khi bắt đầu di chuyển chỉ có một `CMD21` và chưa có packet tung chiêu.
- Sau thời gian hoạt ảnh có đúng hai `CMD21`; gói chốt đứng trước packet bắn/skill.
- Không có `CMD53`, `CMD-64` hoặc `CMD93` trong luồng đi bộ của Rùa.
- Callback của phiên lượt cũ không được chốt di chuyển, tung chiêu hoặc đổi lượt.
- Rùa map 54 và hai Rùa map 58 đều phải di chuyển rồi mới tung chiêu.
- Kiểm tra thực tế trên PC và JAR: di chuyển mượt, bắn đúng một lần và không kẹt đọc packet.
- Mọi waypoint Cảm tử sinh ra ở map 50/51 phải hoàn tất được bằng `CPlayer` native.
- JAR không được chứa class hoặc lời gọi `BigBossMoveWaitFix`.

## Quy tắc chuyển các fix client khác

Trước khi xóa hook hiển thị phải lần đủ chuỗi: packet handler, state client,
animation, callback kết thúc và packet tiếp theo. Logic gameplay phải authoritative
ở server; nếu cần tương thích client cũ, hành vi đó phải được đặt tên rõ, tập trung
trong lớp giao thức và có test thứ tự packet.
