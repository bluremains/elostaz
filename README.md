# ELOSTAZ

نظام لإدارة المدرسين الخصوصيين: المدرس بيسجل بياناته ومواده ومناطقه ومواعيده، وولي الأمر بيدوّر ويحجز حصة.

## التشغيل

1. شغّلي `database/schema.sql` في MySQL Workbench (مرة واحدة).
2. انسخي `config.properties.example` باسم `config.properties` واكتبي الباسورد بتاعك (الملف ده مش بيترفع على GitHub).
3. اعملي Reload للـ Maven في IntelliJ.
4. شغّلي `DemoData` مرة واحدة عشان تتعمل بيانات تجريبية (ولي أمر وأربع مدرسين).
5. شغّلي `Launcher` (مش `App`) علشان يفتح البرنامج.

حسابات التجربة (الباسورد للكل `123456`): `parent1` ولي أمر، و`ahmed` و`sara` و`khaled` و`nour` مدرسين.

## الهيكل

```
ui        الشاشات (JavaFX)        تعرض وتاخد الإدخال بس
service   المنطق والتحقق          القواعد: مين يقدر يعمل إيه
dao       الكلام مع الداتابيز     SQL فقط
model     الكلاسات                User, Teacher, Booking ...
util      أدوات                   DBConnection, PasswordHasher, Validator, Session
```

القاعدة: الشاشة تكلّم الـ Service، والـ Service يكلّم الـ DAO، والـ DAO يكلّم الداتابيز. مفيش شاشة بتكتب SQL.

## مين عمل إيه

| الجزء | الملفات |
|---|---|
| الدخول والتسجيل | `AuthService`, `UserDAO`, `ParentDAO`, `LoginView`, `RegisterView` |
| ملف المدرس | `TeacherDAO`, `TeacherService`, تبويب My profile |
| المواد والمناطق | `SubjectDAO`, `AreaDAO`, `GradeLevelDAO`, `CatalogService`, تبويب Subjects and areas |
| المواعيد | `AvailabilityDAO`, تبويب Free times |
| البحث | `SearchDAO`, تبويب Find a tutor |
| الحجز | `BookingDAO`, `BookingService`, `BookDialog`, `BookingTable` |

## رحلة الحجز (للشرح)

1. ولي الأمر يدوس Book a lesson على مدرس.
2. `BookDialog` يعرض مواد المدرس وأيام فاضية (من جدول availability).
3. لما يختار اليوم، `BookingService.freeSlots` يطلّع الساعات الفاضلة ويشيل المحجوز.
4. `BookingService.book` يتأكد من الساعة تاني ويحفظ الحجز بحالة PENDING.
5. المدرس يشوفه في Booking requests ويدوس Confirm أو Reject.
6. الداتابيز نفسها بتمنع حجزين لنفس المدرس في نفس الميعاد (UNIQUE على teacher_id, lesson_date, start_time).
