# AttendanceCheck

## 설명
출석 보상 시스템을 지원하는 마인크래프트 Spigot 플러그인입니다.  
관리자는 GUI를 통해 보상을 설정할 수 있으며, 유저는 매일 출석 보상을 수령할 수 있습니다.  
출석 주기 설정, 기록 초기화, 출석 정보 조회 등 다양한 명령어도 제공합니다.

---

## 주요 기능
- `/출석` GUI로 간편하게 출석 체크 가능
- 출석 주기 설정 지원 (`daily`, `weekly`, `monthly`)
- 관리자용 출석 보상 설정 GUI
- 인벤토리 공간이 부족할 경우 보상 차단
- 출석 기록 초기화 및 조회 명령어
- 탭 자동 완성 기능 지원

---

## 명령어 목록

### `/attendance`
> 일반 유저가 출석 보상을 수령하는 GUI를 엽니다.

---

### `/attendanceadmin`
> 관리자 명령어입니다. 인자 없이 입력하면 모든 명령어 목록이 표시됩니다.

- `/attendanceadmin reward`  
  → 출석 보상 설정 GUI를 엽니다.

- `/attendanceadmin reload`  
  → 설정 파일들을 다시 로드합니다.

- `/attendanceadmin clear <닉네임>`  
  → 해당 유저의 출석 기록을 초기화합니다.

- `/attendanceadmin date <daily|weekly|monthly>`  
  → 출석 주기를 설정합니다.

- `/attendanceadmin info <닉네임>`  
  → 유저의 출석 정보를 확인합니다.

---

## 설정 파일

### `/plugins/AttendanceCheck/messages.yml`
> 모든 메시지 문구를 수정할 수 있습니다. `&` 색상 코드 사용 가능.

### `/plugins/AttendanceCheck/gui.yml`
> 출석 GUI의 제목, 슬롯 수, 버튼 설정 등을 제어합니다.

### `/plugins/AttendanceCheck/attendance.yml`
> 출석 주기 설정(`date`) 및 보상 아이템 설정(`rewards`)을 포함합니다.

### `/plugins/AttendanceCheck/logs.yml`
> 각 유저의 출석 기록을 저장합니다.

---

## 예시 설정 (`messages.yml`)

```yaml
prefix: "&6[출석]&f "

attendance:
  already: "{prefix}&c이미 출석을 완료하셨습니다!"
  success: "{prefix}&a출석 보상을 수령했습니다!"
  not-available: "{prefix}&c아직 출석할 수 없습니다. 남은 시간: &e{time}"

admin:
  cleared: "{prefix}&a{target}님의 출석 기록을 초기화했습니다."
  not-found: "{prefix}&c{target}님의 출석 기록을 찾을 수 없습니다."
  set-date: "{prefix}&a출석 주기를 '{mode}'(으)로 설정했습니다."
  set-date-fail: "{prefix}&c출석 주기는 daily, weekly, monthly 중 하나여야 합니다."
  info:
    - "{prefix}&e{name}님의 출석 정보"
    - "&7- 마지막 출석일: &b{date}"
    - "&7- 출석 주기: &a{mode}"
```

---

## 예시 설정 (`gui.yml`)

```yaml
gui_name: "&a출석체크"
gui_slot: 3

items:
  attendance_button:
    slot: 13
    material: PAPER
    name: "&e출석하기!"
    custom_model_data: 0
    lore:
      - ""
      - "&e{player}&f님. 저를 클릭하여 출석 체크하세요!"
      - ""

  waiting_button:
    slot: 13
    material: BARRIER
    name: "&c출석 불가"
    custom_model_data: 0
    lore:
      - ""
      - "&7남은 시간: &c{date}"
      - ""
```

---

개발자 연락처 :
https://discord.gg/MjKbVwjGeF
