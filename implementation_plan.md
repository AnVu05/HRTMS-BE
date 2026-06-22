# Kế hoạch thực hiện: Thêm logic gửi thông báo cho Trọng tài khi tạo Cuộc đua mới

Báo cáo chi tiết các phần cần làm rõ (Open Questions) và đề xuất hướng xử lý kỹ thuật đối với chức năng gửi thông báo cho trọng tài khi tạo cuộc đua đơn (`createSingleRace`).

## User Review Required

> [!IMPORTANT]
> Cần chốt các câu hỏi làm rõ dưới đây về luồng nghiệp vụ trước khi tiến hành viết code thực thi để đảm bảo tính đồng bộ của hệ thống.

## Open Questions

Dưới đây là các thông tin chưa rõ ràng từ yêu cầu của a Lead và thiết kế hiện tại:

### 1. Thông tin về Địa điểm thi đấu (Track) lấy từ đâu?
* **Hiện trạng**: Màn hình hiển thị cho trọng tài yêu cầu hiển thị trường **Track** (Ví dụ: `Sector 4 - Grand Track`). Tuy nhiên, trong cả thực thể [Race](file:///d:/DEMO/clone/src/main/java/com/swp/hrtms/hrtmsbe/entity/Race.java) và [Tournament](file:///d:/DEMO/clone/src/main/java/com/swp/hrtms/hrtmsbe/entity/Tournament.java) hiện tại đều **không** có thuộc tính/cột nào lưu trữ thông tin địa điểm hay đường đua.
* **Câu hỏi**: Chúng ta nên xử lý thông tin này như thế nào?
  * *Phương án A*: Bổ sung thuộc tính `track` (hoặc `location`) vào thực thể `Race` (sẽ cần thay đổi cơ sở dữ liệu và DTO `SingleRaceCreateRequest`).
  * *Phương án B*: Lưu tạm/sinh ngẫu nhiên (Mock) thông tin đường đua này vào cột `content` của bảng `notifications`.
  * *Phương án C*: Tournament có trường mô tả `description`, có thể parse hoặc lấy tạm từ đó?

### 2. Người gửi thông báo (Sender) là ai?
* **Hiện trạng**: Hàm `createSingleRace` được gọi bởi Admin để tạo lịch đua. Tuy nhiên, request payload [SingleRaceCreateRequest](file:///d:/DEMO/clone/src/main/java/com/swp/hrtms/hrtmsbe/dto/request/SingleRaceCreateRequest.java) hiện tại **không chứa** thông tin `adminId` của người tạo, và hệ thống cũng chưa tích hợp cơ chế lấy `User` hiện tại từ Security Context trong tầng Service (thường lấy ở Controller hoặc qua SecurityContextHolder).
* **Câu hỏi**: Người gửi (`sender`) trong bảng `Notification` nên là:
  * *Phương án A*: Lấy `admin` liên kết trực tiếp với Tournament (`tournament.getAdmin()`).
  * *Phương án B*: Lấy từ `SecurityContextHolder.getContext().getAuthentication()` để xác định Admin đang đăng nhập.
  * *Phương án C*: Để `sender` là `null` (hệ thống tự động gửi).

### 3. Định nghĩa loại thông báo (Notification Type) mới
* **Hiện trạng**: Dự án hiện chỉ có các loại thông báo: `VERIFY_CERTIFICATE`, `ACCEPT_CERTIFICATE`, `REJECT_CERTIFICATE`.
* **Đề xuất**: Cần thống nhất thêm một loại thông báo mới, ví dụ: `REFEREE_INVITATION` để hệ thống phân biệt và truy vấn riêng cho màn hình "Pending Invitations" của Trọng tài.

### 4. Thiết kế các API còn thiếu để phục vụ màn hình Trọng tài (Ảnh 4)
* **Hiện trạng**: Swagger hiện tại chỉ có duy nhất một API lấy thông báo của Jockey. Chưa hề có các API hỗ trợ màn hình của Trọng tài như trong ảnh 4.
* **Câu hỏi**: A Lead có yêu cầu chúng ta phát triển toàn bộ các API này không? Nếu có, đề xuất các endpoints mới:
  * **GET** `/api/v1/notifications/referees/{refereeId}/invitations`: Lấy danh sách lời mời đang chờ (`Pending Invitations`) của Trọng tài (trả về thông tin Race, Tournament, Date, Track).
  * **PUT** `/api/v1/notifications/referees/{refereeId}/invitations/{notificationId}/respond`: Phản hồi lời mời (Accept/Decline). Khi Trọng tài **Accept**, trạng thái cuộc đua (`Race.status`) sẽ chuyển từ `PENDING_REFEREE` thành `PUBLISHED` (hoặc trạng thái phù hợp); nếu **Decline**, trạng thái đổi thành `REJECTED` hoặc reset `referee_id` về null.
  * **GET** `/api/v1/referees/{refereeId}/scheduled-races`: Lấy danh sách lịch thi đấu đã xác nhận (`My Scheduled Races`) của Trọng tài đó.

---

## Proposed Changes

Dưới đây là đề xuất các thay đổi dự kiến nếu thực thi giải pháp đồng bộ:

### [Core Backend - Notification Flow]

#### [MODIFY] [RaceServiceImpl.java](file:///d:/DEMO/clone/src/main/java/com/swp/hrtms/hrtmsbe/service/impl/RaceServiceImpl.java)
* Thêm logic gửi thông báo ở hàm `createSingleRace`:
  ```java
  if (race.getReferee() != null) {
      // 1. Tạo Notification mới
      Notification notification = Notification.builder()
              .sender(tournament.getAdmin()) // Tạm thời dùng admin của tournament
              .title("Race Referee Invitation")
              .content(String.format("You are invited to referee the race '%s' in tournament '%s' on %s.", 
                      race.getName(), tournament.getName(), race.getDate()))
              .type("REFEREE_INVITATION")
              .createdAt(LocalDateTime.now())
              .build();
      notificationRepository.save(notification);

      // 2. Tạo NotificationRecipient để liên kết tới Referee
      NotificationRecipient recipient = NotificationRecipient.builder()
              .notification(notification)
              .recipient(race.getReferee())
              .status("None") // Đang ở trạng thái chờ Accept/Decline
              .build();
      notificationRecipientRepository.save(recipient);
  }
  ```

#### [NEW] [Các API mới phục vụ Trọng tài (nếu được yêu cầu)]
* Thêm các Service & Controller methods để Trọng tài tương tác với Lời mời (Accept / Decline) và xem lịch Scheduled Races.

---

## Verification Plan

### Automated Tests
* Viết Integration Test cho hàm `createSingleRace` để verify sau khi tạo thành công một cuộc đua có gán trọng tài, cơ sở dữ liệu xuất hiện bản ghi `Notification` và `NotificationRecipient` tương ứng với Referee ID đó.

### Manual Verification
* Sử dụng H2 hoặc SQL Server Database client để truy vấn bảng `notifications` và `notification_recipients` ngay sau khi gọi API `POST /api/v1/races` tạo cuộc đua đơn.
